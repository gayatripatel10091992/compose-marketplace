package com.gayatripatel.marketplace.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.Product
import com.gayatripatel.marketplace.core.ui.EmptyState
import com.gayatripatel.marketplace.core.ui.ProductCard

@Composable
internal fun CatalogRoute(
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val category by viewModel.category.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()

    CatalogScreen(
        query = query,
        selectedCategory = category,
        products = products,
        cartItemCount = cartItemCount,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
    )
}

/** Stateless screen: everything it shows comes in as parameters, so it is easy to preview and screenshot-test. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    query: String,
    selectedCategory: Category?,
    products: LazyPagingItems<Product>,
    cartItemCount: Int,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ReLoop Market", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Recyclables marketplace",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) Badge { Text(cartItemCount.toString()) }
                            },
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search materials or sellers") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(Category.entries) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.label) },
                    )
                }
            }
            ProductList(products = products, onProductClick = onProductClick)
        }
    }
}

@Composable
private fun ProductList(
    products: LazyPagingItems<Product>,
    onProductClick: (Long) -> Unit,
) {
    val refresh = products.loadState.refresh
    when {
        refresh is LoadState.Error && products.itemCount == 0 -> EmptyState(
            title = "Couldn't load the catalog",
            message = refresh.error.message ?: "Something went wrong.",
            action = { Button(onClick = { products.retry() }) { Text("Retry") } },
        )

        refresh is LoadState.Loading && products.itemCount == 0 -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        products.itemCount == 0 -> EmptyState(
            title = "No matches",
            message = "Try a different search or clear the category filter.",
        )

        else -> LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = products.itemCount,
                key = products.itemKey { it.id },
                contentType = products.itemContentType { "product" },
            ) { index ->
                products[index]?.let { product ->
                    ProductCard(product = product, onClick = { onProductClick(product.id) })
                }
            }
            if (products.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(Modifier.size(24.dp)) }
                }
            }
        }
    }
}
