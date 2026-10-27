# ==============================================================================
# R8 / ProGuard Configuration for TownSpot
# ==============================================================================

# --- Line Numbers & Stack Traces ---
# Preserve line numbers and source file names for Google Play Console deobfuscation
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# --- Room Database ---
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>();
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class com.example.data.database.** { *; }
-keep class com.example.data.model.** { *; }

# --- Moshi & JSON Models ---
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonClass public <init>(...);
}
-keep class * extends com.squareup.moshi.JsonAdapter {
    public <init>(...);
}
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# --- Retrofit & OkHttp ---
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Kotlin Coroutines & Flow ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- Coil Image Loading ---
-keep class coil.** { *; }
-dontwarn coil.**

# --- Google Mobile Ads / AdSense ---
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}
-dontwarn com.google.android.gms.ads.**

# --- Supabase & App Models ---
-keep class com.example.data.remote.supabase.** { *; }
-keepclassmembers class com.example.data.model.** {
    <fields>;
    <methods>;
}

# --- General Optimization & Warnings ---
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
