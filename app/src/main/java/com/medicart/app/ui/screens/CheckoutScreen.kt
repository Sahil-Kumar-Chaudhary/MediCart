package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.CartSummary
import com.medicart.app.model.DeliveryAddress
import com.medicart.app.model.PaymentMethod
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    repository: PharmacyRepository,
    currentAddress: DeliveryAddress,
    onBack: () -> Unit,
    onChangeAddress: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val cartItems by repository.cartItems.collectAsState()
    val summary = remember(cartItems) { CartSummary(cartItems) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.UPI_DEMO) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Checkout & Review",
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("FINAL TOTAL", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text(
                            "₹${summary.finalTotal.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    PrimaryButton(
                        text = if (isPlacingOrder) "Placing Order..." else "Place Order (Demo)",
                        enabled = !isPlacingOrder && cartItems.isNotEmpty(),
                        onClick = {
                            isPlacingOrder = true
                            errorMessage = null
                            scope.launch {
                                val result = repository.submitOrder(currentAddress, selectedPayment)
                                isPlacingOrder = false
                                result.onSuccess { order ->
                                    onOrderPlaced(order.orderReference)
                                }.onFailure { error ->
                                    errorMessage = error.localizedMessage ?: "Failed to place order."
                                }
                            }
                        },
                        modifier = Modifier.width(210.dp)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Error banner if any
            if (errorMessage != null) {
                Surface(
                    color = LightRedSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FeedbackError),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ $errorMessage",
                        color = FeedbackError,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Delivery Address card
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DELIVERING TO", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                        Text(
                            "Change",
                            color = PrimaryEmerald,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.clickable { onChangeAddress() }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${currentAddress.recipientName} • ${currentAddress.mobileNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        currentAddress.fullAddressLine,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Order preview
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📦 ${cartItems.sumOf { it.quantity }} items in package", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("₹${summary.itemTotal.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            // Simulated Payment Selection (Indian Market)
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PAYMENT METHOD (SIMULATED)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                    
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = SecondaryMint.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ℹ️ Demo Sandbox: No real money is deducted. Testing reliable backend order creation.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = PrimaryDark,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentMethod.values().forEach { method ->
                        val isSelected = selectedPayment == method
                        Surface(
                            color = if (isSelected) AppBackground else SurfaceWhite,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) PrimaryEmerald else BorderNeutral
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedPayment = method }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPayment = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryEmerald)
                                )
                                Column {
                                    Text(method.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    val subtext = when (method) {
                                        PaymentMethod.UPI_DEMO -> "Google Pay, PhonePe, Paytm, or UPI ID"
                                        PaymentMethod.CASH_ON_DELIVERY -> "Pay on delivery via Cash or QR"
                                        PaymentMethod.CARD_DEMO -> "RuPay, Visa, Mastercard simulated"
                                    }
                                    Text(subtext, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
