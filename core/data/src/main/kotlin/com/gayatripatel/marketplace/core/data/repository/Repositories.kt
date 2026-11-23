package com.gayatripatel.marketplace.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gayatripatel.marketplace.core.data.local.CartDao
import com.gayatripatel.marketplace.core.data.local.ProductDao
import com.gayatripatel.marketplace.core.data.local.toEntity
import com.gayatripatel.marketplace.core.data.local.toModel
import com.gayatripatel.marketplace.core.domain.CartRepository
import com.gayatripatel.marketplace.core.domain.ProductRepository
import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.Product
import com.gayatripatel.marketplace.core.model.SampleCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/** Seeds the local catalog once, on first use. In a real app this would be a sync with the backend. */
@Singleton
class CatalogSeeder @Inject constructor(
    private val productDao: ProductDao,
) {
    private val mutex = Mutex()

    @Volatile
    private var seeded = false

    suspend fun ensureSeeded() {
        if (seeded) return
        mutex.withLock {
            if (seeded) return
            if (productDao.count() == 0) {
                productDao.insertAll(SampleCatalog.generate().map { it.toEntity() })
            }
            seeded = true
        }
    }
}

class OfflineProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val seeder: CatalogSeeder,
) : ProductRepository {

    override fun pagedProducts(query: String, category: Category?): Flow<PagingData<Product>> = flow {
        seeder.ensureSeeded()
        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE / 2, enablePlaceholders = false),
            pagingSourceFactory = { productDao.pagingSource(query, category?.name) },
        )
        emitAll(pager.flow.map { page -> page.map { it.toModel() } })
    }

    override fun product(id: Long): Flow<Product?> = flow {
        seeder.ensureSeeded()
        emitAll(productDao.observe(id).map { it?.toModel() })
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

class OfflineCartRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartRepository {

    override fun cartLines(): Flow<List<CartLine>> =
        cartDao.observeLines().map { rows -> rows.map { it.toModel() } }

    override suspend fun add(productId: Long, quantityKg: Int) = cartDao.addQuantity(productId, quantityKg)

    override suspend fun setQuantity(productId: Long, quantityKg: Int) {
        if (quantityKg <= 0) {
            cartDao.delete(productId)
        } else {
            cartDao.upsert(com.gayatripatel.marketplace.core.data.local.CartItemEntity(productId, quantityKg))
        }
    }

    override suspend fun clear() = cartDao.clear()
}
