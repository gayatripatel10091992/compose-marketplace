package com.gayatripatel.marketplace.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.SampleCatalog
import com.gayatripatel.marketplace.core.ui.theme.MarketplaceTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Screenshot tests rendered on the JVM with Robolectric + Roborazzi.
 * `./gradlew recordRoborazziDebug` writes the PNGs that CI copies into docs/screenshots.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class CatalogScreenshotTest {

    private val products = SampleCatalog.generate(17)

    @Test
    fun catalog_light() {
        captureRoboImage("build/outputs/roborazzi/01_catalog_light.png") {
            MarketplaceTheme(darkTheme = false) { CatalogContent() }
        }
    }

    @Test
    fun catalog_dark() {
        captureRoboImage("build/outputs/roborazzi/02_catalog_dark.png") {
            MarketplaceTheme(darkTheme = true) { CatalogContent() }
        }
    }

    @Test
    fun catalog_filtered_metal() {
        captureRoboImage("build/outputs/roborazzi/03_catalog_filter_metal.png") {
            MarketplaceTheme(darkTheme = false) {
                CatalogContent(category = Category.METAL)
            }
        }
    }

    @Test
    fun product_detail() {
        captureRoboImage("build/outputs/roborazzi/04_product_detail.png") {
            MarketplaceTheme(darkTheme = false) {
                ProductDetailScreen(
                    product = products[15], // Li-ion Battery Packs: 605 kg in stock
                    quantityKg = 500,
                    showAddedMessage = false,
                    onQuantityChange = {},
                    onAddToCart = {},
                    onAddedMessageShown = {},
                    onBack = {},
                    onCartClick = {},
                )
            }
        }
    }

    @Composable
    private fun CatalogContent(category: Category? = null, query: String = "") {
        val visible = remember(category, query) {
            products.filter { (category == null || it.category == category) && it.name.contains(query, ignoreCase = true) }
                .sortedBy { it.name }
        }
        val items = remember(visible) { flowOf(PagingData.from(visible)) }.collectAsLazyPagingItems()
        CatalogScreen(
            query = query,
            selectedCategory = category,
            products = items,
            cartItemCount = 3,
            onQueryChange = {},
            onCategorySelected = {},
            onProductClick = {},
            onCartClick = {},
        )
    }
}
