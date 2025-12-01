
---

# Restaurant Guide – Android App

A simple restaurant management application built for the COMP3074 – Android Development course.
This project demonstrates Android fundamentals including Room Database, RecyclerView, Activities, Intents, Permissions, Location APIs, and basic UI design.

---

## Overview

Restaurant Guide allows users to:

* Add new restaurants
* Edit existing restaurants
* View restaurant details
* Delete restaurants
* Search restaurants by name or tags
* Capture the device’s current GPS location
* Launch system apps (Phone, Google Maps, Email)
* Navigate through dedicated screens such as Splash, Main, Add/Edit, Details, and About

Restaurant data is stored locally using Room.

---

## Features

### Add / Edit Restaurants

Users can create or update restaurants with the following fields:

* Name
* Address
* Phone
* Tags
* Description
* Star Rating
* Latitude & longitude (via location capture button)

### Capture Device Location

The app uses the FusedLocationProviderClient and requires:

* ACCESS_FINE_LOCATION
* ACCESS_COARSE_LOCATION

### Restaurant Details Screen

The details page shows all restaurant information and provides:

* Call button (opens dialer)
* View on Map (opens Google Maps with coordinates or address)
* Get Directions (turn-by-turn navigation)
* Share via Email
* Edit
* Delete
* Back to Home

### Search Functionality

The main list allows searching by name or tags using a SearchView.
Search is performed with Room LIKE queries.

### Persistent Data Storage with Room

Room is used for full offline data storage.

Components:

* Entity: Restaurant.kt
* DAO: RestaurantDao.kt
* Database: AppDatabase.kt

### General UI and Navigation

* Splash screen
* Main list with RecyclerView
* Add/Edit form
* Details page
* About page
* Back navigation on all screens

---

## Architecture

The app uses a straight-forward Activity-based architecture.

```
MainActivity (restaurant list + search)
 ├── AddEditRestaurantActivity (form)
 └── DetailsActivity (restaurant detail view)
```

Room structure:

```
Restaurant.kt       (Entity)
RestaurantDao.kt    (DAO)
AppDatabase.kt      (RoomDatabase Singleton)
```

Recycler adapter:

```
RestaurantListAdapter.kt
```

---

## ROOM Database Structure

### Entity (Restaurant.kt)

```
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
    val longitude: Double?
)
```

### DAO (RestaurantDao.kt)

```
@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants ORDER BY name")
    fun getAll(): List<Restaurant>

    @Query("SELECT * FROM restaurants WHERE id = :arg0 LIMIT 1")
    fun getById(arg0: Long): Restaurant?

    @Query("""
        SELECT * FROM restaurants
        WHERE name LIKE '%' || :arg0 || '%'
           OR tags LIKE '%' || :arg0 || '%'
        ORDER BY name
    """)
    fun search(arg0: String): List<Restaurant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(restaurant: Restaurant)

    @Update
    fun update(restaurant: Restaurant)

    @Delete
    fun delete(restaurant: Restaurant)
}
```

### Database (AppDatabase.kt)

```
@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}
```

---

## Navigation Flow

### App Launch

```
SplashActivity → MainActivity
```

### From MainActivity

```
+ Add button → AddEditRestaurantActivity
Tap item → DetailsActivity
Menu option → AboutActivity
```

### From DetailsActivity

```
Edit → AddEditRestaurantActivity
Delete → finish() → MainActivity
Back to Home → finish()
```

---

## Requirements

* Android Studio (Hedgehog or newer)
* Minimum SDK: 21
* Target SDK: 34
* Runtime permissions required:

  * ACCESS_FINE_LOCATION
  * ACCESS_COARSE_LOCATION
  * INTERNET

---

## How to Run

1. Clone the repository

   ```
   git clone https://github.com/<your-username>/RestaurantGuide.git
   ```
2. Open the project in Android Studio
3. Sync Gradle
4. Run on emulator or physical device
5. Allow location permissions when prompted

---

## Project Status

Fully completed and tested according to assignment requirements.
Implements Room, RecyclerView, implicit intents, permissions, navigation, and full CRUD functionality.


