package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Sahil Kumar",
    val mobileNumber: String = "+91 98765 43210",
    val isOfflineDemo: Boolean = false
)

class ProfileViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isOffline.collect { offline ->
                _uiState.value = _uiState.value.copy(isOfflineDemo = offline)
            }
        }
    }

    fun toggleOfflineDemo() {
        val current = repository.isOffline.value
        repository.setOfflineMode(!current)
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLoggedOut()
        }
    }
}
