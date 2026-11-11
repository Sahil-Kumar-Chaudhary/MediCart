package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val product: Product? = null,
    val selectedQuantity: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddedToCart: Boolean = false
)

class ProductDetailViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    val cartItemCount: StateFlow<Int> = repository.cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun loadProduct(productId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.getProductById(productId)
            result.onSuccess { product ->
                _uiState.value = _uiState.value.copy(
                    product = product,
                    isLoading = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message ?: "Medicine not found."
                )
            }
        }
    }

    fun incrementQuantity() {
        val current = _uiState.value.selectedQuantity
        if (current < 5) {
            _uiState.value = _uiState.value.copy(selectedQuantity = current + 1)
        }
    }

    fun decrementQuantity() {
        val current = _uiState.value.selectedQuantity
        if (current > 1) {
            _uiState.value = _uiState.value.copy(selectedQuantity = current - 1)
        }
    }

    fun addToCart(): Result<Unit> {
        val prod = _uiState.value.product ?: return Result.failure(IllegalStateException("No product loaded"))
        val result = repository.addToCart(prod, _uiState.value.selectedQuantity)
        if (result.isSuccess) {
            _uiState.value = _uiState.value.copy(isAddedToCart = true)
        }
        return result
    }
}
