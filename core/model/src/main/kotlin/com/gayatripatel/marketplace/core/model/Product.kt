package com.gayatripatel.marketplace.core.model

import java.util.Locale

enum class Category(val label: String) {
    PLASTIC("Plastic"),
    METAL("Metal"),
    PAPER("Paper"),
    GLASS("Glass"),
    E_WASTE("E-waste"),
}

/**
 * A lot of recyclable material listed by a seller.
 * Prices are stored in minor units (cents) to avoid floating point rounding.
 */
data class Product(
    val id: Long,
    val name: String,
    val category: Category,
    val pricePerKgCents: Long,
    val sellerName: String,
    val location: String,
    val availableKg: Int,
    val rating: Double,
    val description: String,
)

/** Formats an amount in cents as US dollars, e.g. 123456 -> "$1,234.56". */
fun Long.asPrice(): String {
    val abs = kotlin.math.abs(this)
    val sign = if (this < 0) "-" else ""
    val whole = String.format(Locale.US, "%,d", abs / 100)
    val cents = (abs % 100).toString().padStart(2, '0')
    return "$sign$$whole.$cents"
}

fun Double.asRating(): String = String.format(Locale.US, "%.1f", this)
