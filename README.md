# Glass Manager (ZArchiver + MT Manager style, liquid glass)

## Build APK unsigned
**Android Studio:** Open folder ini -> Build > Generate App Bundles or APKs, atau terminal `./gradlew assembleRelease`
(Android Studio membuat Gradle wrapper otomatis).
Hasil: `app/build/outputs/apk/release/app-release-unsigned.apk`

**GitHub Actions:** upload folder ini ke repo -> tab Actions -> "Build unsigned APK" -> unduh artifact.

## Fitur sekarang
- Jelajah berkas, buka .zip/.apk dari daftar
- ZIP: lihat isi, ekstrak (sandi AES), hapus entri, ZIP satu folder
- APK: info paket, buat keystore, tanda tangan V1/V2/V3/V4 (apksig)
- Tema liquid glass (blob blur + panel kaca)

## Belum ada (roadmap)
- Edit DEX (baksmali/smali), Manifest/resources.arsc (ARSCLib), zipalign, salin/pindah/hapus berkas, multi-select
- Blur latar sungguhan di belakang panel (mis. library Haze)
