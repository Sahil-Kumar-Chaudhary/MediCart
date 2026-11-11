package com.medicart.app.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.model.DeliveryAddress
import com.medicart.app.ui.screens.*
import kotlinx.coroutines.launch

@Composable
fun MediCartNavGraph(
    navController: NavHostController,
    repository: PharmacyRepository
) {
    var activeAddress by remember { mutableStateOf(repository.getDefaultAddress()) }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val isAuthenticated by repository.isAuthenticated.collectAsState()
            SplashScreen(
                onTimeout = {
                    val destination = if (isAuthenticated) Screen.Home.route else Screen.Login.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onContinue = { mobile ->
                    scope.launch {
                        repository.requestOtp(mobile)
                        navController.navigate(Screen.Otp.createRoute(mobile))
                    }
                }
            )
        }

        composable(Screen.Otp.route) { backStackEntry ->
            val mobile = backStackEntry.arguments?.getString("mobile") ?: "9876543210"
            OtpVerificationScreen(
                mobileNumber = mobile,
                onBack = { navController.popBackStack() },
                onVerified = {
                    scope.launch {
                        val result = repository.verifyOtp(mobile, "487210")
                        result.onSuccess {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onNavigateToCatalogue = { navController.navigate(Screen.Catalogue.route) },
                onNavigateToProduct = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToOrders = { navController.navigate(Screen.OrderHistory.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Catalogue.route) {
            CatalogueScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onNavigateToProduct = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToOrders = { navController.navigate(Screen.OrderHistory.route) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "med_01"
            ProductDetailScreen(
                productId = productId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onProceedToCheckout = { navController.navigate(Screen.Checkout.route) },
                onExploreMedicines = { navController.navigate(Screen.Catalogue.route) }
            )
        }

        composable(Screen.Address.route) {
            DeliveryAddressScreen(
                initialAddress = activeAddress,
                onBack = { navController.popBackStack() },
                onSaveAddress = { updated ->
                    activeAddress = updated
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                repository = repository,
                currentAddress = activeAddress,
                onBack = { navController.popBackStack() },
                onChangeAddress = { navController.navigate(Screen.Address.route) },
                onOrderPlaced = { orderRef ->
                    navController.navigate(Screen.OrderConfirmation.createRoute(orderRef)) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OrderConfirmation.route) { backStackEntry ->
            val orderRef = backStackEntry.arguments?.getString("orderRef") ?: "#MC-2026-8942"
            OrderConfirmationScreen(
                orderRef = orderRef,
                onViewOrderDetails = {
                    navController.navigate(Screen.OrderDetail.createRoute(orderRef)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onContinueShopping = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(
                repository = repository,
                onNavigateToDetail = { ref -> navController.navigate(Screen.OrderDetail.createRoute(ref)) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToCatalogue = { navController.navigate(Screen.Catalogue.route) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.OrderDetail.route) { backStackEntry ->
            val orderRef = backStackEntry.arguments?.getString("orderRef") ?: "#MC-2026-8942"
            OrderDetailScreen(
                orderRef = orderRef,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onNavigateToAddresses = { navController.navigate(Screen.Address.route) },
                onNavigateToOrders = { navController.navigate(Screen.OrderHistory.route) },
                onLogout = {
                    scope.launch {
                        repository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
