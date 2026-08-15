package com.quincaillerie.gestion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val barcode: String? = null,
    val unitPrice: Double,
    val quantity: Int,
    val minStock: Int = 5
) {
    val isLowStock: Boolean
        get() = quantity <= minStock
}
