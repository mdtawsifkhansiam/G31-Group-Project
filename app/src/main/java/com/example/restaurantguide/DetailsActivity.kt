package com.example.restaurantguide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurantguide.data.AppDatabase
import com.example.restaurantguide.databinding.ActivityDetailsBinding
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val dao by lazy { AppDatabase.getDatabase(this).restaurantDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---- Back to home button ----
        // (works even before data finishes loading)
        binding.btnBack.setOnClickListener {
            finish()  // just close this screen and go back to MainActivity
        }

        // ---- Get id from intent ----
        val id = intent.getLongExtra("id", -1L)
        if (id == -1L) {
            finish()
            return
        }

        // ---- Load restaurant from DB ----
        lifecycleScope.launch {
            val r = dao.getById(id)
            if (r == null) {
                Toast.makeText(
                    this@DetailsActivity,
                    "Restaurant not found",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            binding.tvName.text = r.name
            binding.tvAddress.text = r.address
            binding.tvPhone.text = r.phone ?: ""
            binding.tvTags.text = r.tags ?: ""
            binding.tvDescription.text = r.description ?: ""
            binding.rbRating.rating = r.rating.toFloat()

            binding.btnCall.setOnClickListener {
                if (!r.phone.isNullOrBlank()) {
                    startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:${r.phone}")
                        )
                    )
                } else {
                    Toast.makeText(this@DetailsActivity, "No phone number", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnMap.setOnClickListener {
                val uri =
                    if (r.latitude != null && r.longitude != null)
                        Uri.parse("geo:${r.latitude},${r.longitude}?q=${Uri.encode(r.name)}")
                    else
                        Uri.parse("geo:0,0?q=${Uri.encode(r.address)}")

                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }

            binding.btnDirections.setOnClickListener {
                val uri =
                    if (r.latitude != null && r.longitude != null)
                        Uri.parse("google.navigation:q=${r.latitude},${r.longitude}")
                    else
                        Uri.parse("google.navigation:q=${Uri.encode(r.address)}")

                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }

            binding.btnShare.setOnClickListener {
                val email = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_SUBJECT, "Restaurant Recommendation")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${r.name}\n${r.address}\nPhone: ${r.phone ?: "N/A"}\nRating: ${r.rating}"
                    )
                }
                startActivity(Intent.createChooser(email, "Share via email"))
            }

            binding.btnEdit.setOnClickListener {
                val i = Intent(this@DetailsActivity, AddEditRestaurantActivity::class.java)
                i.putExtra("id", r.id)
                startActivity(i)
            }

            binding.btnDelete.setOnClickListener {
                lifecycleScope.launch {
                    dao.delete(r)
                    Toast.makeText(
                        this@DetailsActivity,
                        "Restaurant deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }
}
