package com.example.restaurantguide

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.restaurantguide.data.AppDatabase
import com.example.restaurantguide.data.Restaurant
import com.example.restaurantguide.databinding.ActivityAddEditRestaurantBinding
import kotlinx.coroutines.launch

class AddEditRestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditRestaurantBinding
    private val dao by lazy { AppDatabase.getDatabase(this).restaurantDao() }

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var editingId: Long? = null

    private val requestLoc =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) captureLocation()
            else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ⭐ Force keyboard to open when screen appears
        binding.etName.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        // Existing ID loading logic
        editingId = intent.getLongExtra("id", -1L).takeIf { it != -1L }

        // --- BUTTON: Capture location ---
        binding.btnGetLocation.setOnClickListener {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestLoc.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                captureLocation()
            }
        }

        // --- BUTTON: Save restaurant ---
        binding.btnSave.setOnClickListener { saveRestaurant() }

        // --- BUTTON: Back to Home ---
        binding.btnBack.setOnClickListener {
            finish()  // Return to MainActivity
        }

        // Load data if editing
        editingId?.let { id -> loadRestaurant(id) }
    }

    private fun loadRestaurant(id: Long) {
        lifecycleScope.launch {
            val r = dao.getById(id) ?: return@launch
            binding.etName.setText(r.name)
            binding.etAddress.setText(r.address)
            binding.etPhone.setText(r.phone)
            binding.etTags.setText(r.tags)
            binding.etDescription.setText(r.description)
            binding.rbRating.rating = r.rating.toFloat()
            latitude = r.latitude
            longitude = r.longitude
        }
    }

    @SuppressLint("MissingPermission")
    private fun captureLocation() {
        val client = com.google.android.gms.location.LocationServices
            .getFusedLocationProviderClient(this)

        client.lastLocation.addOnSuccessListener { loc: Location? ->
            if (loc != null) {
                latitude = loc.latitude
                longitude = loc.longitude
                Toast.makeText(this, "Location captured!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No location available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRestaurant() {
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isBlank() || address.isBlank()) {
            Toast.makeText(this, "Name & Address are required", Toast.LENGTH_SHORT).show()
            return
        }

        val rest = Restaurant(
            id = editingId ?: 0L,
            name = name,
            address = address,
            phone = binding.etPhone.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            tags = binding.etTags.text.toString().trim(),
            rating = binding.rbRating.rating.toInt(),
            latitude = latitude,
            longitude = longitude
        )

        lifecycleScope.launch {
            if (editingId != null) {
                dao.update(rest)
                Toast.makeText(this@AddEditRestaurantActivity, "Updated", Toast.LENGTH_SHORT).show()
            } else {
                dao.insert(rest)
                Toast.makeText(this@AddEditRestaurantActivity, "Saved", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
