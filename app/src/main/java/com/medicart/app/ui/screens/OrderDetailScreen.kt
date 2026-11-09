package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.model.Order
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.StatusBadge
import com.medicart.app.ui.theme.*

@Composable
fun OrderDetailScreen(
    orderRef: String,
    repository: MockPharmacyRepository,
    onBack: () -> Unit
) {
    val orders by repository.orders.collectAsState()
    val order = remember(orderRef, orders) { orders.find { it.orderReference == orderRef } }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Order $orderRef",
                onBackClick = onBack,
                actions = {
                    TextButton(onClick = { /* Download Invoice */ }) {
                        Text("Invoice 📄", color = PrimaryEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        if (order != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Progress Tracker
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ORDER PROGRESS", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        TimelineStep(isCompleted = true, isCurrent = false, title = "Order Confirmed", subtitle = "Backend verification complete")
                        TimelineConnector(isActive = true)
                        TimelineStep(isCompleted = false, isCurrent = true, title = "Dispensing & Packing", subtitle = "Pharmacist verifying batch & expiry")
                        TimelineConnector(isActive = false)
                        TimelineStep(isCompleted = false, isCurrent = false, title = "Dispatched", subtitle = "Delivery partner assigned")
                        TimelineConnector(isActive = false)
                        TimelineStep(isCompleted = false, isCurrent = false, title = "Delivered", subtitle = order.estimatedDelivery)
                    }
                }

                // Ordered Items
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("PACKAGE CONTENTS (${order.items.size} ITEMS)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(item.product.iconEmoji, fontSize = 16.sp)
                                    }
                                    Column {
                                        Text(item.product.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${item.product.packSize} • Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                                Text("₹${item.totalPrice.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                // Address & Payment
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("DELIVERY & PAYMENT", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(order.address.fullAddressLine, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Paid via: ${order.paymentMethod.label}", style = MaterialTheme.typography.bodySmall, color = PrimaryDark, fontWeight = FontWeight.Medium)
                        Text("Transaction ID: ${order.paymentTransactionId}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }

                // Need help button
                OutlinedButton(
                    onClick = { /* Help */ },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💬 Need Help with this Order?", color = TextPrimary, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun TimelineStep(isCompleted: Boolean, isCurrent: Boolean, title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) PrimaryEmerald else if (isCurrent) SecondaryMint else BorderNeutral
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Text("✓", color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            } else if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PrimaryEmerald)
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = TextPrimary
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun TimelineConnector(isActive: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 11.dp)
            .width(2.dp)
            .height(14.dp)
            .background(if (isActive) PrimaryEmerald else BorderNeutral)
    )
}
