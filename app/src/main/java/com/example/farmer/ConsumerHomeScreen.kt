
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class pd(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val weight: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerHomeScreen(
    //onNavigateToMarketplace: () -> Unit,
    onNavigateToCart: () -> Unit,
   // onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAboutUs: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var productList by remember { mutableStateOf<List<pd>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf<List<pd>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch products from Firestore
    LaunchedEffect(Unit) {
        try {
            val fetchedProducts = fetchProductsFromFirestore()
            productList = fetchedProducts
            filteredProducts = fetchedProducts
        } catch (e: Exception) {
            errorMessage = "Failed to load products: ${e.message}"
            Log.e("Firestore", "Error fetching products: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(

                onCartClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToCart()
                },
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToProfile()
                },
                onAboutUsClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToAboutUs()
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Consumer Dashboard") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu Icon"
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { query ->
                                searchQuery = query
                                filteredProducts = if (query.isBlank()) {
                                    productList
                                } else {
                                    productList.filter { product ->
                                        product.name.contains(query, ignoreCase = true)
                                    }
                                }
                            },
                            label = { Text("Search Products") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            singleLine = true
                        )

                        Box(modifier = Modifier.fillMaxSize()) {
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
                                filteredProducts.isEmpty() -> {
                                    Text(
                                        text = "No products available",
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                else -> {
                                    ProductList(products = filteredProducts, userId = userId)
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}

suspend fun fetchProductsFromFirestore(): List<pd> {
    val firestore = FirebaseFirestore.getInstance()
    val usersCollection = firestore.collection("users")
    val productList = mutableListOf<pd>()

    try {
        // Fetch all users
        val usersSnapshot = usersCollection.get().await()
        for (userDoc in usersSnapshot.documents) {
            val productsSnapshot = userDoc.reference.collection("products").get().await()
            productsSnapshot.documents.mapNotNull { productDoc ->
                productDoc.toObject(pd::class.java)?.copy(id = productDoc.id)
            }.let {
                productList.addAll(it)
            }
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Error fetching products: ${e.message}")
    }
    return productList
}

suspend fun addToCart(userId: String, product: pd) {
    val firestore = FirebaseFirestore.getInstance()

    try {
        // Get the user's cart subcollection reference
        val cartRef = firestore.collection("users").document(userId).collection("cart")

        // Check if the product already exists in the cart
        val existingProductSnapshot = cartRef.whereEqualTo("name", product.name).get().await()
        if (existingProductSnapshot.isEmpty) {
            // Add the product to the cart if it's not already there
            cartRef.add(product).await()
            Log.d("Firestore", "com.example.farmer.ua.screens.pd added to cart")
        } else {
            Log.d("Firestore", "com.example.farmer.ua.screens.pd already in cart")
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Error adding product to cart: ${e.message}")
    }
}

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                drawerContent()
            }
        },
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                content()
            }
        }
    )
}

@Composable
fun DrawerContent(
   // onMarketplaceClick: () -> Unit,
    onCartClick: () -> Unit,
    //onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAboutUsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Consumer Dashboard",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
       // DrawerMenuItem(label = "Marketplace", onClick = onMarketplaceClick)
        DrawerMenuItem(label = "Cart", onClick = onCartClick)
       // DrawerMenuItem(label = "Orders", onClick = onOrdersClick)
        DrawerMenuItem(label = "Profile", onClick = onProfileClick)
        DrawerMenuItem(label = "About Us", onClick = onAboutUsClick)
    }
}

@Composable
fun DrawerMenuItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

suspend fun toggleCartState(userId: String, product: pd, isInCart: Boolean) {
    val firestore = FirebaseFirestore.getInstance()
    val cartRef = firestore.collection("users").document(userId).collection("cart")

    try {
        if (isInCart) {
            // Remove product from cart
            val existingProductSnapshot = cartRef.whereEqualTo("name", product.name).get().await()
            for (doc in existingProductSnapshot.documents) {
                doc.reference.delete().await()
            }
            Log.d("Firestore", "com.example.farmer.ua.screens.pd removed from cart")
        } else {
            // Add product to cart
            cartRef.add(product).await()
            Log.d("Firestore", "com.example.farmer.ua.screens.pd added to cart")
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Error toggling cart state: ${e.message}")
    }
}

@Composable
fun ProductList(products: List<pd>, userId: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product, userId = userId)
        }
    }
}

@Composable
fun ProductCard(product: pd, userId: String) {
    val scope = rememberCoroutineScope()
    var isInCart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Check if product is in cart
        val cartRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId).collection("cart")
        val cartSnapshot = cartRef.whereEqualTo("name", product.name).get().await()
        isInCart = !cartSnapshot.isEmpty
    }

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

            // Toggle Cart State Button
            IconButton(
                onClick = {
                    if (userId.isNotEmpty()) {
                        scope.launch {
                            toggleCartState(userId, product, isInCart)
                            isInCart = !isInCart
                        }
                    } else {
                        Log.e("Cart", "User not authenticated")
                    }
                }
            ) {
                Icon(
                    imageVector = if (isInCart) Icons.Default.Check else Icons.Default.ShoppingCart,
                    contentDescription = if (isInCart) "Remove from Cart" else "Add to Cart"
                )
            }
        }
    }
}