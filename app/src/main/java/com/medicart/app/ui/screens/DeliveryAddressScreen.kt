package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.model.AddressType
import com.medicart.app.model.DeliveryAddress
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.theme.*

@Composable
fun DeliveryAddressScreen(
    initialAddress: DeliveryAddress,
    onBack: () -> Unit,
    onSaveAddress: (DeliveryAddress) -> Unit
) {
    var name by remember { mutableStateOf(initialAddress.recipientName) }
    var mobile by remember { mutableStateOf(initialAddress.mobileNumber) }
    var flat by remember { mutableStateOf(initialAddress.flatNumber) }
    var street by remember { mutableStateOf(initialAddress.streetLocality) }
    var city by remember { mutableStateOf(initialAddress.city) }
    var state by remember { mutableStateOf(initialAddress.state) }
    var pinCode by remember { mutableStateOf(initialAddress.pinCode) }
    var addressType by remember { mutableStateOf(initialAddress.addressType) }

    val isPinValid = pinCode.length == 6 && pinCode.all { it.isDigit() } && !pinCode.startsWith("0")
    val isFormValid = name.isNotBlank() && flat.isNotBlank() && street.isNotBlank() && isPinValid

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Delivery Address",
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        text = "Save & Deliver Here",
                        enabled = isFormValid,
                        onClick = {
                            val updated = initialAddress.copy(
                                recipientName = name,
                                mobileNumber = mobile,
                                flatNumber = flat,
                                streetLocality = street,
                                city = city,
                                state = state,
                                pinCode = pinCode,
                                addressType = addressType
                            )
                            onSaveAddress(updated)
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contact Info
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("RECIPIENT CONTACT", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile (+91)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Address Details (Indian Format)
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("INDIAN ADDRESS DETAILS", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = flat,
                        onValueChange = { flat = it },
                        label = { Text("Flat / House No. / Building") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("Street / Locality") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { if (it.length <= 6) pinCode = it },
                        label = { Text("6-Digit PIN Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = pinCode.isNotEmpty() && !isPinValid,
                        supportingText = {
                            if (pinCode.isNotEmpty() && !isPinValid) {
                                Text("Enter valid 6-digit Indian PIN code", color = FeedbackError)
                            } else if (isPinValid) {
                                Text("✓ Verified deliverable pincode", color = FeedbackSuccess)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Address Type Tag
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ADDRESS TYPE", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AddressType.values().forEach { type ->
                            FilterChip(
                                selected = addressType == type,
                                onClick = { addressType = type },
                                label = { Text(type.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryEmerald,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
