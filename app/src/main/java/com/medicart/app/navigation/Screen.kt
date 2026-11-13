package com.medicart.app.navigation

import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object RegisterOtp : Screen("register_otp/{mobile}/{name}/{password}") {
        fun createRoute(mobile: String, name: String, password: String) =
            "register_otp/$mobile/${URLEncoder.encode(name, "UTF-8")}/${URLEncoder.encode(password, "UTF-8")}"
    }
    object ForgotPassword : Screen("forgot_password")
    object ResetPasswordOtp : Screen("reset_password_otp/{mobile}") {
        fun createRoute(mobile: String) = "reset_password_otp/$mobile"
    }
    object ResetPassword : Screen("reset_password/{mobile}/{otp}") {
        fun createRoute(mobile: String, otp: String) = "reset_password/$mobile/$otp"
    }
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
        fun createRoute(orderRef: String) = "order_confirmation/${URLEncoder.encode(orderRef, "UTF-8")}"
    }
    object OrderHistory : Screen("orders")
    object OrderDetail : Screen("order_detail/{orderRef}") {
        fun createRoute(orderRef: String) = "order_detail/${URLEncoder.encode(orderRef, "UTF-8")}"
    }
    object Profile : Screen("profile")
}
