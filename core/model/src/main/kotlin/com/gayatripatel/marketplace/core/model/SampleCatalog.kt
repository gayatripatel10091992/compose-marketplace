package com.gayatripatel.marketplace.core.model

/**
 * Deterministic demo data. Used to seed the local database and by previews/screenshot tests,
 * so every screenshot is reproducible.
 */
object SampleCatalog {

    private data class Material(val name: String, val category: Category, val basePriceCents: Long)

    private val materials = listOf(
        Material("PET Bottle Flakes", Category.PLASTIC, 42),
        Material("HDPE Regrind", Category.PLASTIC, 55),
        Material("LDPE Film Bales", Category.PLASTIC, 31),
        Material("PP Granules", Category.PLASTIC, 68),
        Material("Aluminium Cans (UBC)", Category.METAL, 120),
        Material("Copper Wire Scrap", Category.METAL, 690),
        Material("Stainless Steel Offcuts", Category.METAL, 95),
        Material("Brass Turnings", Category.METAL, 410),
        Material("Corrugated Cardboard (OCC)", Category.PAPER, 14),
        Material("Office Paper Mix", Category.PAPER, 18),
        Material("Newsprint Bundles", Category.PAPER, 11),
        Material("Clear Glass Cullet", Category.GLASS, 9),
        Material("Amber Glass Cullet", Category.GLASS, 8),
        Material("Green Glass Cullet", Category.GLASS, 7),
        Material("Circuit Board Scrap", Category.E_WASTE, 880),
        Material("Li-ion Battery Packs", Category.E_WASTE, 350),
        Material("Mixed Cable Scrap", Category.E_WASTE, 210),
    )

    private val sellers = listOf(
        "GreenCycle Ltd" to "Antwerp, BE",
        "Urban Reclaim" to "Rotterdam, NL",
        "ReLoop Traders" to "Ahmedabad, IN",
        "EcoMetals GmbH" to "Hamburg, DE",
        "BlueBin Co." to "Austin, US",
        "Circular Works" to "Lyon, FR",
    )

    private val grades = listOf("Grade A", "Grade B", "Premium", "Bulk")

    fun generate(count: Int = 120): List<Product> = (0 until count).map { i ->
        val material = materials[i % materials.size]
        val round = i / materials.size
        val (seller, location) = sellers[(i * 7 + round) % sellers.size]
        val grade = grades[round % grades.size]
        Product(
            id = i + 1L,
            name = "${material.name} · $grade",
            category = material.category,
            pricePerKgCents = material.basePriceCents + (i % 5) * 3,
            sellerName = seller,
            location = location,
            availableKg = 50 + (i * 37) % 950,
            rating = 3.6 + ((i * 13) % 14) / 10.0,
            description = "$grade ${material.name.lowercase()} sorted and baled by $seller. " +
                "Moisture and contamination checked before dispatch. Pickup or delivery from $location.",
        )
    }
}
