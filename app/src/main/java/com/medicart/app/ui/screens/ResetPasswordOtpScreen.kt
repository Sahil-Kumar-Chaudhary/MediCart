package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.theme.*

@Composable
fun ResetPasswordOtpScreen(
    mobileNumber: String,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onVerifyOtp: (String) -> Unit = {},
    onResendOtp: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var otpCode by remember { mutableStateOf("487210") }
    val focusManager = LocalFocusManager.current
    val isOtpComplete = otpCode.length == 6

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Verification Code",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Verify Reset Code 🔒",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter the 6-digit password reset code sent to +91 $mobileNumber.",
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

                // OTP Display Boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until 6) {
                        val char = otpCode.getOrNull(i)?.toString() ?: ""
                        val isCurrent = i == otpCode.length.coerceAtMost(5)
                        Surface(
                            color = if (char.isNotEmpty()) SecondaryMint else SurfaceWhite,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (char.isNotEmpty() || isCurrent) PrimaryEmerald else BorderNeutral
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = char,
                                    color = PrimaryEmerald,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hidden / Direct Input
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(6)
                        otpCode = digits
                    },
                    label = { Text("Enter 6-Digit Code") },
                    placeholder = { Text("487210") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (isOtpComplete && !isLoading) onVerifyOtp(otpCode)
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = BorderNeutral
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Demo hint card
                Surface(
                    color = SecondaryMint.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 14.sp)
                            Text(
                                text = "Demo Mode OTP: 487210",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Auto-filled",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Didn't receive code?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Resend Code",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onResendOtp() }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                PrimaryButton(
                    text = if (isLoading) "Proceeding..." else "Proceed to Set New Password",
                    enabled = isOtpComplete && !isLoading,
                    onClick = { onVerifyOtp(otpCode) }
                )
            }

            // Keypad
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "⌫")
                    )
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                TextButton(
                                    onClick = {
                                        when (key) {
                                            "⌫" -> if (otpCode.isNotEmpty()) otpCode = otpCode.dropLast(1)
                                            "C" -> otpCode = ""
                                            else -> if (otpCode.length < 6) otpCode += key
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
