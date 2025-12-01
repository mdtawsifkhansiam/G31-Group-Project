package com.example.restaurantguide.data

import androidx.room.*

@Dao
interface RestaurantDao {

    // Get all restaurants, sorted by name
    @Query("SELECT * FROM restaurants ORDER BY name")
    fun getAll(): List<Restaurant>

    // Get a single restaurant by id
    @Query("SELECT * FROM restaurants WHERE id = :arg0 LIMIT 1")
    fun getById(arg0: Long): Restaurant?

    // Search by name or tags
    @Query(
        """
        SELECT * FROM restaurants
        WHERE name LIKE '%' || :arg0 || '%'
           OR tags LIKE '%' || :arg0 || '%'
        ORDER BY name
        """
    )
    fun search(arg0: String): List<Restaurant>

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(restaurant: Restaurant)

    // Update
    @Update
    fun update(restaurant: Restaurant)

    // Delete
    @Delete
    fun delete(restaurant: Restaurant)
}
