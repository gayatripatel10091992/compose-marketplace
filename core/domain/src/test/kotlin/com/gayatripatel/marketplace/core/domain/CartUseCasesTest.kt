package com.gayatripatel.marketplace.core.domain

import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.Product
import com.gayatripatel.marketplace.core.model.SampleCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartUseCasesTest {

    private val products = SampleCatalog.generate(3).associateBy { it.id }

    private class FakeCartRepository(private val products: Map<Long, Product>) : CartRepository {
        private val quantities = MutableStateFlow<Map<Long, Int>>(emptyMap())

        override fun cartLines(): Flow<List<CartLine>> =
            quantities.map { map -> map.map { (id, kg) -> CartLine(products.getValue(id), kg) } }

        override suspend fun add(productId: Long, quantityKg: Int) {
            val current = quantities.value[productId] ?: 0
            quantities.value = quantities.value + (productId to current + quantityKg)
        }

        override suspend fun setQuantity(productId: Long, quantityKg: Int) {
            quantities.value =
                if (quantityKg <= 0) quantities.value - productId else quantities.value + (productId to quantityKg)
        }

        override suspend fun clear() {
            quantities.value = emptyMap()
        }
    }

    @Test
    fun addingSameProductTwice_accumulatesQuantity() = runTest {
        val repo = FakeCartRepository(products)
        val add = AddToCartUseCase(repo)
        add(1, 10)
        add(1, 20)
        assertEquals(30, ObserveCartSummaryUseCase(repo)().first().totalKg)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addingZero_isRejected() = runTest {
        AddToCartUseCase(FakeCartRepository(products))(1, 0)
    }

    @Test
    fun negativeQuantity_removesLine() = runTest {
        val repo = FakeCartRepository(products)
        AddToCartUseCase(repo)(2, 10)
        UpdateCartQuantityUseCase(repo)(2, -10)
        assertTrue(ObserveCartSummaryUseCase(repo)().first().isEmpty)
    }

    @Test
    fun placeOrder_clearsCart() = runTest {
        val repo = FakeCartRepository(products)
        AddToCartUseCase(repo)(3, 10)
        PlaceOrderUseCase(repo)()
        assertTrue(ObserveCartSummaryUseCase(repo)().first().isEmpty)
    }
}
