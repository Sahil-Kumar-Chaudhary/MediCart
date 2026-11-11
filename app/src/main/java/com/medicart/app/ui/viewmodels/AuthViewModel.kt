package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val mobileNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false,
    val isVerified: Boolean = false
)

class AuthViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onMobileNumberChanged(number: String) {
        val clean = number.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(mobileNumber = clean, errorMessage = null)
    }

    fun onOtpChanged(otp: String) {
        val clean = otp.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(otpCode = clean, errorMessage = null)
    }

    fun requestOtp(onSuccess: (String) -> Unit) {
        val mobile = _uiState.value.mobileNumber
        if (mobile.length != 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number.")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.requestOtp(mobile)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isOtpSent = true
                )
                onSuccess(mobile)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to request OTP. Please try again."
                )
            }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val mobile = _uiState.value.mobileNumber
        val otp = _uiState.value.otpCode
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a complete 6-digit verification code.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.verifyOtp(mobile, otp)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isVerified = true
                )
                onSuccess()
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Invalid OTP code."
                )
            }
        }
    }
}
