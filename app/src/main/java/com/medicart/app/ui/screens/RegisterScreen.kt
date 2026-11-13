package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.theme.*

@Composable
fun RegisterScreen(
    name: String = "",
    mobileNumber: String = "",
    password: String = "",
    confirmPassword: String = "",
    termsAccepted: Boolean = false,
    isPasswordVisible: Boolean = false,
    isConfirmPasswordVisible: Boolean = false,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNameChange: (String) -> Unit = {},
    onMobileNumberChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onTermsAcceptedChange: () -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onToggleConfirmPasswordVisibility: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val isPasswordStrong = hasMinLength && hasUppercase && hasLowercase && hasDigit
    val passwordsMatch = password.isNotEmpty() && password == confirmPassword

    val isFormValid = name.trim().length >= 2 &&
            mobileNumber.length == 10 &&
            isPasswordStrong &&
            passwordsMatch &&
            termsAccepted

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Create Account",
                onBackClick = onNavigateToLogin
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Join MediCart 🌿",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sign up to order authentic medicines and healthcare products.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                // Error Banner
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = ErrorRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚠️", fontSize = 16.sp)
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Registration Form Card
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Full Name
                        Column {
                            Text(
                                text = "FULL NAME",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = onNameChange,
                                placeholder = { Text("e.g. Sahil Chaudhary", color = TextSecondary.copy(alpha = 0.5f)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = BorderNeutral
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 2. Mobile Number
                        Column {
                            Text(
                                text = "MOBILE NUMBER",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = AppBackground,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral)
                                ) {
                                    Text(
                                        text = "🇮🇳 +91",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)
                                    )
                                }

                                OutlinedTextField(
                                    value = mobileNumber,
                                    onValueChange = { input ->
                                        val digitsOnly = input.filter { it.isDigit() }.take(10)
                                        onMobileNumberChange(digitsOnly)
                                    },
                                    placeholder = { Text("10-digit number", color = TextSecondary.copy(alpha = 0.5f)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryEmerald,
                                        unfocusedBorderColor = BorderNeutral
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // 3. Password
                        Column {
                            Text(
                                text = "PASSWORD",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = onPasswordChange,
                                placeholder = { Text("At least 8 chars (Aa1...)", color = TextSecondary.copy(alpha = 0.5f)) },
                                singleLine = true,
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = onTogglePasswordVisibility) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = BorderNeutral
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Password Criteria Indicators
                            if (password.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    PasswordCriterionRow("At least 8 characters", hasMinLength)
                                    PasswordCriterionRow("At least 1 uppercase letter (A-Z)", hasUppercase)
                                    PasswordCriterionRow("At least 1 lowercase letter (a-z)", hasLowercase)
                                    PasswordCriterionRow("At least 1 number (0-9)", hasDigit)
                                }
                            }
                        }

                        // 4. Confirm Password
                        Column {
                            Text(
                                text = "CONFIRM PASSWORD",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = onConfirmPasswordChange,
                                placeholder = { Text("Re-enter password", color = TextSecondary.copy(alpha = 0.5f)) },
                                singleLine = true,
                                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = onToggleConfirmPasswordVisibility) {
                                        Icon(
                                            imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (isFormValid && !isLoading) onRegisterClick()
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (confirmPassword.isNotEmpty() && !passwordsMatch) ErrorRed else PrimaryEmerald,
                                    unfocusedBorderColor = if (confirmPassword.isNotEmpty() && !passwordsMatch) ErrorRed else BorderNeutral
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (confirmPassword.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (passwordsMatch) "✓ Passwords match" else "✗ Passwords do not match",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (passwordsMatch) PrimaryEmerald else ErrorRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // 5. Terms & Conditions Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onTermsAcceptedChange() }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = termsAccepted,
                                onCheckedChange = { onTermsAcceptedChange() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryEmerald,
                                    checkmarkColor = SurfaceWhite
                                )
                            )
                            Text(
                                text = "I agree to MediCart's Terms of Service and Privacy Policy.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                PrimaryButton(
                    text = if (isLoading) "Creating Account..." else "Register & Send OTP",
                    enabled = isFormValid && !isLoading,
                    onClick = onRegisterClick
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Already Have Account
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Protected by 256-bit encryption & secure token revocation.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PasswordCriterionRow(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Close,
            contentDescription = null,
            tint = if (isMet) PrimaryEmerald else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isMet) PrimaryEmerald else TextSecondary.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
    }
}
