package com.example.sondev

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    private lateinit var carAdapter: CarAdapter
    private var favoriListesi = ArrayList<Araba>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Favorilerim"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        findViewById<android.view.View>(R.id.fabAddCar).visibility = android.view.View.GONE
        findViewById<android.view.View>(R.id.spinnerMarka).visibility = android.view.View.GONE

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCars)
        recyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(favoriListesi)
        recyclerView.adapter = carAdapter

        verileriCekVeKontrolEt()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun verileriCekVeKontrolEt() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            db.collection("Users").document(user.uid).collection("Favorites")
                .get()
                .addOnSuccessListener { documents ->
                    favoriListesi.clear()

                    if (documents.isEmpty) {
                        Toast.makeText(this, "Henüz favori eklemediniz.", Toast.LENGTH_SHORT).show()
                        carAdapter.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }

                    for (document in documents) {
                        val favAraba = document.toObject(Araba::class.java)
                        val aracId = favAraba.id

                        db.collection("Cars").document(aracId).get()
                            .addOnSuccessListener { carDoc ->
                                if (carDoc.exists()) {
                                    val guncelAraba = carDoc.toObject(Araba::class.java)
                                    if (guncelAraba != null) {
                                        favoriListesi.add(guncelAraba)
                                        carAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    db.collection("Users").document(user.uid)
                                        .collection("Favorites").document(aracId)
                                        .delete()
                                }
                            }
                    }
                }
        }
    }
}