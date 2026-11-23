package com.gayatripatel.marketplace.feature.catalog

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object CatalogDestination

@Serializable
data class ProductDetailDestination(val id: Long)

fun NavController.navigateToProductDetail(productId: Long) {
    navigate(ProductDetailDestination(productId))
}

fun NavGraphBuilder.catalogScreen(
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit,
) {
    composable<CatalogDestination> {
        CatalogRoute(onProductClick = onProductClick, onCartClick = onCartClick)
    }
}

fun NavGraphBuilder.productDetailScreen(
    onBack: () -> Unit,
    onCartClick: () -> Unit,
) {
    composable<ProductDetailDestination> {
        ProductDetailRoute(onBack = onBack, onCartClick = onCartClick)
    }
}
