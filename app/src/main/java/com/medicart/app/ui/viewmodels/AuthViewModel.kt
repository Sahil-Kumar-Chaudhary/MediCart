package com.medicart.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicart.app.data.PharmacyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    // Login State
    val loginMobileNumber: String = "9876543210",
    val loginPassword: String = "",
    val rememberMe: Boolean = true,
    val isLoginPasswordVisible: Boolean = false,

    // Registration State
    val registerName: String = "",
    val registerMobileNumber: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val registerTermsAccepted: Boolean = false,
    val isRegisterPasswordVisible: Boolean = false,
    val isRegisterConfirmPasswordVisible: Boolean = false,

    // Forgot Password & Reset State
    val forgotMobileNumber: String = "",
    val forgotOtpCode: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmNewPasswordVisible: Boolean = false,
    val isPasswordResetSuccess: Boolean = false,

    // General Status
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isOtpSent: Boolean = false,
    val isVerified: Boolean = false,
    val isAuthenticated: Boolean = false
) {
    val isLoginFormValid: Boolean
        get() = loginMobileNumber.length == 10 && loginPassword.isNotBlank()

    val hasMinLength: Boolean get() = registerPassword.length >= 8
    val hasUppercase: Boolean get() = registerPassword.any { it.isUpperCase() }
    val hasLowercase: Boolean get() = registerPassword.any { it.isLowerCase() }
    val hasDigit: Boolean get() = registerPassword.any { it.isDigit() }
    val isPasswordStrong: Boolean get() = hasMinLength && hasUppercase && hasLowercase && hasDigit

    val isRegisterFormValid: Boolean
        get() = registerName.trim().length >= 2 &&
                registerMobileNumber.length == 10 &&
                isPasswordStrong &&
                registerPassword == registerConfirmPassword &&
                registerTermsAccepted

    val isNewPasswordStrong: Boolean
        get() = newPassword.length >= 8 &&
                newPassword.any { it.isUpperCase() } &&
                newPassword.any { it.isLowerCase() } &&
                newPassword.any { it.isDigit() }

    val isResetFormValid: Boolean
        get() = isNewPasswordStrong && newPassword == confirmNewPassword
}

class AuthViewModel(
    private val repository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --- Login Handlers ---
    fun onLoginMobileChanged(number: String) {
        val clean = number.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(loginMobileNumber = clean, errorMessage = null)
    }

    fun onLoginPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(loginPassword = password, errorMessage = null)
    }

    fun toggleLoginPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isLoginPasswordVisible = !_uiState.value.isLoginPasswordVisible)
    }

    fun toggleRememberMe() {
        _uiState.value = _uiState.value.copy(rememberMe = !_uiState.value.rememberMe)
    }

    fun login(onSuccess: () -> Unit) {
        val mobile = _uiState.value.loginMobileNumber
        val password = _uiState.value.loginPassword

        if (mobile.length != 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number.")
            return
        }
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password cannot be empty.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.login(mobile, password)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    errorMessage = null
                )
                onSuccess()
            }.onFailure { error: Throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Invalid mobile number or password."
                )
            }
        }
    }

    // --- Registration Handlers ---
    fun onRegisterNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(registerName = name, errorMessage = null)
    }

    fun onRegisterMobileChanged(number: String) {
        val clean = number.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(registerMobileNumber = clean, errorMessage = null)
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(registerPassword = password, errorMessage = null)
    }

    fun onRegisterConfirmPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(registerConfirmPassword = password, errorMessage = null)
    }

    fun toggleRegisterPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isRegisterPasswordVisible = !_uiState.value.isRegisterPasswordVisible)
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isRegisterConfirmPasswordVisible = !_uiState.value.isRegisterConfirmPasswordVisible)
    }

    fun toggleRegisterTerms() {
        _uiState.value = _uiState.value.copy(registerTermsAccepted = !_uiState.value.registerTermsAccepted)
    }

    fun requestRegistrationOtp(onSuccess: (mobile: String, name: String, password: String) -> Unit) {
        val state = _uiState.value
        if (!state.isRegisterFormValid) {
            val errorMsg = when {
                state.registerName.trim().length < 2 -> "Please enter your full name."
                state.registerMobileNumber.length != 10 -> "Please enter a valid 10-digit mobile number."
                !state.isPasswordStrong -> "Password must be at least 8 characters with uppercase, lowercase, and a number."
                state.registerPassword != state.registerConfirmPassword -> "Passwords do not match."
                !state.registerTermsAccepted -> "Please accept Terms & Conditions to proceed."
                else -> "Please verify all registration fields."
            }
            _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.registerRequestOtp(
                name = state.registerName.trim(),
                phoneNumber = state.registerMobileNumber,
                password = state.registerPassword
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isOtpSent = true
                )
                onSuccess(state.registerMobileNumber, state.registerName.trim(), state.registerPassword)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to send registration OTP."
                )
            }
        }
    }

    fun verifyRegistrationOtp(
        mobile: String,
        name: String,
        password: String,
        otp: String,
        onSuccess: () -> Unit
    ) {
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a complete 6-digit OTP code.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.registerVerifyOtp(mobile, otp, name, password)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isVerified = true,
                    isAuthenticated = true
                )
                onSuccess()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Invalid OTP verification code."
                )
            }
        }
    }

    // --- Forgot Password & Reset Handlers ---
    fun onForgotMobileChanged(number: String) {
        val clean = number.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(forgotMobileNumber = clean, errorMessage = null)
    }

    fun onForgotOtpChanged(otp: String) {
        val clean = otp.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(forgotOtpCode = clean, errorMessage = null)
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password, errorMessage = null)
    }

    fun onConfirmNewPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(confirmNewPassword = password, errorMessage = null)
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isNewPasswordVisible = !_uiState.value.isNewPasswordVisible)
    }

    fun toggleConfirmNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmNewPasswordVisible = !_uiState.value.isConfirmNewPasswordVisible)
    }

    fun requestPasswordResetOtp(onSuccess: (mobile: String) -> Unit) {
        val mobile = _uiState.value.forgotMobileNumber
        if (mobile.length != 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.forgotPasswordRequestOtp(mobile)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess(mobile)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to send reset code."
                )
            }
        }
    }

    fun resetPassword(mobile: String, otp: String, onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isResetFormValid) {
            val errorMsg = when {
                !state.isNewPasswordStrong -> "Password must be at least 8 characters with uppercase, lowercase, and a number."
                state.newPassword != state.confirmNewPassword -> "Passwords do not match."
                else -> "Please verify password fields."
            }
            _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.forgotPasswordReset(mobile, otp, state.newPassword)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isPasswordResetSuccess = true,
                    successMessage = "Password reset successfully! Please login with your new password.",
                    newPassword = "",
                    confirmNewPassword = "",
                    forgotOtpCode = ""
                )
                onSuccess()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Password reset failed. Please check the code and try again."
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }
}
