package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.CartItem
import com.medicart.app.model.CartSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val summary: CartSummary = CartSummary(emptyList()),
    val errorMessage: String? = null
)

class CartViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.cartItems.collect { items ->
                _uiState.value = _uiState.value.copy(
                    items = items,
                    summary = CartSummary(items),
                    errorMessage = null
                )
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val result = repository.updateCartQuantity(productId, quantity)
        result.onFailure {
            _uiState.value = _uiState.value.copy(errorMessage = it.message)
        }
    }

    fun clearCart() {
        repository.clearCart()
    }
}
