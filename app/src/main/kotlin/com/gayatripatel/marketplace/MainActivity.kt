package com.gayatripatel.marketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gayatripatel.marketplace.core.ui.theme.MarketplaceTheme
import com.gayatripatel.marketplace.feature.cart.cartScreen
import com.gayatripatel.marketplace.feature.cart.navigateToCart
import com.gayatripatel.marketplace.feature.catalog.CatalogDestination
import com.gayatripatel.marketplace.feature.catalog.catalogScreen
import com.gayatripatel.marketplace.feature.catalog.navigateToProductDetail
import com.gayatripatel.marketplace.feature.catalog.productDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketplaceTheme {
                MarketplaceNavHost()
            }
        }
    }
}

/** The app module only wires feature graphs together; features never depend on each other. */
@Composable
fun MarketplaceNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = CatalogDestination) {
        catalogScreen(
            onProductClick = navController::navigateToProductDetail,
            onCartClick = navController::navigateToCart,
        )
        productDetailScreen(
            onBack = { navController.popBackStack() },
            onCartClick = navController::navigateToCart,
        )
        cartScreen(
            onBack = { navController.popBackStack() },
        )
    }
}
