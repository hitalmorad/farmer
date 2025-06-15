package com.example.farmer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.example.farmer.nevigation.AppNavigation
import com.example.farmer.ui.theme.FarmerTheme

object CloudinaryConfig {
    val cloudinary: Cloudinary by lazy {
        Cloudinary(
            mapOf(
                "cloud_name" to "duhc5fja9",
                "api_key" to "597938773129959",
                "api_secret" to "DotZ3z0sHqt3UxDARBwRB9DXBtg"
            )
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        try {
            val config = mapOf(
                "cloud_name" to "duhc5fja9",
                "api_key" to "597938773129959",
                "api_secret" to "DotZ3z0sHqt3UxDARBwRB9DXBtg"
            )
            MediaManager.init(this, config)
            Log.d("Cloudinary", "Cloudinary initialized successfully.")
        } catch (e: Exception) {
            Log.e("Cloudinary", "Cloudinary initialization failed: ${e.message}")
        }


        setContent {
            FarmerTheme {
                AppNavigation()
            }
        }
    }
}
