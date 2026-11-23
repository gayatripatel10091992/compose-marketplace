package com.gayatripatel.marketplace.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gayatripatel.marketplace.core.domain.GetProductsUseCase
import com.gayatripatel.marketplace.core.domain.ObserveCartSummaryUseCase
import com.gayatripatel.marketplace.core.model.Category
import com.gayatripatel.marketplace.core.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    getProducts: GetProductsUseCase,
    observeCartSummary: ObserveCartSummaryUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _category = MutableStateFlow<Category?>(null)
    val category: StateFlow<Category?> = _category.asStateFlow()

    /** Debounced search + category filter, backed by a Room PagingSource and cached across configuration changes. */
    val products: Flow<PagingData<Product>> =
        combine(_query.debounce(SEARCH_DEBOUNCE_MS), _category) { query, category -> query to category }
            .distinctUntilChanged()
            .flatMapLatest { (query, category) -> getProducts(query, category) }
            .cachedIn(viewModelScope)

    val cartItemCount: StateFlow<Int> = observeCartSummary()
        .map { it.lines.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onCategorySelected(category: Category) {
        _category.value = if (_category.value == category) null else category
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
