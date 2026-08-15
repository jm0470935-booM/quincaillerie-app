package com.quincaillerie.gestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quincaillerie.gestion.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class CartLine(
    val product: Product,
    val quantity: Int
)

class SaleViewModel(
    private val productDao: ProductDao,
    private val saleDao: SaleDao
) : ViewModel() {

    private val _cart = MutableStateFlow<List<CartLine>>(emptyList())
    val cart: StateFlow<List<CartLine>> = _cart

    val cartTotal: StateFlow<Double> = _cart
        .map { lines -> lines.sumOf { it.product.unitPrice * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val todaySalesTotal: StateFlow<Double> = saleDao.getTotalSalesSince(startOfToday())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todaySales: StateFlow<List<Sale>> = saleDao.getSalesSince(startOfToday())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToCart(product: Product, quantity: Int) {
        if (quantity <= 0) return
        val current = _cart.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartLine(product, quantity))
        }
        _cart.value = current
    }

    fun removeFromCart(productId: Long) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun checkout(onComplete: (Boolean, String) -> Unit) {
        val lines = _cart.value
        if (lines.isEmpty()) {
            onComplete(false, "Le panier est vide")
            return
        }
        viewModelScope.launch {
            val total = lines.sumOf { it.product.unitPrice * it.quantity }
            val saleId = saleDao.insertSale(Sale(total = total))
            val items = lines.map {
                SaleItem(
                    saleId = saleId,
                    productId = it.product.id,
                    productName = it.product.name,
                    quantity = it.quantity,
                    unitPrice = it.product.unitPrice
                )
            }
            saleDao.insertSaleItems(items)
            lines.forEach { line ->
                productDao.decreaseStock(line.product.id, line.quantity)
            }
            clearCart()
            onComplete(true, "Vente enregistrée avec succès")
        }
    }
}
