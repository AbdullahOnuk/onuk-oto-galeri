package com.example.sondev

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val marka = intent.getStringExtra("marka") ?: ""
        val model = intent.getStringExtra("model") ?: ""
        val fiyat = intent.getDoubleExtra("fiyat", 0.0)
        val yil = intent.getIntExtra("yil", 2023)
        val aciklama = intent.getStringExtra("aciklama") ?: ""
        val gorselUrl = intent.getStringExtra("gorselUrl") ?: ""

        findViewById<TextView>(R.id.detailMarkaModel).text = "$marka $model"
        findViewById<TextView>(R.id.detailFiyat).text = "$fiyat TL"
        findViewById<TextView>(R.id.detailAciklama).text = aciklama
        findViewById<TextView>(R.id.detailYil).text = "Yıl: $yil"

        val imgView = findViewById<ImageView>(R.id.detailImage)
        try {
            if (gorselUrl.length > 200) {
                val bytes = Base64.decode(gorselUrl, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgView.setImageBitmap(bmp)
            }
        } catch (e: Exception) {}

        val fabShare = findViewById<FloatingActionButton>(R.id.fabShare)
        fabShare.setOnClickListener {
            val paylasimMetni = "Bu araca bir bak! \n\n$marka $model ($yil)\nFiyat: $fiyat TL\n\nDetay: $aciklama\n\n- Onuk Oto Galeri"

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "$marka $model İlanı")
            shareIntent.putExtra(Intent.EXTRA_TEXT, paylasimMetni)
            startActivity(Intent.createChooser(shareIntent, "İlanı Paylaş"))
        }
    }
}