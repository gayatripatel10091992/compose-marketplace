package com.gayatripatel.marketplace.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gayatripatel.marketplace.core.domain.ObserveCartSummaryUseCase
import com.gayatripatel.marketplace.core.domain.PlaceOrderUseCase
import com.gayatripatel.marketplace.core.domain.UpdateCartQuantityUseCase
import com.gayatripatel.marketplace.core.model.CartLine
import com.gayatripatel.marketplace.core.model.CartSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCartSummary: ObserveCartSummaryUseCase,
    private val updateQuantity: UpdateCartQuantityUseCase,
    private val placeOrder: PlaceOrderUseCase,
) : ViewModel() {

    val summary: StateFlow<CartSummary> = observeCartSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartSummary())

    /** Total of the order that was just placed, or null when no confirmation is showing. */
    private val _placedOrderTotalCents = MutableStateFlow<Long?>(null)
    val placedOrderTotalCents: StateFlow<Long?> = _placedOrderTotalCents.asStateFlow()

    fun increase(line: CartLine) = setQuantity(line, line.quantityKg + CartSummary.QUANTITY_STEP_KG)

    fun decrease(line: CartLine) = setQuantity(line, line.quantityKg - CartSummary.QUANTITY_STEP_KG)

    fun remove(line: CartLine) = setQuantity(line, 0)

    fun onPlaceOrder() {
        val total = summary.value.totalCents
        viewModelScope.launch {
            placeOrder()
            _placedOrderTotalCents.value = total
        }
    }

    fun onOrderConfirmationDismissed() {
        _placedOrderTotalCents.value = null
    }

    private fun setQuantity(line: CartLine, kg: Int) {
        viewModelScope.launch {
            updateQuantity(line.product.id, kg.coerceAtMost(line.product.availableKg))
        }
    }
}
