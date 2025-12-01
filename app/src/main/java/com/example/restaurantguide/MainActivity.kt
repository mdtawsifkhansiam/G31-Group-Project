package com.example.restaurantguide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantguide.data.AppDatabase
import com.example.restaurantguide.data.RestaurantDao
import com.example.restaurantguide.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), RestaurantListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RestaurantListAdapter
    private lateinit var dao: RestaurantDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Room DB
        val db = AppDatabase.getDatabase(this)
        dao = db.restaurantDao()

        // RecyclerView
        adapter = RestaurantListAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Initial load
        loadRestaurants("")

        // FAB -> Add screen
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditRestaurantActivity::class.java))
        }

        // Search
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadRestaurants(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadRestaurants(newText ?: "")
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Reload current query whenever we come back
        val currentQuery = binding.searchView.query?.toString() ?: ""
        loadRestaurants(currentQuery)
    }

    private fun loadRestaurants(query: String) {
        Thread {
            val list = if (query.isBlank()) {
                dao.getAll()
            } else {
                dao.search(query)
            }

            runOnUiThread {
                adapter.submitList(list)
            }
        }.start()
    }

    override fun onItemClick(restaurantId: Long) {
        val i = Intent(this, DetailsActivity::class.java)
        i.putExtra("id", restaurantId)
        startActivity(i)
    }
}