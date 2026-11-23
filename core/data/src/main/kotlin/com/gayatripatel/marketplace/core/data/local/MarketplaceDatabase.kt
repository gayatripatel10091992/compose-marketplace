package com.gayatripatel.marketplace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class, CartItemEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
