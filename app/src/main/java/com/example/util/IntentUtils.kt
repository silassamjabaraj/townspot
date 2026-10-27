package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.BusinessEntity

object IntentUtils {

    fun dialBusiness(context: Context, phone: String) {
        try {
            val cleaned = phone.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Cannot open dialer for $phone", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDirections(context: Context, name: String, address: String) {
        try {
            val uri = Uri.parse("geo:0,0?q=" + Uri.encode("$name, $address"))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Cannot open map app", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWebsite(context: Context, url: String) {
        try {
            val formatted = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formatted))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Cannot open web browser", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Cannot open email client", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareBusiness(context: Context, business: BusinessEntity) {
        try {
            val shareText = """
                Check out ${business.name} in ${business.neighborhood}!
                ${business.tagline}
                📍 ${business.address}
                📞 ${business.phone}
                🌐 ${business.website}
                
                Discovered on TownSpot Local Directory.
            """.trimIndent()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, business.name)
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share ${business.name} via"))
        } catch (_: Exception) {
            Toast.makeText(context, "Cannot share listing", Toast.LENGTH_SHORT).show()
        }
    }
}
