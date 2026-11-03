package com.example.sondev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val emailText = findViewById<EditText>(R.id.kullanıcıAdı)
        val passText = findViewById<EditText>(R.id.sifre)
        val btnLogin = findViewById<Button>(R.id.button)

        val btnKayitSayfasinaGit = findViewById<Button>(R.id.btnGitKayitOl)

        // 1. GİRİŞ BUTONU
        btnLogin.setOnClickListener {
            val email = emailText.text.toString()
            val password = passText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Hoşgeldiniz", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Giriş Başarısız: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Lütfen bilgileri giriniz", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. KAYIT OL BUTONU
        btnKayitSayfasinaGit.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}