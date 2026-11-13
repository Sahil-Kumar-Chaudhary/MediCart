package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    mobileNumber: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onMobileNumberChange: (String) -> Unit = {},
    onRequestResetOtp: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val isValid = mobileNumber.length == 10

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Forgot Password",
                onBackClick = onNavigateBack
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
                // Key Icon in Rounded Surface
                Surface(
                    color = SecondaryMint,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🔑", fontSize = 28.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Don't worry! Enter your registered mobile number and we'll send you a 6-digit code to securely reset your password.",
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

                Spacer(modifier = Modifier.height(28.dp))

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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "REGISTERED MOBILE NUMBER",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold
                        )

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
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (isValid && !isLoading) onRequestResetOtp()
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
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = if (isLoading) "Sending Code..." else "Send Reset Code",
                    enabled = isValid && !isLoading,
                    onClick = onRequestResetOtp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remembered your password? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Back to Login",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                }
            }

            // Security note
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "A 6-digit OTP will be generated with 10-minute expiry.\nFor security reasons, invalid numbers will not disclose user existence.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
