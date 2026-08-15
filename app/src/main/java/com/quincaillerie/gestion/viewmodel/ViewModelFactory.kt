package com.quincaillerie.gestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quincaillerie.gestion.data.ProductDao
import com.quincaillerie.gestion.data.SaleDao

class ViewModelFactory(
    private val productDao: ProductDao,
    private val saleDao: SaleDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductViewModel::class.java) ->
                ProductViewModel(productDao) as T
            modelClass.isAssignableFrom(SaleViewModel::class.java) ->
                SaleViewModel(productDao, saleDao) as T
            else -> throw IllegalArgumentException("ViewModel inconnu: ${modelClass.name}")
        }
    }
}
