package com.gayatripatel.marketplace.feature.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.CartSummary
import com.gayatripatel.marketplace.core.model.asPrice
import com.gayatripatel.marketplace.core.ui.EmptyState
import com.gayatripatel.marketplace.core.ui.ProductThumbnail

@Composable
internal fun CartRoute(
    onBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val placedOrderTotal by viewModel.placedOrderTotalCents.collectAsStateWithLifecycle()

    CartScreen(
        summary = summary,
        placedOrderTotalCents = placedOrderTotal,
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onRemove = viewModel::remove,
        onPlaceOrder = viewModel::onPlaceOrder,
        onOrderConfirmationDismissed = {
            viewModel.onOrderConfirmationDismissed()
            onBack()
        },
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    summary: CartSummary,
    placedOrderTotalCents: Long?,
    onIncrease: (CartLine) -> Unit,
    onDecrease: (CartLine) -> Unit,
    onRemove: (CartLine) -> Unit,
    onPlaceOrder: () -> Unit,
    onOrderConfirmationDismissed: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (placedOrderTotalCents != null) {
        AlertDialog(
            onDismissRequest = onOrderConfirmationDismissed,
            confirmButton = { TextButton(onClick = onOrderConfirmationDismissed) { Text("Done") } },
            title = { Text("Order placed") },
            text = { Text("Your order of ${placedOrderTotalCents.asPrice()} was sent to the sellers.") },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            if (!summary.isEmpty) OrderSummary(summary = summary, onPlaceOrder = onPlaceOrder)
        },
    ) { padding ->
        if (summary.isEmpty) {
            EmptyState(
                title = "Your cart is empty",
                message = "Browse the catalog and add a lot to get started.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(summary.lines, key = { it.product.id }) { line ->
                    CartLineItem(
                        line = line,
                        onIncrease = { onIncrease(line) },
                        onDecrease = { onDecrease(line) },
                        onRemove = { onRemove(line) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CartLineItem(
    line: CartLine,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductThumbnail(line.product, size = 48.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = line.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${line.product.pricePerKgCents.asPrice()} / kg · ${line.product.sellerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(onClick = onDecrease) { Text("−", style = MaterialTheme.typography.titleMedium) }
                    Text("${line.quantityKg} kg", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    FilledTonalIconButton(onClick = onIncrease) { Icon(Icons.Filled.Add, contentDescription = "Increase") }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
                Text(line.lineTotalCents.asPrice(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OrderSummary(summary: CartSummary, onPlaceOrder: () -> Unit) {
    Surface(tonalElevation = 3.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SummaryRow("Subtotal · ${summary.totalKg} kg", summary.subtotalCents.asPrice())
            if (summary.bulkDiscountCents > 0) {
                SummaryRow(
                    label = "Bulk discount (${CartSummary.BULK_DISCOUNT_PERCENT}%)",
                    value = "-" + summary.bulkDiscountCents.asPrice(),
                    highlight = true,
                )
            } else {
                Text(
                    text = "Add ${CartSummary.BULK_THRESHOLD_KG - summary.totalKg} kg more for ${CartSummary.BULK_DISCOUNT_PERCENT}% off",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            SummaryRow("Platform fee", summary.platformFeeCents.asPrice())
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
            SummaryRow("Total", summary.totalCents.asPrice(), bold = true)
            Spacer(Modifier.height(4.dp))
            Button(onClick = onPlaceOrder, modifier = Modifier.fillMaxWidth()) {
                Text("Place order")
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false, highlight: Boolean = false) {
    val color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val weight = if (bold) FontWeight.Bold else FontWeight.Normal
    val style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    Row(Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), style = style, fontWeight = weight, color = color)
        Text(value, style = style, fontWeight = weight, color = color)
    }
}
