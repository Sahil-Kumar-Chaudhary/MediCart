package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.medicart.app.ui.components.EmptyStateView
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.components.QuantityStepper
import com.medicart.app.ui.theme.*

@Composable
fun CartScreen(
    repository: PharmacyRepository,
    onBack: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onExploreMedicines: () -> Unit
) {
    val cartItems by repository.cartItems.collectAsState()
    val summary = remember(cartItems) { CartSummary(cartItems) }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Shopping Cart (${cartItems.size})",
                onBackClick = onBack,
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = { repository.clearCart() }) {
                            Text("Clear", color = FeedbackError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
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
                            Text("TOTAL PAYABLE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                "₹${summary.finalTotal.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        PrimaryButton(
                            text = "Proceed to Checkout",
                            onClick = onProceedToCheckout,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyStateView(
                iconEmoji = "🛒",
                title = "Your Cart is Empty",
                description = "You have no medicines added to your order basket.",
                actionButtonText = "Explore Medicines",
                onActionClick = onExploreMedicines,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Delivery address preview strip
                item {
                    Surface(
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("DELIVERING TO", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 9.sp)
                                    Text("Indiranagar, Bengaluru - 560038", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                            Text("Change", color = PrimaryEmerald, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Cart item list
                items(cartItems) { item ->
                    Surface(
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.product.iconEmoji, fontSize = 24.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = item.product.packSize,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${item.totalPrice.toInt()} (${item.product.priceInr.toInt()} × ${item.quantity})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            QuantityStepper(
                                quantity = item.quantity,
                                onIncrease = { repository.addToCart(item.product, 1) },
                                onDecrease = { repository.updateCartQuantity(item.product.id, item.quantity - 1) }
                            )
                        }
                    }
                }

                // Bill details breakdown
                item {
                    Surface(
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("BILL DETAILS", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            BillRow("Item Total", "₹${summary.itemTotal.toInt()}")
                            BillRow("Delivery Fee", "₹${summary.deliveryFee.toInt()}")
                            BillRow("Packaging & Safety Charge", "₹${summary.packagingFee.toInt()}")
                            if (summary.totalSavings > 0) {
                                BillRow("Total Savings", "-₹${summary.totalSavings.toInt()}", isGreen = true)
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = BorderNeutral)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("₹${summary.finalTotal.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillRow(label: String, value: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = if (isGreen) PrimaryEmerald else TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = if (isGreen) PrimaryEmerald else TextPrimary)
    }
}
