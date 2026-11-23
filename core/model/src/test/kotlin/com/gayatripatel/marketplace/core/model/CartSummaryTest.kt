package com.gayatripatel.marketplace.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartSummaryTest {

    private val product = SampleCatalog.generate(1).first().copy(pricePerKgCents = 100)

    @Test
    fun emptyCart_hasNoFeesOrTotal() {
        val summary = CartSummary()
        assertTrue(summary.isEmpty)
        assertEquals(0L, summary.platformFeeCents)
        assertEquals(0L, summary.totalCents)
    }

    @Test
    fun smallOrder_addsPlatformFee_withoutDiscount() {
        val summary = CartSummary(listOf(CartLine(product, quantityKg = 100)))
        assertEquals(10_000L, summary.subtotalCents)
        assertEquals(0L, summary.bulkDiscountCents)
        assertEquals(10_000L + CartSummary.PLATFORM_FEE_CENTS, summary.totalCents)
    }

    @Test
    fun bulkOrder_getsFivePercentDiscount() {
        val summary = CartSummary(listOf(CartLine(product, quantityKg = CartSummary.BULK_THRESHOLD_KG)))
        assertEquals(50_000L, summary.subtotalCents)
        assertEquals(2_500L, summary.bulkDiscountCents)
        assertEquals(50_000L - 2_500L + CartSummary.PLATFORM_FEE_CENTS, summary.totalCents)
    }

    @Test
    fun priceFormatting_usesThousandsSeparator() {
        assertEquals("$1,234.56", 123_456L.asPrice())
        assertEquals("$0.07", 7L.asPrice())
    }
}
