package com.quincaillerie.gestion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItem>)

    @Query("SELECT * FROM sales ORDER BY dateMillis DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE dateMillis >= :startOfDay ORDER BY dateMillis DESC")
    fun getSalesSince(startOfDay: Long): Flow<List<Sale>>

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getItemsForSale(saleId: Long): List<SaleItem>

    @Query("""
        SELECT productName, SUM(quantity) as totalQty 
        FROM sale_items 
        WHERE saleId IN (SELECT id FROM sales WHERE dateMillis >= :startOfDay)
        GROUP BY productName 
        ORDER BY totalQty DESC 
        LIMIT 5
    """)
    suspend fun getTopProductsSince(startOfDay: Long): List<TopProduct>

    @Query("SELECT COALESCE(SUM(total), 0.0) FROM sales WHERE dateMillis >= :startOfDay")
    fun getTotalSalesSince(startOfDay: Long): Flow<Double>
}

data class TopProduct(
    val productName: String,
    val totalQty: Int
)
