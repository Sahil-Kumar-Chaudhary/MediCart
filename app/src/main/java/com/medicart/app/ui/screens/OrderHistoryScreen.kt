package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.OrderStatus
import com.medicart.app.ui.components.EmptyStateView
import com.medicart.app.ui.components.MediBottomNavBar
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.StatusBadge
import com.medicart.app.ui.theme.*

@Composable
fun OrderHistoryScreen(
    repository: PharmacyRepository,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val orders by repository.orders.collectAsState()
    val cartItems by repository.cartItems.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Order History (${orders.size})"
            )
        },
        bottomBar = {
            MediBottomNavBar(
                currentRoute = "orders",
                cartBadgeCount = cartItems.sumOf { it.quantity },
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "catalogue" -> onNavigateToCatalogue()
                        "cart" -> onNavigateToCart()
                    }
                }
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            EmptyStateView(
                iconEmoji = "📦",
                title = "No Orders Placed Yet",
                description = "Your previous prescription and OTC pharmacy orders will show up here.",
                actionButtonText = "Start Shopping",
                onActionClick = onNavigateToCatalogue,
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
                items(orders) { order ->
                    Surface(
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(order.orderReference) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.orderReference,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryEmerald
                                )
                                StatusBadge(status = order.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(order.createdAt, style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = BorderNeutral)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "₹${order.totalAmount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${order.items.size} item(s)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                TextButton(onClick = { onNavigateToDetail(order.orderReference) }) {
                                    Text("View Details →", color = PrimaryEmerald, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
