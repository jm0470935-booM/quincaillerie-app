package com.quincaillerie.gestion.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quincaillerie.gestion.ui.screens.DashboardScreen
import com.quincaillerie.gestion.ui.screens.ProductListScreen
import com.quincaillerie.gestion.ui.screens.SaleScreen
import com.quincaillerie.gestion.viewmodel.ProductViewModel
import com.quincaillerie.gestion.viewmodel.SaleViewModel

sealed class Screen(val route: String, val label: String) {
    object Dashboard : Screen("dashboard", "Accueil")
    object Products : Screen("products", "Produits")
    object Sale : Screen("sale", "Vente")
}

@Composable
fun AppNavigation(
    productViewModel: ProductViewModel,
    saleViewModel: SaleViewModel
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Dashboard, Screen.Products, Screen.Sale)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val icon = when (screen) {
                        Screen.Dashboard -> Icons.Default.Home
                        Screen.Products -> Icons.Default.Inventory
                        Screen.Sale -> Icons.Default.ShoppingCart
                    }
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(productViewModel, saleViewModel)
            }
            composable(Screen.Products.route) {
                ProductListScreen(productViewModel)
            }
            composable(Screen.Sale.route) {
                SaleScreen(productViewModel, saleViewModel)
            }
        }
    }
}
