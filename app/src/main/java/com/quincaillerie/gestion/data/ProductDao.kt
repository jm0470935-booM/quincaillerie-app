package com.quincaillerie.gestion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE quantity <= minStock ORDER BY quantity ASC")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET quantity = quantity - :amount WHERE id = :productId")
    suspend fun decreaseStock(productId: Long, amount: Int)

    @Query("UPDATE products SET quantity = quantity + :amount WHERE id = :productId")
    suspend fun increaseStock(productId: Long, amount: Int)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}
