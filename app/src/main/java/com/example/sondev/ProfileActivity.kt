package com.example.sondev

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val ADMIN_EMAIL = "admin@example.com" // KENDİ MAİLİNİ YAZ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val txtEmail = findViewById<TextView>(R.id.txtProfileEmail)
        val txtStatTitle = findViewById<TextView>(R.id.txtStatTitle)
        val txtStatCount = findViewById<TextView>(R.id.txtStatCount)
        val btnResetPass = findViewById<Button>(R.id.btnChangePassword)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            txtEmail.text = currentUser.email

            // ADMIN Mİ YOKSA KULLANICI MI?
            if (currentUser.email == ADMIN_EMAIL) {
                // Adminse: Toplam araç sayısını göster
                txtStatTitle.text = "Galerideki Toplam Araç"
                db.collection("Cars").get().addOnSuccessListener {
                    txtStatCount.text = it.size().toString()
                }
            } else {
                // Kullanıcıysa: Favori sayısını göster
                txtStatTitle.text = "Favorilerimdeki Araçlar"
                db.collection("Users").document(currentUser.uid)
                    .collection("Favorites").get()
                    .addOnSuccessListener {
                        txtStatCount.text = it.size().toString()
                    }
            }

            // Şifre Değiştirme Butonu
            btnResetPass.setOnClickListener {
                auth.sendPasswordResetEmail(currentUser.email!!)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Şifre sıfırlama maili gönderildi!", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Hata: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}