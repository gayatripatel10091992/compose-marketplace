package com.gayatripatel.marketplace.core.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.Product

@Entity(
    tableName = "products",
    indices = [Index("category"), Index("name")],
)
data class ProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String,
    val pricePerKgCents: Long,
    val sellerName: String,
    val location: String,
    val availableKg: Int,
    val rating: Double,
    val description: String,
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantityKg: Int,
)

data class CartLineRow(
    @Embedded val product: ProductEntity,
    @ColumnInfo(name = "quantityKg") val quantityKg: Int,
)

fun ProductEntity.toModel() = Product(
    id = id,
    name = name,
    category = Category.valueOf(category),
    pricePerKgCents = pricePerKgCents,
    sellerName = sellerName,
    location = location,
    availableKg = availableKg,
    rating = rating,
    description = description,
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    category = category.name,
    pricePerKgCents = pricePerKgCents,
    sellerName = sellerName,
    location = location,
    availableKg = availableKg,
    rating = rating,
    description = description,
)

fun CartLineRow.toModel() = CartLine(product = product.toModel(), quantityKg = quantityKg)
