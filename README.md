<p align="center">
  <img src="app/src/main/res/drawable/otologosu.jpg" alt="Onuk Oto Galeri logosu" width="640">
</p>

# Onuk Oto Galeri

**Araç ilanlarını keşfet, favorilerini oluştur, beğendiğin aracı paylaş.**

Kotlin ve Firebase ile Android Studio üzerinde geliştirilen oto galeri uygulaması. Siyah ve altın tonlarındaki arayüz; araç ilanlarını inceleme, marka ve modele göre arama, favori listesi oluşturma ve yönetici için ilan yönetimi sunar.

## Özellikler

| Özellik | Açıklama |
| --- | --- |
| Üyelik ve giriş | Firebase Authentication ile e-posta ve şifre kullanarak kayıt ve oturum açma |
| Araç vitrini | Cloud Firestore üzerinden güncel araç listesinin dinlenmesi |
| Arama ve filtre | Marka/model araması ve marka seçimi |
| Fiyat sıralaması | En yeni, artan fiyat ve azalan fiyat seçenekleri |
| İlan detayları | Görsel, marka, model, yıl, fiyat ve açıklama |
| Favoriler | Kullanıcıya özel favori araç listesi |
| İlan yönetimi | Yönetici arayüzünde görselli araç ekleme ve ilan silme |
| Paylaşım | Android paylaşım menüsü üzerinden ilan bilgilerinin paylaşılması |
| Profil | Hesap bilgileri, istatistikler ve şifre sıfırlama e-postası |

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

2. Android SDK 36'yı kurun. Android Studio'nun Gradle JDK ayarında Android Gradle Plugin 8.13.2 ile uyumlu **JDK 17 veya üzerini** kullanın. Uygulamanın Kotlin/JVM derleme hedefi 11'dir.
3. Kendi Firebase projenize `com.example.sondev` paket adıyla bir Android uygulaması ekleyin.
4. Firebase'den indirdiğiniz `google-services.json` dosyasını `app/` klasörüne koyun. Bu dosya depoya dahil edilmemiştir.
5. Firebase Authentication içinde **E-posta/Şifre** sağlayıcısını etkinleştirin ve Cloud Firestore veritabanını oluşturun.
6. Firestore erişim kurallarını kendi kullanıcı ve yönetici modelinize göre yapılandırın. Gradle senkronizasyonunu tamamlayıp uygulamayı emülatör veya Android cihazda çalıştırın.

Komut satırından debug derlemesi:

```powershell
.\gradlew.bat assembleDebug
```

## Veri yapısı

```text
Cars/{aracId}
  id, marka, model, yil, fiyat, aciklama, gorselUrl, tarih

Users/{kullaniciUid}/Favorites/{aracId}
```

Araç görselleri küçültülüp JPEG biçiminde Base64 metnine çevrilerek `gorselUrl` alanında saklanır. Firebase Storage bağımlılığı projede bulunur; mevcut araç ekleme akışı görselleri Firestore'a yazar.

## Proje yapısı

```text
app/src/main/
├── java/com/example/sondev/
│   ├── SplashActivity.kt
│   ├── LoginActivity.kt / RegisterActivity.kt
│   ├── MainActivity.kt / CarAdapter.kt / Araba.kt
│   ├── AddCarActivity.kt / DetailActivity.kt
│   ├── FavoritesActivity.kt / ProfileActivity.kt
│   └── AboutActivity.kt
├── res/                         # Ekranlar, logo, simge ve tema
└── AndroidManifest.xml
```

## Geliştirme notları

Bu proje eğitim ve portföy amacıyla geliştirilmiştir. Yönetici arayüzünün görünürlüğü kaynak kodundaki `ADMIN_EMAIL` değeriyle belirlenir; paylaşım sürümünde bu değer `admin@example.com` olarak birleştirilmiştir. Kendi kurulumunuzda ilgili değeri değiştirin. Bu istemci kontrolü tek başına yetkilendirme sağlamaz; ilan yazma/silme ve favori erişim izinleri Firestore kurallarıyla uygulanmalıdır. Sunucudaki mevcut kurallar bu depoda bulunmaz.

Paylaşım sürümündeki iletişim alanları örnek yer tutucularla değiştirilmiştir. Yerel Firebase ayarları, imzalama anahtarları, SDK yolu, APK'lar ve derleme önbellekleri depoya dahil edilmez. Uygulama adı ve simgesi giriş ekranındaki mevcut Onuk Oto Galeri görseliyle uyumludur.

Bu paylaşım sırasında kaynak ve yapılandırma kontrolü yapılmıştır; cihaz/emülatör üzerinde yeniden çalışma testi veya APK derlemesi yapılmamıştır.
