package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.Category
import com.medicart.app.model.Product
import com.medicart.app.ui.components.MediBottomNavBar
import com.medicart.app.ui.components.OfflineBanner
import com.medicart.app.ui.theme.*

@Composable
fun HomeScreen(
    repository: PharmacyRepository,
    onNavigateToCatalogue: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val categories = remember { repository.getCategories() }
    var featuredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    val cartItems by repository.cartItems.collectAsState()
    val isOffline by repository.isOffline.collectAsState()

    LaunchedEffect(isOffline) {
        val result = repository.getProducts()
        featuredProducts = result.getOrDefault(emptyList()).take(4)
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Column {
                OfflineBanner(
                    isOffline = isOffline,
                    onReconnect = { repository.setOfflineMode(false) }
                )
                Surface(
                    color = SurfaceWhite,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                    // Top Bar: Location & Avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "DELIVER TO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "Indiranagar, Bengaluru - 560038 ▾",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AppBackground)
                                    .clickable { /* Notification */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔔", fontSize = 14.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryEmerald)
                                    .clickable { onNavigateToProfile() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("SK", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search field
                    Surface(
                        color = AppBackground,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToCatalogue() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search medicines, health essentials...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    },
    bottomBar = {
            MediBottomNavBar(
                currentRoute = "home",
                cartBadgeCount = cartItems.sumOf { it.quantity },
                onNavigate = { route ->
                    when (route) {
                        "catalogue" -> onNavigateToCatalogue()
                        "orders" -> onNavigateToOrders()
                        "cart" -> onNavigateToCart()
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Health Banner
            item {
                Surface(
                    color = PrimaryEmerald,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryEmerald, PrimaryDark)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Surface(
                            color = SurfaceWhite.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "MONSOON WELLNESS",
                                color = SurfaceWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Transparent Generic Medicine Prices",
                            style = MaterialTheme.typography.titleMedium,
                            color = SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verified formulations delivered safely to your home.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToCatalogue,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Explore Catalogue →",
                                color = PrimaryEmerald,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            // Categories Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Shop by Category", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("View All", style = MaterialTheme.typography.labelSmall, color = PrimaryEmerald, modifier = Modifier.clickable { onNavigateToCatalogue() })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        Surface(
                            color = SurfaceWhite,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToCatalogue() }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecondaryMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat.iconEmoji, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cat.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${cat.itemCount} items",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Featured Products
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Featured Medicines", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("See More", style = MaterialTheme.typography.labelSmall, color = PrimaryEmerald, modifier = Modifier.clickable { onNavigateToCatalogue() })
                }
            }

            items(featuredProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { product ->
                        Surface(
                            color = SurfaceWhite,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToProduct(product.id) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(product.iconEmoji, fontSize = 32.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (product.inStock) "● In Stock" else "Out of Stock",
                                    color = if (product.inStock) FeedbackSuccess else FeedbackError,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = product.packSize,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${product.priceInr.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = { repository.addToCart(product, 1) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("+ Add", fontSize = 10.sp, color = SurfaceWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
