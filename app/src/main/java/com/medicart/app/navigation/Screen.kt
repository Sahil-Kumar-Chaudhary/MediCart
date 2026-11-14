package com.medicart.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Otp : Screen("otp/{mobile}") {
        fun createRoute(mobile: String) = "otp/$mobile"
    }
    object Home : Screen("home")
    object Catalogue : Screen("catalogue")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    object Cart : Screen("cart")
    object Address : Screen("address")
    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation/{orderRef}") {
        fun createRoute(orderRef: String) = "order_confirmation/${java.net.URLEncoder.encode(orderRef, "UTF-8")}"
    }
    object OrderHistory : Screen("orders")
    object OrderDetail : Screen("order_detail/{orderRef}") {
        fun createRoute(orderRef: String) = "order_detail/${java.net.URLEncoder.encode(orderRef, "UTF-8")}"
    }
    object Profile : Screen("profile")
}
