package com.example.farmer.ua.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.farmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class Product(
    val name: String,
    val price: String,
    val weight: String,
    val imageUrl: String
)


@Composable
fun VideoBackground(videoUri: String, modifier: Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            prepare()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false // Disable video controls
            }
        },
        modifier = modifier
    )
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(25.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display product image
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )

            // Display product details
            Text(
                text = product.name,
                fontSize = 35.sp,
                color = Color(0xFF32CD32),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Price: ${product.price}",
                fontSize = 25.sp,
                color = Color(0xFF32CD32),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Weight: ${product.weight}",
                fontSize = 25.sp,
                color = Color(0xFF32CD32)
            )
        }
    }
}

@Composable
fun MyProductsScreen( onNavigateBack: () -> Unit ) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val userId = auth.currentUser?.uid
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Fetch products uploaded by the logged-in user
    LaunchedEffect(userId) {
        userId?.let {
            firestore.collection("users")
                .document(it)
                .collection("products")
                .get()
                .addOnSuccessListener { snapshot ->
                    products = snapshot.documents.map { document ->
                        Product(
                            name = document.getString("name") ?: "",
                            price = document.getString("price") ?: "",
                            weight = document.getString("weight") ?: "",
                            imageUrl = document.getString("imageUrl") ?: ""
                        )
                    }
                }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Video Background
        VideoBackground(
            videoUri = "android.resource://com.example.farmer/${R.raw.mypv}",
            modifier = Modifier.fillMaxSize()
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "My Products",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(products) { product ->
                    ProductCard(product = product)
                }
            }
        }

    }
}

