package com.example.sondev

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var carAdapter: CarAdapter

    private var tumArabaListesi = ArrayList<Araba>()
    private var gosterilenListe = ArrayList<Araba>()

    private val db = FirebaseFirestore.getInstance()

    private var secilenKategori = "Tümü"
    private var aramaMetni = ""
    private var siralamaTuru = 0 // 0: Varsayılan, 1: Fiyat Artan, 2: Fiyat Azalan

    private val ADMIN_EMAIL = "admin@example.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCars)
        recyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(gosterilenListe)
        recyclerView.adapter = carAdapter

        // Ekleme Butonu (Admin)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddCar)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser?.email == ADMIN_EMAIL) {
            fabAdd.visibility = View.VISIBLE
            fabAdd.setOnClickListener {
                startActivity(Intent(this, AddCarActivity::class.java))
            }
        } else {
            fabAdd.visibility = View.GONE
        }

        setupSpinner()
        canliVeriDinle()
    }

    private fun setupSpinner() {
        val spinnerMarka = findViewById<Spinner>(R.id.spinnerMarka)
        val markalar = arrayOf("Tümü", "BMW", "Mercedes", "Audi", "Fiat", "Honda", "Toyota", "Ford")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, markalar)
        spinnerMarka.adapter = adapter

        spinnerMarka.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilenKategori = markalar[position]
                listeyiGuncelle()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun canliVeriDinle() {
        db.collection("Cars")
            .orderBy("tarih", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                if (value != null) {
                    tumArabaListesi.clear()
                    for (document in value.documents) {
                        val araba = document.toObject(Araba::class.java)
                        if (araba != null) tumArabaListesi.add(araba)
                    }
                    listeyiGuncelle()
                }
            }
    }

    // arama filtre gibi işlemler ---
    private fun listeyiGuncelle() {
        gosterilenListe.clear()

        // Filtreleme ve Arama
        for (araba in tumArabaListesi) {
            val markaUyuyorMu = (secilenKategori == "Tümü" || araba.marka.equals(secilenKategori, ignoreCase = true))

            val aramaUyuyorMu = araba.marka.lowercase(Locale.getDefault()).contains(aramaMetni) ||
                    araba.model.lowercase(Locale.getDefault()).contains(aramaMetni)

            if (markaUyuyorMu && aramaUyuyorMu) {
                gosterilenListe.add(araba)
            }
        }

        // Sıralama
        when (siralamaTuru) { //0 olduğu gibi
            1 -> gosterilenListe.sortBy { it.fiyat }           // Fiyat Artan
            2 -> gosterilenListe.sortByDescending { it.fiyat } // Fiyat Azalan
        }

        carAdapter.notifyDataSetChanged()
    }

    // --- MENÜ OLUŞTURMA (ARAMA ve SIRALAMA BURADA) ---
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser?.email == ADMIN_EMAIL) {
            menu?.findItem(R.id.action_favorites)?.isVisible = false
        }

        // 2. ARAMA ÇUBUĞU
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }

            override fun onQueryTextChange(newText: String?): Boolean {
                aramaMetni = newText?.lowercase(Locale.getDefault()) ?: ""
                listeyiGuncelle() // Her harf girişinde listeyi yenile
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                val secenekler = arrayOf("Varsayılan (En Yeni)", "Fiyat (Önce En Ucuz)", "Fiyat (Önce En Pahalı)")
                AlertDialog.Builder(this)
                    .setTitle("Sıralama Seç")
                    .setItems(secenekler) { _, which ->
                        siralamaTuru = which
                        listeyiGuncelle()
                        Toast.makeText(this, "${secenekler[which]} seçildi", Toast.LENGTH_SHORT).show()
                    }
                    .show()
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}