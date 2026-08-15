package com.quincaillerie.gestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quincaillerie.gestion.data.Product
import com.quincaillerie.gestion.data.ProductDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(private val dao: ProductDao) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val products: StateFlow<List<Product>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) dao.getAllProducts() else dao.searchProducts(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = dao.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addProduct(product: Product) {
        viewModelScope.launch { dao.insertProduct(product) }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { dao.updateProduct(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { dao.deleteProduct(product) }
    }
}
