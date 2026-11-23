package com.gayatripatel.marketplace.core.domain

import androidx.paging.PagingData
import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun pagedProducts(query: String, category: Category?): Flow<PagingData<Product>>
    fun product(id: Long): Flow<Product?>
}

interface CartRepository {
    fun cartLines(): Flow<List<CartLine>>
    suspend fun add(productId: Long, quantityKg: Int)
    suspend fun setQuantity(productId: Long, quantityKg: Int)
    suspend fun clear()
}
