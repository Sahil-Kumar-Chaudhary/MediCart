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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
fun ResetPasswordScreen(
    mobileNumber: String,
    otpCode: String,
    newPassword: String = "",
    confirmNewPassword: String = "",
    isNewPasswordVisible: Boolean = false,
    isConfirmNewPasswordVisible: Boolean = false,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmNewPasswordChange: (String) -> Unit = {},
    onToggleNewPasswordVisibility: () -> Unit = {},
    onToggleConfirmNewPasswordVisibility: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    val hasMinLength = newPassword.length >= 8
    val hasUppercase = newPassword.any { it.isUpperCase() }
    val hasLowercase = newPassword.any { it.isLowerCase() }
    val hasDigit = newPassword.any { it.isDigit() }
    val isPasswordStrong = hasMinLength && hasUppercase && hasLowercase && hasDigit
    val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmNewPassword

    val isFormValid = isPasswordStrong && passwordsMatch

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Reset Password",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Set New Password 🔐",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create a strong new password for +91 $mobileNumber. All existing active sessions on other devices will be invalidated for security.",
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

                Spacer(modifier = Modifier.height(24.dp))

                // Card Container
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. New Password
                        Column {
                            Text(
                                text = "NEW PASSWORD",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = onNewPasswordChange,
                                placeholder = { Text("At least 8 chars (Aa1...)", color = TextSecondary.copy(alpha = 0.5f)) },
                                singleLine = true,
                                visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = onToggleNewPasswordVisibility) {
                                        Icon(
                                            imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password",
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
                            if (newPassword.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    PasswordCriterionRow("At least 8 characters", hasMinLength)
                                    PasswordCriterionRow("At least 1 uppercase letter (A-Z)", hasUppercase)
                                    PasswordCriterionRow("At least 1 lowercase letter (a-z)", hasLowercase)
                                    PasswordCriterionRow("At least 1 number (0-9)", hasDigit)
                                }
                            }
                        }

                        // 2. Confirm New Password
                        Column {
                            Text(
                                text = "CONFIRM NEW PASSWORD",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = confirmNewPassword,
                                onValueChange = onConfirmNewPasswordChange,
                                placeholder = { Text("Re-enter new password", color = TextSecondary.copy(alpha = 0.5f)) },
                                singleLine = true,
                                visualTransformation = if (isConfirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = onToggleConfirmNewPasswordVisibility) {
                                        Icon(
                                            imageVector = if (isConfirmNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isConfirmNewPasswordVisible) "Hide password" else "Show password",
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
                                        if (isFormValid && !isLoading) onResetPasswordClick()
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (confirmNewPassword.isNotEmpty() && !passwordsMatch) ErrorRed else PrimaryEmerald,
                                    unfocusedBorderColor = if (confirmNewPassword.isNotEmpty() && !passwordsMatch) ErrorRed else BorderNeutral
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (confirmNewPassword.isNotEmpty()) {
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
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = if (isLoading) "Updating Password..." else "Reset Password",
                    enabled = isFormValid && !isLoading,
                    onClick = onResetPasswordClick
                )
            }

            // Footer note
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your password is encrypted using salted bcrypt.\nAfter resetting, you will be redirected to the login screen.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
