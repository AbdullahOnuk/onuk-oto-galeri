package com.example.sondev // <-- Kendi paket ismin kalsın

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Uygulama her açıldığında önceki oturumu kapat (Garanti olsun)
        FirebaseAuth.getInstance().signOut()

        // 3 Saniye bekle ve Login ekranına git
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Geri dönülemesin
        }, 3000)
    }
}