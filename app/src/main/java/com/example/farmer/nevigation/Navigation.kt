package com.example.farmer.nevigation

import ConsumerHomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.farmer.AboutUsScreen
//import com.example.consumerapp.screens.ConsumerHomeScreen
import com.example.farmer.ua.screens.*
import com.example.farmer.data.network.WeatherDataRepository
import com.example.farmer.data.network.WeatherApi
import com.example.farmer.ua.screens.AddProductScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val apiKey = "edec707d0f932fc35f6ce78335cb1f03"
    val weatherDataRepository = WeatherDataRepository(WeatherApi.weatherApiService)

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }


        composable("signup") {
            SignupScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }


        composable("home") {
            HomeScreen(
                onFarmerClick = { navController.navigate("farmer") },
                onConsumerClick = { navController.navigate("consumer") }
            )
        }

        composable("farmer") {
            FarmerHomeScreen(
                apiKey = apiKey,
                weatherDataRepository = weatherDataRepository,
                onNavigateToAddProduct = { navController.navigate("add_product") },
                onNavigateToMyProducts = { navController.navigate("my_products") },
                onNavigateToTransactions = { navController.navigate("transactions") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToAboutUs = { navController.navigate("about_us") }
            )
        }


        composable("add_product") {
            AddProductScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }


        composable("my_products") {
            MyProductsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }




        composable("profile") {
            ProfileScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }



        composable("consumer") {
            ConsumerHomeScreen(
               // onNavigateToMarketplace = { navController.navigate("marketplace") },
                onNavigateToCart = { navController.navigate("cart") },
               // onNavigateToOrders = { navController.navigate("orders") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToAboutUs = { navController.navigate("about_us") }
            )
        }



        composable("cart") {
            CartScreen(

                onNavigateBack = { navController.popBackStack() },
                        onNavigateToPayment = { navController.navigate("payment") }
            )
        }



        composable("payment") {
            PaymentScreen(
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = {}

            )
        }

        composable("about_us") {
            AboutUsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
