package com.example.farmer.ua.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class CartProduct(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val weight: String = "",
    val imageUrl: String = ""
)


@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var cartProducts by remember { mutableStateOf<List<CartProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        try {
            val fetchedCartProducts = fetchCartProductsFromFirestore(userId)
            cartProducts = fetchedCartProducts
        } catch (e: Exception) {
            errorMessage = "Failed to load cart products: ${e.message}"
            Log.e("Firestore", "Error fetching cart: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                actions = {
                    TextButton(
                        onClick = { performBuyAction(cartProducts, onNavigateToPayment) },
                        enabled = cartProducts.isNotEmpty()
                    ) {
                        Text(
                            text = "BUY",
                            color = if (cartProducts.isNotEmpty()) MaterialTheme.colorScheme.onPrimary else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "An unknown error occurred",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    cartProducts.isEmpty() -> {
                        Text(
                            text = "Your cart is empty",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(cartProducts) { product ->
                                CartProductCard(
                                    product = product,
                                    userId = userId,
                                    onRemove = { removedProduct ->
                                        scope.launch {
                                            removeFromCart(userId, removedProduct)
                                            cartProducts = cartProducts.filter { it.id != removedProduct.id }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

fun performBuyAction(cartProducts: List<CartProduct>, navigateToPayment: () -> Unit) {
    if (cartProducts.isNotEmpty()) {
        Log.d("Cart", "Redirecting to payment for items: $cartProducts")
        navigateToPayment()
    } else {
        Log.d("Cart", "No items in the cart to purchase.")
    }
}


suspend fun fetchCartProductsFromFirestore(userId: String): List<CartProduct> {
    val firestore = FirebaseFirestore.getInstance()
    val cartRef = firestore.collection("users").document(userId).collection("cart")
    val cartProducts = mutableListOf<CartProduct>()

    try {
        val snapshot = cartRef.get().await()
        snapshot.documents.mapNotNull { doc ->
            doc.toObject(CartProduct::class.java)?.copy(id = doc.id)
        }.let {
            cartProducts.addAll(it)
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Error fetching cart products: ${e.message}")
    }

    return cartProducts
}


suspend fun removeFromCart(userId: String, product: CartProduct) {
    val cartRef = FirebaseFirestore.getInstance()
        .collection("users").document(userId).collection("cart")

    try {
        cartRef.document(product.id).delete().await()
        Log.d("Firestore", "Product removed from cart: ${product.name}")
    } catch (e: Exception) {
        Log.e("Firestore", "Error removing product from cart: ${e.message}")
    }
}

@Composable
fun CartProductCard(
    product: CartProduct,
    userId: String,
    onRemove: (CartProduct) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imagePainter: Painter = rememberAsyncImagePainter(model = product.imageUrl)

            Image(
                painter = imagePainter,
                contentDescription = product.name,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Price: ₹${product.price}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Weight: ${product.weight} kg",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { onRemove(product) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove from Cart"
                )
            }
        }
    }
}
