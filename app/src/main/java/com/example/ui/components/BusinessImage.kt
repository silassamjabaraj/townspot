package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R

object BusinessImageResolver {
    fun getDrawableResId(name: String?): Int? {
        return when (name) {
            "biz_cafe" -> R.drawable.biz_cafe
            "biz_bistro" -> R.drawable.biz_bistro
            "biz_woodcraft" -> R.drawable.biz_woodcraft
            "biz_wellness" -> R.drawable.biz_wellness
            "biz_auto" -> R.drawable.biz_auto
            "biz_bakery" -> R.drawable.biz_bakery
            "biz_outdoors" -> R.drawable.biz_outdoors
            "biz_botanical" -> R.drawable.biz_botanical
            "biz_books" -> R.drawable.biz_books
            "biz_veterinary" -> R.drawable.biz_veterinary
            "biz_pizza" -> R.drawable.biz_pizza
            "biz_ceramics" -> R.drawable.biz_ceramics
            "biz_gymnasium" -> R.drawable.biz_gymnasium
            "biz_barber" -> R.drawable.biz_barber
            else -> null
        }
    }
}

@Composable
fun BusinessProfileImage(
    imageResName: String?,
    imageUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    val staticResId = remember(imageResName) {
        BusinessImageResolver.getDrawableResId(imageResName)
            ?: imageResName?.let {
                val id = context.resources.getIdentifier(it, "drawable", context.packageName)
                if (id != 0) id else null
            }
    }

    Box(modifier = modifier) {
        when {
            // Picked URI or Network URL
            !imageUri.isNullOrBlank() -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Local generated business photo
            staticResId != null -> {
                Image(
                    painter = painterResource(id = staticResId),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Fallback: Elegant branded gradient with store icon
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storefront,
                        contentDescription = contentDescription,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
