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
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.theme.*

@Composable
fun LoginScreen(
    mobileNumber: String = "9876543210",
    password: String = "",
    rememberMe: Boolean = true,
    isPasswordVisible: Boolean = false,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onMobileNumberChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onToggleRememberMe: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val isFormValid = mobileNumber.length == 10 && password.isNotBlank()

    Scaffold(
        containerColor = AppBackground
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
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Header with Tagline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = PrimaryEmerald,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✚", color = SurfaceWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column {
                        Text(
                            text = "MediCart",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                        Text(
                            text = "Healthcare, Made Convenient.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Welcome Back! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Login to continue to MediCart.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                // Error Banner
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
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

                // Form Container
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
                        // 1. Mobile Number Field
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
                                    placeholder = { Text("98765 43210", color = TextSecondary.copy(alpha = 0.5f)) },
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

                        // 2. Password Field
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PASSWORD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Forgot Password?",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onNavigateToForgotPassword() }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = onPasswordChange,
                                placeholder = { Text("Enter your password", color = TextSecondary.copy(alpha = 0.5f)) },
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
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (isFormValid && !isLoading) onLoginClick()
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = BorderNeutral
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 3. Remember Me Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onToggleRememberMe() }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { onToggleRememberMe() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryEmerald,
                                    checkmarkColor = SurfaceWhite
                                )
                            )
                            Text(
                                text = "Remember Me",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Login Button
                PrimaryButton(
                    text = if (isLoading) "Signing In..." else "Log In",
                    enabled = isFormValid && !isLoading,
                    onClick = onLoginClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider with OR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderNeutral)
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderNeutral)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // New User Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "By continuing, you agree to MediCart's Terms of Use & Privacy Policy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
