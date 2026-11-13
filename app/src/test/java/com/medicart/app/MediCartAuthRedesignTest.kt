package com.medicart.app

import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.data.remote.NetworkModule
import com.medicart.app.data.remote.api.MediCartApiService
import com.medicart.app.ui.viewmodels.AuthUiState
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class MediCartAuthRedesignTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: MediCartApiService
    private lateinit var repository: MockPharmacyRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString()
        val okHttpClient = NetworkModule.createOkHttpClient()
        val retrofit = NetworkModule.createRetrofit(okHttpClient, baseUrl)
        apiService = retrofit.create(MediCartApiService::class.java)

        repository = MockPharmacyRepository(
            context = null,
            injectedApiService = apiService
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // --- 1. Login Form Validation Tests ---
    @Test
    fun test1_LoginFormValidation_Success() {
        val state = AuthUiState(
            loginMobileNumber = "9876543210",
            loginPassword = "Password@123"
        )
        assertTrue("Valid 10-digit number and non-empty password should be valid", state.isLoginFormValid)
    }

    @Test
    fun test2_LoginFormValidation_RejectsInvalidNumber() {
        val shortState = AuthUiState(
            loginMobileNumber = "987654321", // 9 digits
            loginPassword = "Password@123"
        )
        assertFalse("9 digits mobile number must be invalid", shortState.isLoginFormValid)

        val longState = AuthUiState(
            loginMobileNumber = "98765432101", // 11 digits
            loginPassword = "Password@123"
        )
        assertFalse("11 digits mobile number must be invalid", longState.isLoginFormValid)
    }

    @Test
    fun test3_LoginFormValidation_RejectsBlankPassword() {
        val emptyState = AuthUiState(
            loginMobileNumber = "9876543210",
            loginPassword = ""
        )
        assertFalse("Empty password must be invalid", emptyState.isLoginFormValid)

        val blankState = AuthUiState(
            loginMobileNumber = "9876543210",
            loginPassword = "   "
        )
        assertFalse("Blank whitespace password must be invalid", blankState.isLoginFormValid)
    }

    // --- 2. Password Strength Validation Tests ---
    @Test
    fun test4_PasswordStrength_RequiresMinimumLength() {
        val state = AuthUiState(registerPassword = "Pass1")
        assertFalse("Less than 8 chars must fail min length", state.hasMinLength)
        assertFalse("Overall password must not be strong", state.isPasswordStrong)
    }

    @Test
    fun test5_PasswordStrength_RequiresUppercase() {
        val state = AuthUiState(registerPassword = "password123")
        assertFalse("Missing uppercase character must fail", state.hasUppercase)
        assertFalse("Overall password must not be strong", state.isPasswordStrong)
    }

    @Test
    fun test6_PasswordStrength_RequiresLowercase() {
        val state = AuthUiState(registerPassword = "PASSWORD123")
        assertFalse("Missing lowercase character must fail", state.hasLowercase)
        assertFalse("Overall password must not be strong", state.isPasswordStrong)
    }

    @Test
    fun test7_PasswordStrength_RequiresDigit() {
        val state = AuthUiState(registerPassword = "PasswordOnly")
        assertFalse("Missing digit must fail", state.hasDigit)
        assertFalse("Overall password must not be strong", state.isPasswordStrong)
    }

    @Test
    fun test8_PasswordStrength_ValidPasswordMeetsAllCriteria() {
        val state = AuthUiState(registerPassword = "MediCart@2026")
        assertTrue("8+ chars should pass", state.hasMinLength)
        assertTrue("Uppercase should pass", state.hasUppercase)
        assertTrue("Lowercase should pass", state.hasLowercase)
        assertTrue("Digit should pass", state.hasDigit)
        assertTrue("Overall password must be strong", state.isPasswordStrong)
    }

    // --- 3. Registration Form Validation Tests ---
    @Test
    fun test9_RegistrationFormValidation_RejectsMismatchedPasswords() {
        val state = AuthUiState(
            registerName = "Sahil Chaudhary",
            registerMobileNumber = "9876543210",
            registerPassword = "Password@123",
            registerConfirmPassword = "Password@999",
            registerTermsAccepted = true
        )
        assertFalse("Mismatched passwords must invalidate registration form", state.isRegisterFormValid)
    }

    @Test
    fun test10_RegistrationFormValidation_RequiresTermsAccepted() {
        val state = AuthUiState(
            registerName = "Sahil Chaudhary",
            registerMobileNumber = "9876543210",
            registerPassword = "Password@123",
            registerConfirmPassword = "Password@123",
            registerTermsAccepted = false
        )
        assertFalse("Terms not accepted must invalidate registration form", state.isRegisterFormValid)
    }

    @Test
    fun test11_RegistrationFormValidation_Success() {
        val state = AuthUiState(
            registerName = "Sahil Chaudhary",
            registerMobileNumber = "9876543210",
            registerPassword = "Password@123",
            registerConfirmPassword = "Password@123",
            registerTermsAccepted = true
        )
        assertTrue("Valid registration form must pass all checks", state.isRegisterFormValid)
    }

    // --- 4. Reset Password Validation Tests ---
    @Test
    fun test12_ResetFormValidation_RequiresStrongAndMatchingPasswords() {
        val invalidMismatch = AuthUiState(
            newPassword = "Password@123",
            confirmNewPassword = "Different@123"
        )
        assertFalse("Mismatched reset passwords must be invalid", invalidMismatch.isResetFormValid)

        val invalidWeak = AuthUiState(
            newPassword = "weak",
            confirmNewPassword = "weak"
        )
        assertFalse("Weak reset password must be invalid", invalidWeak.isResetFormValid)

        val validReset = AuthUiState(
            newPassword = "NewPassword@2026",
            confirmNewPassword = "NewPassword@2026"
        )
        assertTrue("Strong and matching reset passwords must be valid", validReset.isResetFormValid)
    }

    // --- 5. Repository Live API Integration with MockWebServer ---
    @Test
    fun test13_RepositoryLogin_Success_StoresToken() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": true,
                    "message": "Login successful",
                    "data": {
                        "user": {
                            "id": "u1",
                            "name": "Sahil Chaudhary",
                            "phoneNumber": "9876543210"
                        },
                        "token": "jwt_redesign_token_987654"
                    }
                }""".trimIndent())
        )

        val result = repository.login("9876543210", "Demo@1234")
        assertTrue("Login should succeed", result.isSuccess)
        assertTrue("Repository state should become authenticated", repository.isAuthenticated.value)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(recorded)
        assertEquals("/api/auth/login", recorded?.path)
        assertEquals("POST", recorded?.method)
        val body = recorded!!.body.readUtf8()
        assertTrue(body.contains("9876543210"))
        assertTrue(body.contains("Demo@1234"))
    }

    @Test
    fun test14_RepositoryLogin_InvalidCredentials_ReturnsFailure() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": false,
                    "message": "Invalid mobile number or password."
                }""".trimIndent())
        )

        val result = repository.login("9876543210", "WrongPassword")
        assertTrue("Invalid login must fail", result.isFailure)
        assertFalse("Repository should not be authenticated", repository.isAuthenticated.value)
    }

    @Test
    fun test15_RepositoryRegisterRequestOtp_Success() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": true,
                    "message": "Verification code dispatched.",
                    "data": {
                        "phoneNumber": "9123456780",
                        "expiresInSeconds": 600,
                        "simulatedOtp": "487210"
                    }
                }""".trimIndent())
        )

        val result = repository.registerRequestOtp("Sahil Chaudhary", "9123456780", "Password@123")
        assertTrue("Registration request OTP must succeed", result.isSuccess)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(recorded)
        assertEquals("/api/auth/register/request-otp", recorded?.path)
        assertEquals("POST", recorded?.method)
        val body = recorded!!.body.readUtf8()
        assertTrue(body.contains("Sahil Chaudhary"))
        assertTrue(body.contains("9123456780"))
        assertTrue(body.contains("Password@123"))
    }

    @Test
    fun test16_RepositoryRegisterVerifyOtp_Success_SetsAuthenticated() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": true,
                    "message": "Registration completed successfully.",
                    "data": {
                        "user": {
                            "id": "u2",
                            "name": "Sahil Chaudhary",
                            "phoneNumber": "9123456780"
                        },
                        "token": "jwt_registered_token_abc123"
                    }
                }""".trimIndent())
        )

        val result = repository.registerVerifyOtp("9123456780", "487210", "Sahil Chaudhary", "Password@123")
        assertTrue("Registration verify OTP must succeed", result.isSuccess)
        assertTrue("Repository state should become authenticated after registration", repository.isAuthenticated.value)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(recorded)
        assertEquals("/api/auth/register/verify-otp", recorded?.path)
        assertEquals("POST", recorded?.method)
    }

    @Test
    fun test17_RepositoryForgotPasswordRequestOtp_Success() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": true,
                    "message": "If this mobile number is registered with MediCart, a verification OTP has been sent.",
                    "data": {
                        "phoneNumber": "9876543210",
                        "expiresInSeconds": 600,
                        "simulatedOtp": "487210"
                    }
                }""".trimIndent())
        )

        val result = repository.forgotPasswordRequestOtp("9876543210")
        assertTrue("Forgot password request OTP must succeed", result.isSuccess)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(recorded)
        assertEquals("/api/auth/forgot-password/request-otp", recorded?.path)
        assertEquals("POST", recorded?.method)
        val body = recorded!!.body.readUtf8()
        assertTrue(body.contains("9876543210"))
    }

    @Test
    fun test18_RepositoryForgotPasswordReset_Success() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": true,
                    "message": "Password reset successfully. Please log in with your new password.",
                    "data": {
                        "phoneNumber": "9876543210",
                        "passwordReset": true
                    }
                }""".trimIndent())
        )

        val result = repository.forgotPasswordReset("9876543210", "487210", "NewPassword@123")
        assertTrue("Forgot password reset must succeed", result.isSuccess)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(recorded)
        assertEquals("/api/auth/forgot-password/reset", recorded?.path)
        assertEquals("POST", recorded?.method)
        val body = recorded!!.body.readUtf8()
        assertTrue(body.contains("9876543210"))
        assertTrue(body.contains("487210"))
        assertTrue(body.contains("NewPassword@123"))
    }

    @Test
    fun test19_RepositoryLogout_ClearsAuthentication() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success": true,
                    "message": "Login successful",
                    "data": {
                        "user": {
                            "id": "u1",
                            "name": "Sahil Chaudhary",
                            "phoneNumber": "9876543210"
                        },
                        "token": "jwt_session_token_xyz"
                    }
                }""".trimIndent())
        )

        repository.login("9876543210", "Demo@1234")
        assertTrue("Should be authenticated", repository.isAuthenticated.value)

        repository.logout()
        assertFalse("Should not be authenticated after logout", repository.isAuthenticated.value)
    }
}
