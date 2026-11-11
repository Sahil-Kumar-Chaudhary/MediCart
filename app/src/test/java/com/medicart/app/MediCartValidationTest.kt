package com.medicart.app

import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.model.AddressType
import com.medicart.app.model.CartSummary
import com.medicart.app.model.DeliveryAddress
import com.medicart.app.model.PaymentMethod
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MediCartValidationTest {

    private lateinit var repository: MockPharmacyRepository

    @Before
    fun setup() {
        // Pure in-memory repository for unit test isolation
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
        val products = repository.getProducts().getOrThrow()
        val outOfStockProduct = products.find { !it.inStock }
        assertNotNull("Should have out of stock item in catalogue", outOfStockProduct)

        val result = repository.addToCart(outOfStockProduct!!, 1)
        assertTrue("Adding out of stock item must fail", result.isFailure)
    }

    @Test
    fun testCannotAddExpiredProduct() = runBlocking {
        val products = repository.getProducts().getOrThrow()
        val expiredProduct = products.find { it.isExpired }
        assertNotNull("Should have expired item in catalogue", expiredProduct)

        val result = repository.addToCart(expiredProduct!!, 1)
        assertTrue("Adding expired item must fail safety check", result.isFailure)
    }

    @Test
    fun testCartQuantityLimit() = runBlocking {
        val products = repository.getProducts().getOrThrow()
        val availableProduct = products.first { it.isAvailable }
        repository.clearCart()

        val validAdd = repository.addToCart(availableProduct, 4)
        assertTrue(validAdd.isSuccess)

        // Adding 2 more would exceed 5 limit
        val exceededAdd = repository.addToCart(availableProduct, 2)
        assertTrue("Exceeding 5 packs limit per item must fail", exceededAdd.isFailure)
    }

    @Test
    fun testOrderPlacementGeneratesConfirmedReference() = runBlocking {
        val products = repository.getProducts().getOrThrow()
        val availableProduct = products.first { it.isAvailable }
        repository.clearCart()
        repository.addToCart(availableProduct, 2)

        val address = repository.getDefaultAddress()
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)

        assertTrue("Order should be placed successfully", orderResult.isSuccess)
        val order = orderResult.getOrThrow()
        assertTrue("Order reference must follow MediCart pattern", order.orderReference.startsWith("#MC-2026-"))
        assertEquals("Cart should be cleared after confirmed backend order", 0, repository.cartItems.value.size)
        assertTrue("Orders list must contain the placed order", repository.orders.value.any { it.orderReference == order.orderReference })
    }

    @Test
    fun testCannotSubmitEmptyCart() = runBlocking {
        repository.clearCart()
        val address = repository.getDefaultAddress()
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)

        assertTrue("Submitting empty cart must fail", orderResult.isFailure)
    }

    @Test
    fun testOfflineOrderPlacementIsRejectedWithoutFalseSuccess() = runBlocking {
        val products = repository.getProducts().getOrThrow()
        val availableProduct = products.first { it.isAvailable }
        repository.clearCart()
        repository.addToCart(availableProduct, 1)

        // Simulate network disconnect / offline state
        repository.setOfflineMode(true)
        assertTrue("Repository must report offline", repository.isOffline.value)

        val address = repository.getDefaultAddress()
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)

        // Order submission must fail cleanly without false success
        assertTrue("Offline order submission must be rejected", orderResult.isFailure)
        assertEquals("Cart must not be cleared on rejected offline submission", 1, repository.cartItems.value.size)
    }

    @Test
    fun testSuccessfulReconnectEnablesOrderPlacement() = runBlocking {
        val products = repository.getProducts().getOrThrow()
        val availableProduct = products.first { it.isAvailable }
        repository.clearCart()
        repository.addToCart(availableProduct, 1)

        // Simulate offline then reconnected
        repository.setOfflineMode(true)
        repository.setOfflineMode(false)
        assertFalse("Repository must report online", repository.isOffline.value)

        val address = repository.getDefaultAddress()
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)

        assertTrue("Order should succeed once reconnected", orderResult.isSuccess)
        assertEquals("Cart must be cleared after online confirmation", 0, repository.cartItems.value.size)
    }

    @Test
    fun testOtpAuthenticationFlowAndInvalidCodeRejection() = runBlocking {
        // Test invalid OTP (less than 6 digits)
        val invalidShort = repository.verifyOtp("9876543210", "123")
        assertTrue("Short OTP must fail", invalidShort.isFailure)
        assertFalse(repository.isAuthenticated.value)

        // Test non-digit OTP
        val invalidChars = repository.verifyOtp("9876543210", "12AB56")
        assertTrue("Alphanumeric OTP must fail", invalidChars.isFailure)
        assertFalse(repository.isAuthenticated.value)

        // Test valid 6-digit OTP
        val validOtp = repository.verifyOtp("9876543210", "487210")
        assertTrue("Valid 6-digit OTP must succeed", validOtp.isSuccess)
        assertTrue("User must be marked as authenticated", repository.isAuthenticated.value)

        // Test logout
        repository.logout()
        assertFalse("User must be logged out after logout() call", repository.isAuthenticated.value)
    }

    @Test
    fun testCartPriceCalculationsAndIndianRupeeValues() = runBlocking {
        val products = repository.getProducts().getOrThrow()
        val p1 = products[0] // Paracare 650mg: price ₹34.0, mrp ₹38.0
        val p2 = products[1] // Cetriz-L 10mg: price ₹22.0, mrp ₹28.0

        repository.clearCart()
        repository.addToCart(p1, 2) // 34 * 2 = 68
        repository.addToCart(p2, 1) // 22 * 1 = 22

        val items = repository.cartItems.value
        val summary = CartSummary(items)

        assertEquals("Subtotal must equal sum of price * qty", 90.0, summary.itemTotal, 0.01)
        assertEquals("Delivery fee is ₹30 standard", 30.0, summary.deliveryFee, 0.01)
        assertEquals("Packaging fee is ₹14 standard", 14.0, summary.packagingFee, 0.01)
        assertEquals("Final total must include subtotal + delivery + packaging", 134.0, summary.finalTotal, 0.01)
    }
}
