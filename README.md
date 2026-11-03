<p align="center">
  <img src="app/src/main/res/drawable/otologosu.jpg" alt="Onuk Oto Galeri logosu" width="640">
</p>

# Onuk Oto Galeri

Bu uygulamayı, okul döneminde **Mobil Uygulama Geliştirme** dersi kapsamında Android Studio'da Kotlin ve Firebase kullanarak geliştirdim.

Onuk Oto Galeri'de araç ilanlarını inceleyebilir, marka ve modele göre arama yapabilir, beğendiğiniz araçları favorilerinize ekleyebilirsiniz. Yönetici hesabıyla fotoğraflı ilan eklenebilir ve mevcut ilanlar kaldırılabilir. Uygulamanın tasarımında siyah ve altın tonlarını tercih ettim.

## Özellikler

| Özellik | Açıklama |
| --- | --- |
| Üyelik | E-posta ve şifreyle kayıt olma, giriş yapma ve şifre sıfırlama |
| Araç listesi | Yeni eklenen ilanların listeye otomatik yansıması |
| Arama ve filtre | Marka/model araması ve marka seçimi |
| Fiyat sıralaması | En yeni, artan fiyat ve azalan fiyat seçenekleri |
| İlan detayları | Görsel, marka, model, yıl, fiyat ve açıklama |
| Favoriler | Kullanıcıya özel favori araç listesi |
| İlan yönetimi | Yönetici hesabıyla fotoğraflı ilan ekleme ve silme |
| Paylaşım | İlan bilgilerini diğer uygulamalar üzerinden paylaşma |
| Profil | Hesap bilgilerini ve favori sayısını görüntüleme |

## Teknolojiler

- **Kotlin**, Android XML arayüzleri, Material Components ve RecyclerView
- **Firebase Authentication** ve **Cloud Firestore**
- Gradle Kotlin DSL ve Gradle Wrapper
- Minimum Android **7.0 / API 24**, hedef ve derleme SDK **36**

## Kurulum

1. Depoyu klonlayıp Android Studio ile açın:

   ```sh
   git clone https://github.com/AbdullahOnuk/onuk-oto-galeri.git
   ```

2. Android SDK 36'yı kurun ve Gradle JDK olarak **JDK 17 veya üzerini** seçin.
3. Kendi Firebase projenize `com.example.sondev` paket adıyla bir Android uygulaması ekleyin.
4. Firebase'den indirdiğiniz `google-services.json` dosyasını `app/` klasörüne koyun. Bu dosya depoya dahil edilmemiştir.
5. Firebase Authentication içinde **E-posta/Şifre** sağlayıcısını etkinleştirin ve Cloud Firestore veritabanını oluşturun.
6. Yönetici hesabı için `MainActivity`, `CarAdapter` ve `ProfileActivity` dosyalarındaki `ADMIN_EMAIL` değerini kendi hesabınıza göre düzenleyin. Firestore kurallarında ilan yönetimini yöneticiyle, favori erişimini ilgili kullanıcıyla sınırlandırın.
7. Gradle senkronizasyonunu tamamlayıp uygulamayı emülatör veya Android cihazda çalıştırın.

Komut satırından debug derlemesi:

```powershell
.\gradlew.bat assembleDebug
```

## Firebase yapısı

```text
Cars/{aracId}
  id, marka, model, yil, fiyat, aciklama, gorselUrl, tarih

Users/{kullaniciUid}/Favorites/{aracId}
```

Araç bilgileri `Cars`, kullanıcının favorileri ise `Users/{kullaniciUid}/Favorites` koleksiyonunda tutulur. Fotoğraflar küçültülüp Base64 biçiminde araç kaydına eklenir.
