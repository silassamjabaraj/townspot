package com.example.data.remote.supabase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

enum class SupabaseStatus {
    NOT_CONFIGURED,
    CONNECTED,
    CONNECTING,
    ERROR
}

class SupabaseClient(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("townspot_supabase_prefs", Context.MODE_PRIVATE)

    var currentUrl: String
        private set

    var currentAnonKey: String
        private set

    var isAutoPushEnabled: Boolean
        get() = prefs.getBoolean("supabase_auto_push_enabled", true)
        set(value) {
            prefs.edit().putBoolean("supabase_auto_push_enabled", value).apply()
        }

    var status: SupabaseStatus = SupabaseStatus.NOT_CONFIGURED
        private set

    var lastErrorMessage: String? = null
        private set

    private var retrofit: Retrofit? = null
    private var api: SupabaseApi? = null

    init {
        // Read from saved preferences first, or fallback to BuildConfig values injected by secrets plugin
        val defaultUrl = try {
            BuildConfig.SUPABASE_URL
        } catch (_: Exception) {
            "https://placeholder.supabase.co"
        }
        val defaultKey = try {
            BuildConfig.SUPABASE_ANON_KEY
        } catch (_: Exception) {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.placeholder"
        }

        currentUrl = prefs.getString("supabase_url", null) ?: defaultUrl
        currentAnonKey = prefs.getString("supabase_anon_key", null) ?: defaultKey

        rebuildClient()
    }

    fun isConfigured(): Boolean {
        val url = currentUrl.trim()
        val key = currentAnonKey.trim()
        return url.startsWith("https://") &&
                !url.contains("placeholder") &&
                key.length > 20 &&
                !key.contains("placeholder")
    }

    fun getMaskedKey(): String {
        val key = currentAnonKey.trim()
        return if (key.length > 10) {
            "${key.take(6)}...${key.takeLast(4)}"
        } else {
            "••••••••"
        }
    }

    fun updateCredentials(url: String, anonKey: String) {
        val formattedUrl = if (url.trim().endsWith("/")) url.trim() else "${url.trim()}/"
        currentUrl = formattedUrl
        currentAnonKey = anonKey.trim()

        prefs.edit()
            .putString("supabase_url", currentUrl)
            .putString("supabase_anon_key", currentAnonKey)
            .apply()

        rebuildClient()
    }

    fun resetToDefaults() {
        prefs.edit()
            .remove("supabase_url")
            .remove("supabase_anon_key")
            .apply()

        val defaultUrl = try { BuildConfig.SUPABASE_URL } catch (_: Exception) { "https://placeholder.supabase.co" }
        val defaultKey = try { BuildConfig.SUPABASE_ANON_KEY } catch (_: Exception) { "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.placeholder" }
        currentUrl = defaultUrl
        currentAnonKey = defaultKey
        rebuildClient()
    }

    private fun rebuildClient() {
        try {
            var url = currentUrl.trim()
            if (!url.endsWith("/")) {
                url += "/"
            }

            val authInterceptor = Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("apikey", currentAnonKey)
                    .header("Authorization", "Bearer $currentAnonKey")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                chain.proceed(requestBuilder.build())
            }

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            api = retrofit?.create(SupabaseApi::class.java)
            status = if (isConfigured()) SupabaseStatus.CONNECTED else SupabaseStatus.NOT_CONFIGURED
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Failed to build Supabase client", e)
            status = SupabaseStatus.ERROR
            lastErrorMessage = e.message
        }
    }

    fun getApi(): SupabaseApi? = api

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        val service = api ?: return@withContext Result.failure(Exception("Supabase client is not initialized."))

        try {
            status = SupabaseStatus.CONNECTING
            val response = service.ping()
            if (response.isSuccessful || response.code() in 200..299 || response.code() == 404 || response.code() == 401) {
                // In PostgREST, root /rest/v1/ returns openapi doc or HTTP 200/404 depending on config
                status = SupabaseStatus.CONNECTED
                lastErrorMessage = null
                Result.success("Connection to Supabase successful (HTTP ${response.code()})! Base URL: $currentUrl")
            } else {
                status = SupabaseStatus.ERROR
                val err = "Supabase responded with HTTP ${response.code()}: ${response.message()}"
                lastErrorMessage = err
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            status = SupabaseStatus.ERROR
            val err = "Failed to connect to Supabase: ${e.localizedMessage ?: e.message}"
            lastErrorMessage = err
            Result.failure(Exception(err, e))
        }
    }
}
