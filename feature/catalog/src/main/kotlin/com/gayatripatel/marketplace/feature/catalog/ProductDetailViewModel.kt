package com.gayatripatel.marketplace.feature.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gayatripatel.marketplace.core.domain.AddToCartUseCase
import com.gayatripatel.marketplace.core.domain.GetProductUseCase
import com.gayatripatel.marketplace.core.model.CartSummary
import com.gayatripatel.marketplace.core.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getProduct: GetProductUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) : ViewModel() {

    private val productId: Long = savedStateHandle.toRoute<ProductDetailDestination>().id

    val product: StateFlow<Product?> = getProduct(productId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _quantityKg = MutableStateFlow(CartSummary.QUANTITY_STEP_KG * 5)
    val quantityKg: StateFlow<Int> = _quantityKg.asStateFlow()

    private val _showAddedMessage = MutableStateFlow(false)
    val showAddedMessage: StateFlow<Boolean> = _showAddedMessage.asStateFlow()

    fun onQuantityChange(kg: Int) {
        val max = product.value?.availableKg ?: Int.MAX_VALUE
        _quantityKg.value = kg.coerceIn(CartSummary.QUANTITY_STEP_KG, max)
    }

    fun addToCart() {
        viewModelScope.launch {
            addToCartUseCase(productId, _quantityKg.value)
            _showAddedMessage.value = true
        }
    }

    fun onAddedMessageShown() {
        _showAddedMessage.value = false
    }
}
