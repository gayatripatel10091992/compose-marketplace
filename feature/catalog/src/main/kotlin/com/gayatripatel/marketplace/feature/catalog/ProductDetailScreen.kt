package com.gayatripatel.marketplace.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gayatripatel.marketplace.core.model.CartSummary
import com.gayatripatel.marketplace.core.model.Product
import com.gayatripatel.marketplace.core.model.asPrice
import com.gayatripatel.marketplace.core.ui.CategoryBadge
import com.gayatripatel.marketplace.core.ui.ProductHero
import com.gayatripatel.marketplace.core.ui.RatingText

@Composable
internal fun ProductDetailRoute(
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val product by viewModel.product.collectAsStateWithLifecycle()
    val quantityKg by viewModel.quantityKg.collectAsStateWithLifecycle()
    val showAddedMessage by viewModel.showAddedMessage.collectAsStateWithLifecycle()

    ProductDetailScreen(
        product = product,
        quantityKg = quantityKg,
        showAddedMessage = showAddedMessage,
        onQuantityChange = viewModel::onQuantityChange,
        onAddToCart = viewModel::addToCart,
        onAddedMessageShown = viewModel::onAddedMessageShown,
        onBack = onBack,
        onCartClick = onCartClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product?,
    quantityKg: Int,
    showAddedMessage: Boolean,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onAddedMessageShown: () -> Unit,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(showAddedMessage) {
        if (showAddedMessage) {
            val result = snackbarHostState.showSnackbar(message = "Added to cart", actionLabel = "View cart")
            if (result == SnackbarResult.ActionPerformed) onCartClick()
            onAddedMessageShown()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Listing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                },
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(tonalElevation = 3.dp) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text("Add $quantityKg kg · ${(product.pricePerKgCents * quantityKg).asPrice()}")
                    }
                }
            }
        },
    ) { padding ->
        if (product == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProductHero(product)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryBadge(product.category)
                    RatingText(product.rating)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${product.sellerName} · ${product.location}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            OutlinedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp)) {
                    Fact(label = "Price", value = "${product.pricePerKgCents.asPrice()} / kg", modifier = Modifier.weight(1f))
                    Fact(label = "Available", value = "${product.availableKg} kg", modifier = Modifier.weight(1f))
                }
            }

            Text(product.description, style = MaterialTheme.typography.bodyMedium)

            Column {
                Text("Quantity", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                QuantityStepper(
                    quantityKg = quantityKg,
                    onDecrease = { onQuantityChange(quantityKg - CartSummary.QUANTITY_STEP_KG) },
                    onIncrease = { onQuantityChange(quantityKg + CartSummary.QUANTITY_STEP_KG) },
                )
                if (quantityKg >= CartSummary.BULK_THRESHOLD_KG) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Bulk order: ${CartSummary.BULK_DISCOUNT_PERCENT}% off applies at checkout",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun Fact(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun QuantityStepper(
    quantityKg: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilledTonalIconButton(onClick = onDecrease) {
            Text("−", style = MaterialTheme.typography.titleLarge)
        }
        Text("$quantityKg kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        FilledTonalIconButton(onClick = onIncrease) {
            Icon(Icons.Filled.Add, contentDescription = "Increase quantity")
        }
    }
}
