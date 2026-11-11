package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val summary: CartSummary = CartSummary(emptyList()),
    val deliveryAddress: DeliveryAddress? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.UPI_DEMO,
    val isSubmitting: Boolean = false,
    val isOffline: Boolean = false,
    val orderConfirmed: Order? = null,
    val errorMessage: String? = null
)

class CheckoutViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.cartItems.collect { items ->
                _uiState.value = _uiState.value.copy(
                    cartItems = items,
                    summary = CartSummary(items)
                )
            }
        }
        viewModelScope.launch {
            repository.currentAddress.collect { address ->
                _uiState.value = _uiState.value.copy(deliveryAddress = address)
            }
        }
        viewModelScope.launch {
            repository.isOffline.collect { offline ->
                _uiState.value = _uiState.value.copy(isOffline = offline)
            }
        }
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(paymentMethod = method)
    }

    fun submitOrder(
        onSuccess: (Order) -> Unit,
        onError: (String) -> Unit
    ) {
        val address = _uiState.value.deliveryAddress ?: repository.getDefaultAddress()
        val paymentMethod = _uiState.value.paymentMethod

        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.submitOrder(address, paymentMethod)
            result.onSuccess { order ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    orderConfirmed = order
                )
                onSuccess(order)
            }.onFailure { throwable ->
                val errorMsg = throwable.message ?: "Failed to place order. Please try again."
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
            }
        }
    }
}
