# Motion Sensor: Ball

Aplikasi Android (Kotlin + Jetpack Compose) yang memakai **Accelerometer** untuk menggerakkan sebuah bola di dalam arena. Miringkan HP dan bola menggelinding mengikuti kemiringan, lengkap dengan gesekan dan pantulan di dinding arena.

## Memenuhi syarat tugas

| Syarat | Implementasi |
|---|---|
| Sensor dari kategori **Motion** | Accelerometer (`Sensor.TYPE_ACCELEROMETER`) |
| Sensor memberi efek langsung ke UI | Posisi bola berubah mengikuti kemiringan HP |
| Lifecycle sensor | `registerListener` di `onResume()`, `unregisterListener` di `onPause()` |

---

## Apa itu Motion Sensor?

Motion sensor mengukur **gerakan perangkat**, yaitu percepatan dan rotasi.

| Sensor | Fungsi |
|---|---|
| **Accelerometer** | Percepatan pada sumbu X, Y, Z (termasuk gravitasi), satuan m/s² |
| Gyroscope | Kecepatan rotasi pada tiga sumbu, satuan rad/s |
| Gravity | Arah dan besar gravitasi |
| Linear Acceleration | Percepatan tanpa gravitasi |
| Rotation Vector | Orientasi perangkat dalam ruang |

Contoh penggunaan: mendeteksi goyangan, kemiringan layar, atau game yang dikendalikan dengan memiringkan HP.

**Yang dipakai di aplikasi ini: Accelerometer.**

---

## Cara kerja Accelerometer

Accelerometer mengembalikan tiga nilai: `values[0]` (X), `values[1]` (Y), `values[2]` (Z).

| Sumbu | Arah |
|---|---|
| X | Ke kanan layar |
| Y | Ke atas layar |
| Z | Keluar dari layar |

Saat HP diam, sensor tetap membaca gravitasi sekitar **9,8 m/s²** pada sumbu yang menghadap ke atas. Karena itu, saat HP dimiringkan, nilai X dan Y berubah sesuai arah kemiringan. Nilai inilah yang dipakai untuk menggerakkan bola.

---

## Logika pergerakan bola

Setiap kali sensor memberi data baru, kecepatan bola diperbarui, lalu posisinya digeser.

```kotlin
// Sumbu X perangkat positif ke kanan, jadi tanda dibalik supaya bola
// bergerak ke sisi yang dimiringkan. Sumbu Y positif ke atas.
velX += -x * 0.6f
velY += y * 0.6f

// Gesekan supaya bola melambat
velX *= 0.96f
velY *= 0.96f

var nx = ballX + velX
var ny = ballY + velY

// Pantulan di dinding arena
if (nx < 0f)   { nx = 0f;   velX = -velX * 0.4f }
if (nx > maxX) { nx = maxX; velX = -velX * 0.4f }
if (ny < 0f)   { ny = 0f;   velY = -velY * 0.4f }
if (ny > maxY) { ny = maxY; velY = -velY * 0.4f }
```

| Parameter | Fungsi | Efek jika diubah |
|---|---|---|
| `0.6f` | Kepekaan terhadap kemiringan | Lebih besar, bola lebih lincah |
| `0.96f` | Gesekan | Lebih kecil, bola cepat berhenti |
| `0.4f` | Daya pantul di dinding | Lebih besar, bola memantul lebih kuat |

Saat HP berdiri tegak, bola jatuh ke bagian bawah arena seperti terkena gravitasi. Saat HP diletakkan datar, bola relatif diam.

---

## Lifecycle sensor

Sensor didaftarkan saat aplikasi tampil dan dilepas saat aplikasi tidak aktif, supaya tidak menguras baterai.

```kotlin
override fun onResume() {
    super.onResume()
    accelSensor?.also {
        sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
    }
}

override fun onPause() {
    super.onPause()
    sensorManager.unregisterListener(this)
    // Reset kecepatan supaya bola tidak meloncat saat aplikasi dibuka lagi
    velX = 0f
    velY = 0f
}
```

Laju pembacaan `SENSOR_DELAY_GAME` dipilih karena cukup cepat untuk gerakan yang halus.

Aplikasi juga memeriksa ketersediaan sensor di `onCreate()`. Jika Accelerometer tidak ada di perangkat, pesan ditampilkan di layar dan aplikasi tidak crash.

---

## Tampilan UI

- **Arena:** area persegi panjang berlatar gelap transparan dengan sudut membulat.
- **Bola:** lingkaran merah yang posisinya diatur lewat `Modifier.offset`.
- **Teks info:** nilai Accel X dan Y ditampilkan secara realtime.

Posisi bola disimpan sebagai state (`mutableFloatStateOf`), sehingga UI Compose otomatis menggambar ulang setiap nilai berubah.

---

## Cara menjalankan

1. Buka project di Android Studio, lalu **Sync Project with Gradle Files**.
2. Hubungkan HP asli (USB debugging atau Wireless debugging) atau gunakan emulator.
3. Klik tombol **Run**.

### Cara menguji

| Pengujian | Cara |
|---|---|
| Pergerakan bola | Miringkan HP ke kiri, kanan, depan, dan belakang, bola harus mengikuti |
| Pantulan | Miringkan HP cukup tajam sampai bola menabrak dinding arena |
| Lifecycle | Tekan tombol Home lalu buka lagi aplikasinya, bola tidak boleh meloncat |

Di emulator, atur kemiringan lewat **Extended Controls → Virtual sensors → Device Pose**.

> **Tips:** tambahkan `android:screenOrientation="portrait"` pada `<activity>` di `AndroidManifest.xml` supaya arah sumbu tidak berubah saat HP diputar.

## Struktur kode

| Bagian | Fungsi |
|---|---|
| `MainActivity` | Mengelola sensor, lifecycle, dan fisika bola |
| Composable layar | Menampilkan arena, bola, dan teks nilai sensor |

Video Demo Singkat :
https://drive.google.com/drive/folders/1rMHsGhw_pCWniSnY7b4loQOOSzyu4jm4?usp=sharing
