# ── Debugging info (keep line numbers for crash reports) ──────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Room ──────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}

# ── App model / DAO classes ───────────────────────────────────────────────────
-keep class com.example.v2.InventoryItem { *; }
-keep class com.example.v2.StockLog { *; }
-keep class com.example.v2.ValueSnapshot { *; }
-keep class com.example.v2.InventoryBackupManager$BackupData { *; }

# ── ZXing barcode scanner ─────────────────────────────────────────────────────
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.** { *; }

# ── MPAndroidChart ────────────────────────────────────────────────────────────
-keep class com.github.mikephil.charting.** { *; }

# ── WorkManager ───────────────────────────────────────────────────────────────
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ── AndroidX / Material ───────────────────────────────────────────────────────
-dontwarn androidx.**
-keep class androidx.core.app.** { *; }

# ── Suppress common warnings ──────────────────────────────────────────────────
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
