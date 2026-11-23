package com.gayatripatel.marketplace.core.model

data class CartLine(
    val product: Product,
    val quantityKg: Int,
) {
    val lineTotalCents: Long get() = product.pricePerKgCents * quantityKg
}

/**
 * Pricing rules for an order:
 * - a flat platform fee per order,
 * - a bulk discount once the order reaches [BULK_THRESHOLD_KG].
 */
data class CartSummary(
    val lines: List<CartLine> = emptyList(),
) {
    val isEmpty: Boolean get() = lines.isEmpty()

    val totalKg: Int = lines.sumOf { it.quantityKg }

    val subtotalCents: Long = lines.sumOf { it.lineTotalCents }

    val bulkDiscountCents: Long =
        if (totalKg >= BULK_THRESHOLD_KG) subtotalCents * BULK_DISCOUNT_PERCENT / 100 else 0L

    val platformFeeCents: Long = if (lines.isEmpty()) 0L else PLATFORM_FEE_CENTS

    val totalCents: Long = subtotalCents - bulkDiscountCents + platformFeeCents

    companion object {
        const val BULK_THRESHOLD_KG = 500
        const val BULK_DISCOUNT_PERCENT = 5L
        const val PLATFORM_FEE_CENTS = 499L
        const val QUANTITY_STEP_KG = 10
    }
}
