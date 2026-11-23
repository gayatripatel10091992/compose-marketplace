package com.gayatripatel.marketplace.feature.cart

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.CartSummary
import com.gayatripatel.marketplace.core.model.SampleCatalog
import com.gayatripatel.marketplace.core.ui.theme.MarketplaceTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class CartScreenshotTest {

    private val products = SampleCatalog.generate(17)

    @Test
    fun cart_with_bulk_discount() {
        val summary = CartSummary(
            listOf(
                CartLine(products[0], quantityKg = 250),
                CartLine(products[4], quantityKg = 120),
                CartLine(products[8], quantityKg = 200),
            ),
        )
        captureRoboImage("build/outputs/roborazzi/05_cart_bulk_discount.png") {
            MarketplaceTheme(darkTheme = false) { Cart(summary) }
        }
    }

    @Test
    fun cart_dark() {
        val summary = CartSummary(listOf(CartLine(products[5], quantityKg = 40), CartLine(products[14], quantityKg = 10)))
        captureRoboImage("build/outputs/roborazzi/06_cart_dark.png") {
            MarketplaceTheme(darkTheme = true) { Cart(summary) }
        }
    }

    @Test
    fun cart_empty() {
        captureRoboImage("build/outputs/roborazzi/07_cart_empty.png") {
            MarketplaceTheme(darkTheme = false) { Cart(CartSummary()) }
        }
    }

    @androidx.compose.runtime.Composable
    private fun Cart(summary: CartSummary) {
        CartScreen(
            summary = summary,
            placedOrderTotalCents = null,
            onIncrease = {},
            onDecrease = {},
            onRemove = {},
            onPlaceOrder = {},
            onOrderConfirmationDismissed = {},
            onBack = {},
        )
    }
}
