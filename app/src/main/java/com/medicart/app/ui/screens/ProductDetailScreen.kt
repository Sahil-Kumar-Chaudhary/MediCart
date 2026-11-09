package com.medicart.app.ui.screens

import androidx.compose.foundation.background
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
import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.model.Product
import com.medicart.app.ui.components.MediStandardTopBar
import com.medicart.app.ui.components.PrimaryButton
import com.medicart.app.ui.components.QuantityStepper
import com.medicart.app.ui.theme.*

@Composable
fun ProductDetailScreen(
    productId: String,
    repository: MockPharmacyRepository,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var selectedQty by remember { mutableStateOf(1) }

    LaunchedEffect(productId) {
        product = repository.getProductById(productId)
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MediStandardTopBar(
                title = "Medicine Details",
                onBackClick = onBack
            )
        },
        bottomBar = {
            product?.let { p ->
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuantityStepper(
                            quantity = selectedQty,
                            onIncrease = { if (selectedQty < 5) selectedQty++ },
                            onDecrease = { if (selectedQty > 1) selectedQty-- }
                        )

                        PrimaryButton(
                            text = if (p.isAvailable) "Add to Cart" else "Unavailable",
                            trailingText = "₹${(p.priceInr * selectedQty).toInt()}",
                            enabled = p.isAvailable,
                            onClick = {
                                repository.addToCart(p, selectedQty)
                                onNavigateToCart()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        product?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Text(p.iconEmoji, fontSize = 72.sp)
                }

                // Title & Price Card
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (p.isAvailable) "● In Stock • Dispatched in 2-4 hrs" else "Out of Stock",
                                color = if (p.isAvailable) FeedbackSuccess else FeedbackError,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (p.discountPercent > 0) {
                                Surface(
                                    color = SecondaryMint,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${p.discountPercent}% OFF",
                                        color = PrimaryEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = p.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "${p.packSize} • Oral Solid Dosage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "₹${p.priceInr.toInt()}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "MRP ₹${p.mrpInr.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                            Text(
                                text = "(Inclusive of all taxes)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Specification Card
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNeutral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PRODUCT INFORMATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpecRow("Active Ingredient", p.activeIngredient)
                            SpecRow("Form & Packaging", p.packSize)
                            SpecRow("Storage Instruction", p.storageInstruction)
                            SpecRow("Expiry Date", p.expiryDate)
                            SpecRow("Return Policy", "${p.returnableDays} Days Returnable")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = AppBackground,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ℹ️ Healthcare Notice: Fictional pharmacy medicine item for application demonstration. Do not use for clinical prescription.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
