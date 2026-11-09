package com.medicart.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicart.app.model.OrderStatus
import com.medicart.app.ui.theme.*

@Composable
fun StatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        OrderStatus.PROCESSING -> Triple(SecondaryMint, PrimaryEmerald, "● Processing")
        OrderStatus.PACKING -> Triple(SecondaryMint, PrimaryEmerald, "● Packing")
        OrderStatus.DISPATCHED -> Triple(LightAmberSurface, FeedbackWarning, "⚡ Dispatched")
        OrderStatus.DELIVERED -> Triple(LightGreenSurface, FeedbackSuccess, "✓ Delivered")
        OrderStatus.CANCELLED -> Triple(LightRedSurface, FeedbackError, "✕ Cancelled")
    }

    Text(
        text = label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
