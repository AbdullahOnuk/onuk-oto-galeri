package com.example.sondev

import android.content.Intent
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarAdapter(private val arabaListesi: ArrayList<Araba>) :
    RecyclerView.Adapter<CarAdapter.ArabaViewHolder>() {
    private val ADMIN_EMAIL = "admin@example.com" 

    class ArabaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMarkaModel: TextView = itemView.findViewById(R.id.itemMarkaModel)
        val txtFiyat: TextView = itemView.findViewById(R.id.itemFiyat)
        val imgAraba: ImageView = itemView.findViewById(R.id.itemImage)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCar)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavoriteCar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArabaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return ArabaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArabaViewHolder, position: Int) {
        val araba = arabaListesi[position]
        holder.txtMarkaModel.text = "${araba.marka} ${araba.model}"
        holder.txtFiyat.text = "${araba.fiyat} TL"

        // Resim Yükleme
        try {
            if (araba.gorselUrl.length > 200) {
                val imageBytes = Base64.decode(araba.gorselUrl, Base64.DEFAULT)
                Glide.with(holder.itemView.context).asBitmap().load(imageBytes).into(holder.imgAraba)
            } else {
                Glide.with(holder.itemView.context).load(araba.gorselUrl).into(holder.imgAraba)
            }
        } catch (e: Exception) {
            holder.imgAraba.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // --- YETKİLER ---
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email
        val db = FirebaseFirestore.getInstance()

        if (userEmail == ADMIN_EMAIL) {
            // ADMIN
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnFavorite.visibility = View.GONE

            holder.btnDelete.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("İlanı Sil")
                    .setMessage("Bu ilanı silmek istediğine emin misin?")
                    .setPositiveButton("Evet") { _, _ ->
                        db.collection("Cars").document(araba.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "İlan Silindi", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Hayır", null).show()
            }

        } else {
            // NORMAL KULLANICI
            holder.btnDelete.visibility = View.GONE
            holder.btnFavorite.visibility = View.VISIBLE

            holder.btnFavorite.setImageResource(R.drawable.ic_star_bos)
            holder.btnFavorite.tag = "bos"

            if (currentUser != null) {
                val favRef = db.collection("Users").document(currentUser.uid)
                    .collection("Favorites").document(araba.id)

                favRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        holder.btnFavorite.setImageResource(R.drawable.ic_star_dolu)
                        holder.btnFavorite.tag = "dolu"
                    } else {
                        holder.btnFavorite.setImageResource(R.drawable.ic_star_bos)
                        holder.btnFavorite.tag = "bos"
                    }
                }

                holder.btnFavorite.setOnClickListener {
                    val currentTag = holder.btnFavorite.tag.toString()

                    if (currentTag == "dolu") {
                        favRef.delete().addOnSuccessListener {
                            holder.btnFavorite.setImageResource(R.drawable.ic_star_bos)
                            holder.btnFavorite.tag = "bos"
                            Toast.makeText(holder.itemView.context, "Favorilerden Çıkarıldı", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val fullDataMap = hashMapOf(
                            "id" to araba.id,
                            "marka" to araba.marka,
                            "model" to araba.model,
                            "fiyat" to araba.fiyat,
                            "yil" to araba.yil,
                            "aciklama" to araba.aciklama,
                            "gorselUrl" to araba.gorselUrl
                        )

                        favRef.set(fullDataMap).addOnSuccessListener {
                            holder.btnFavorite.setImageResource(R.drawable.ic_star_dolu)
                            holder.btnFavorite.tag = "dolu"
                            Toast.makeText(holder.itemView.context, "Favorilere Eklendi!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("marka", araba.marka)
            intent.putExtra("model", araba.model)
            intent.putExtra("fiyat", araba.fiyat)
            intent.putExtra("yil", araba.yil)
            intent.putExtra("aciklama", araba.aciklama)
            intent.putExtra("gorselUrl", araba.gorselUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arabaListesi.size
    }
}