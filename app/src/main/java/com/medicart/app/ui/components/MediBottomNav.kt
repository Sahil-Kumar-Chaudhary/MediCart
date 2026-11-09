package com.medicart.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.ui.theme.*

enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    CATALOGUE("catalogue", "Catalogue", Icons.Default.LocalPharmacy),
    ORDERS("orders", "Orders", Icons.Default.ListAlt),
    CART("cart", "Cart", Icons.Default.ShoppingBag)
}

@Composable
fun MediBottomNavBar(
    currentRoute: String,
    cartBadgeCount: Int = 0,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = SurfaceWhite,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavDestination.values().forEach { destination ->
                val isSelected = currentRoute == destination.route
                val tintColor = if (isSelected) PrimaryEmerald else TextSecondary
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onNavigate(destination.route) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                        if (destination == BottomNavDestination.CART && cartBadgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 6.dp, y = (-4).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartBadgeCount.toString(),
                                    color = SurfaceWhite,
                                    fontSize = 9.sp,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = destination.label,
                        style = if (isSelected) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
                        color = tintColor,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
