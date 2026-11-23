package com.gayatripatel.marketplace.core.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query(
        """
        SELECT * FROM products
        WHERE (:category IS NULL OR category = :category)
          AND (name LIKE '%' || :query || '%' OR sellerName LIKE '%' || :query || '%')
        ORDER BY name
        """,
    )
    fun pagingSource(query: String, category: String?): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun observe(id: Long): Flow<ProductEntity?>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProductEntity>)
}

@Dao
abstract class CartDao {

    @Query(
        """
        SELECT p.*, c.quantityKg AS quantityKg
        FROM cart_items c INNER JOIN products p ON p.id = c.productId
        ORDER BY p.name
        """,
    )
    abstract fun observeLines(): Flow<List<CartLineRow>>

    @Query("SELECT quantityKg FROM cart_items WHERE productId = :productId")
    abstract suspend fun quantity(productId: Long): Int?

    @Upsert
    abstract suspend fun upsert(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    abstract suspend fun delete(productId: Long)

    @Query("DELETE FROM cart_items")
    abstract suspend fun clear()

    /** Read-modify-write in one transaction so rapid taps never lose an update. */
    @Transaction
    open suspend fun addQuantity(productId: Long, deltaKg: Int) {
        val current = quantity(productId) ?: 0
        upsert(CartItemEntity(productId, current + deltaKg))
    }
}
