package com.medicart.app

import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.model.AddressType
import com.medicart.app.model.DeliveryAddress
import com.medicart.app.model.PaymentMethod
import com.medicart.app.model.Product
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MediCartValidationTest {

    private lateinit var repository: MockPharmacyRepository

    @Before
    fun setup() {
        repository = MockPharmacyRepository()
    }

    @Test
    fun testIndianPinCodeValidation() {
        val validAddress = DeliveryAddress(
            id = "1",
            recipientName = "Sahil Kumar",
            mobileNumber = "9876543210",
            flatNumber = "Flat 402",
            streetLocality = "Indiranagar",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560038"
        )
        assertTrue("560038 should be a valid Indian PIN code", validAddress.isValidPinCode)

        val shortPin = validAddress.copy(pinCode = "5600")
        assertFalse("4 digits PIN code should be invalid", shortPin.isValidPinCode)

        val alphaPin = validAddress.copy(pinCode = "56003A")
        assertFalse("Alphanumeric PIN code should be invalid", alphaPin.isValidPinCode)

        val zeroLeadingPin = validAddress.copy(pinCode = "056038")
        assertFalse("Indian PIN code starting with 0 should be invalid", zeroLeadingPin.isValidPinCode)
    }

    @Test
    fun testCannotAddOutOfStockProduct() = runBlocking {
        val outOfStockProduct = repository.getProducts().find { !it.inStock }
        assertNotNull("Should have out of stock item in catalogue", outOfStockProduct)

        val result = repository.addToCart(outOfStockProduct!!, 1)
        assertTrue("Adding out of stock item must fail", result.isFailure)
    }

    @Test
    fun testCannotAddExpiredProduct() = runBlocking {
        val expiredProduct = repository.getProducts().find { it.isExpired }
        assertNotNull("Should have expired item in catalogue", expiredProduct)

        val result = repository.addToCart(expiredProduct!!, 1)
        assertTrue("Adding expired item must fail safety check", result.isFailure)
    }

    @Test
    fun testCartQuantityLimit() = runBlocking {
        val availableProduct = repository.getProducts().first { it.isAvailable }
        repository.clearCart()

        val validAdd = repository.addToCart(availableProduct, 4)
        assertTrue(validAdd.isSuccess)

        // Adding 2 more would exceed 5 limit
        val exceededAdd = repository.addToCart(availableProduct, 2)
        assertTrue("Exceeding 5 packs limit per item must fail", exceededAdd.isFailure)
    }

    @Test
    fun testOrderPlacementGeneratesConfirmedReference() = runBlocking {
        val availableProduct = repository.getProducts().first { it.isAvailable }
        repository.clearCart()
        repository.addToCart(availableProduct, 2)

        val address = repository.getDefaultAddress()
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)

        assertTrue("Order should be placed successfully", orderResult.isSuccess)
        val order = orderResult.getOrThrow()
        assertTrue("Order reference must follow MediCart pattern", order.orderReference.startsWith("#MC-2026-"))
        assertEquals("Cart should be cleared after confirmed backend order", 0, repository.cartItems.value.size)
    }

    @Test
    fun testCannotSubmitEmptyCart() = runBlocking {
        repository.clearCart()
        val address = repository.getDefaultAddress()
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)

        assertTrue("Submitting empty cart must fail", orderResult.isFailure)
    }
}
