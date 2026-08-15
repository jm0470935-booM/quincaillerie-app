package com.quincaillerie.gestion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val total: Double
)
