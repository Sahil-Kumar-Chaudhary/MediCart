package com.medicart.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.model.Product
import com.medicart.app.ui.components.EmptyStateView
import com.medicart.app.ui.components.MediBottomNavBar
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.QuantityStepper
import com.medicart.app.ui.theme.*

@Composable
fun CatalogueScreen(
    repository: MockPharmacyRepository,
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("Paracetamol") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val cartItems by repository.cartItems.collectAsState()

    LaunchedEffect(searchQuery, selectedCategory) {
        products = repository.getProducts(searchQuery, selectedCategory)
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppBackground)
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("←", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search medicines...", fontSize = 12.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = TextSecondary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { searchQuery = "" }
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = BorderNeutral
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null },
                                label = { Text("All", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryEmerald,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                        items(repository.getCategories()) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.id,
                                onClick = { selectedCategory = cat.id },
                                label = { Text(cat.title, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryEmerald,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            MediBottomNavBar(
                currentRoute = "catalogue",
                cartBadgeCount = cartItems.sumOf { it.quantity },
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "orders" -> onNavigateToOrders()
                        "cart" -> onNavigateToCart()
                    }
                }
            )
        }
    ) { padding ->
        if (products.isEmpty()) {
            EmptyStateView(
                iconEmoji = "🔍",
                title = "No Medicines Found",
                description = "We couldn't find any pharmacy products matching '$searchQuery'.",
                actionButtonText = "Clear Search",
                onActionClick = { searchQuery = ""; selectedCategory = null },
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                item {
                    Text(
                        text = "Showing ${products.size} medicines for '$searchQuery'",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                items(products) { product ->
                    val cartItem = cartItems.find { it.product.id == product.id }

                    Surface(
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToProduct(product.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(product.iconEmoji, fontSize = 28.sp)
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (product.isAvailable) "● In Stock" else if (product.isExpired) "🛑 Batch Expired" else "Out of Stock",
                                            color = if (product.isAvailable) FeedbackSuccess else FeedbackError,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (product.discountPercent > 0) {
                                            Text(
                                                text = "${product.discountPercent}% OFF",
                                                color = PrimaryEmerald,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${product.packSize} • ${product.genericName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "₹${product.priceInr.toInt()}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "₹${product.mrpInr.toInt()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    }

                                    if (cartItem != null) {
                                        QuantityStepper(
                                            quantity = cartItem.quantity,
                                            onIncrease = { repository.addToCart(product, 1) },
                                            onDecrease = { repository.updateCartQuantity(product.id, cartItem.quantity - 1) }
                                        )
                                    } else {
                                        Button(
                                            onClick = { repository.addToCart(product, 1) },
                                            enabled = product.isAvailable,
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(
                                                text = if (product.isAvailable) "+ Add" else "Unavailable",
                                                fontSize = 11.sp,
                                                color = SurfaceWhite
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
    }
}
