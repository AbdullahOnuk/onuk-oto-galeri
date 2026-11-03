package com.example.sondev // <-- Kendi paket ismini kontrol et

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddCarActivity : AppCompatActivity() {

    private lateinit var imgSelected: ImageView
    private var selectedBitmap: Bitmap? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)

        imgSelected = findViewById(R.id.imgSelected)
        val btnResimSec = findViewById<Button>(R.id.btnResimSec)
        val btnUpload = findViewById<Button>(R.id.btnUploadCar)

        // GALERİDEN RESİM SEÇME
        val galeriBaslatici = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imgSelected.setImageURI(uri)
                try {
                    selectedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                    } else {
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        btnResimSec.setOnClickListener {
            galeriBaslatici.launch("image/*")
        }

        // İLANA KOY BUTONU
        btnUpload.setOnClickListener {
            val marka = findViewById<EditText>(R.id.etMarka).text.toString()
            val model = findViewById<EditText>(R.id.etModel).text.toString()
            val fiyat = findViewById<EditText>(R.id.etFiyat).text.toString().toDoubleOrNull() ?: 0.0
            val yil = findViewById<EditText>(R.id.etYil).text.toString().toIntOrNull() ?: 2023
            val aciklama = findViewById<EditText>(R.id.etAciklama).text.toString()

            if (selectedBitmap != null && marka.isNotEmpty() && fiyat > 0) {

                Toast.makeText(this, "Yükleniyor...", Toast.LENGTH_SHORT).show()
                btnUpload.isEnabled = false

                val resimString = bitmapiYaziyaCevir(selectedBitmap!!)
                val uuid = UUID.randomUUID().toString()

                val arabaMap = hashMapOf<String, Any>(
                    "id" to uuid,
                    "marka" to marka,
                    "model" to model,
                    "yil" to yil,
                    "fiyat" to fiyat,
                    "aciklama" to aciklama,
                    "gorselUrl" to resimString,
                    "tarih" to Timestamp.now()
                )

                db.collection("Cars").document(uuid).set(arabaMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Araç Başarıyla Eklendi!", Toast.LENGTH_LONG).show()
                        // İŞTE BURASI SENİ LİSTEYE GÖTÜRÜR:
                        finish()
                    }
                    .addOnFailureListener {
                        btnUpload.isEnabled = true
                        Toast.makeText(this, "Hata: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Lütfen resim seçin ve bilgileri doldurun.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bitmapiYaziyaCevir(bitmap: Bitmap): String {
        val maxBoyut = 800
        var width = bitmap.width
        var height = bitmap.height
        val oran: Float = width.toFloat() / height.toFloat()
        if (width > height) {
            width = maxBoyut
            height = (width / oran).toInt()
        } else {
            height = maxBoyut
            width = (height * oran).toInt()
        }
        val kucukBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        val stream = ByteArrayOutputStream()
        kucukBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        val byteDizisi = stream.toByteArray()
        return Base64.encodeToString(byteDizisi, Base64.DEFAULT)
    }
}