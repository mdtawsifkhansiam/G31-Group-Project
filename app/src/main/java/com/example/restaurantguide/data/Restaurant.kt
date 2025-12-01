package com.example.restaurantguide.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String,
    val phone: String?,
    val description: String?,
    val tags: String?,
    val rating: Int,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: Long = System.currentTimeMillis()
)
