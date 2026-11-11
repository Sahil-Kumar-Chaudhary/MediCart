package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.Category
import com.medicart.app.model.DeliveryAddress
import com.medicart.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val currentAddress: DeliveryAddress? = null
)

class HomeViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val cartItemCount: StateFlow<Int> = repository.cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isOffline: StateFlow<Boolean> = repository.isOffline

    init {
        loadHomeData()
        observeRepositoryState()
    }

    private fun observeRepositoryState() {
        viewModelScope.launch {
            repository.isOffline.collect { offline ->
                _uiState.value = _uiState.value.copy(isOffline = offline)
            }
        }
        viewModelScope.launch {
            repository.currentAddress.collect { address ->
                _uiState.value = _uiState.value.copy(currentAddress = address)
            }
        }
    }

    fun loadHomeData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val categories = repository.getCategories()
            val productsResult = repository.getProducts()
            val products = productsResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                categories = categories,
                featuredProducts = products,
                isLoading = false,
                currentAddress = repository.getDefaultAddress()
            )
        }
    }

    fun toggleOfflineDemo() {
        val current = repository.isOffline.value
        repository.setOfflineMode(!current)
    }

    fun addToCart(product: Product): Result<Unit> {
        return repository.addToCart(product, 1)
    }
}
