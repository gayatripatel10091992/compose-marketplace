package com.gayatripatel.marketplace.core.domain

import androidx.paging.PagingData
import com.gayatripatel.marketplace.core.model.CartSummary
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    operator fun invoke(query: String, category: Category?): Flow<PagingData<Product>> =
        repository.pagedProducts(query.trim(), category)
}

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    operator fun invoke(id: Long): Flow<Product?> = repository.product(id)
}

class ObserveCartSummaryUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    operator fun invoke(): Flow<CartSummary> = repository.cartLines().map(::CartSummary)
}

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(productId: Long, quantityKg: Int) {
        require(quantityKg > 0) { "Quantity must be positive, was $quantityKg" }
        repository.add(productId, quantityKg)
    }
}

class UpdateCartQuantityUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    /** A quantity of zero or less removes the line from the cart. */
    suspend operator fun invoke(productId: Long, quantityKg: Int) =
        repository.setQuantity(productId, quantityKg.coerceAtLeast(0))
}

class PlaceOrderUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    /** Demo checkout: there is no payment backend, so placing an order just clears the cart. */
    suspend operator fun invoke() = repository.clear()
}
