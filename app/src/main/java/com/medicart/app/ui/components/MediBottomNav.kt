package com.medicart.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    ORDERS("orders", "Orders", Icons.AutoMirrored.Filled.ListAlt),
    CART("cart", "Cart", Icons.Default.ShoppingBag)
}

/**
 * Modern floating circular / capsule bottom navigation bar.
 * Features stadium-curved geometry, animated circular pill indicators,
 * subtle elevation, and healthcare Emerald styling.
 */
@Composable
fun MediBottomNavBar(
    currentRoute: String,
    cartBadgeCount: Int = 0,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = SurfaceWhite,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, BorderNeutral),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavDestination.values().forEach { destination ->
                    val isSelected = currentRoute == destination.route
                    val animatedBgColor by animateColorAsState(
                        targetValue = if (isSelected) SecondaryMint else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "bottomNavBg"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) PrimaryEmerald else TextSecondary,
                        label = "bottomNavIcon"
                    )
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(animatedBgColor)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onNavigate(destination.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                    tint = iconColor,
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
                                            text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                                            color = SurfaceWhite,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = destination.label,
                                style = if (isSelected) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = iconColor,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
