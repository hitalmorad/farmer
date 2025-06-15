package com.example.farmer.ua.screens

import android.util.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.example.farmer.R
import com.example.farmer.data.model.WeatherData
import com.example.farmer.data.network.WeatherDataRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    weatherDataRepository: WeatherDataRepository,
    apiKey: String,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToMyProducts: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAboutUs: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backgroundPainter: Painter = painterResource(id = R.drawable.wether_bk)


    var weather by remember { mutableStateOf<WeatherData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var city by remember { mutableStateOf(TextFieldValue("")) }

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onAddProductClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToAddProduct()
                },
                onMyProductsClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToMyProducts()
                },
                onTransactionsClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToTransactions()
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
                        title = {
                            Text(
                                text = "Farmer Dashboard",
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu Icon"
                                )
                            }
                        }
                    )
                },
                content = { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Background image
                        Image(
                            painter = backgroundPainter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(-1f),
                            contentScale = ContentScale.FillBounds
                        )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Welcome to the Farmer Dashboard",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },

                            label = { Text("Enter City Name") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White)
                        )


                        Spacer(modifier = Modifier.height(16.dp))

                        // Get Temperature Button
                        Button(onClick = {
                            scope.launch {
                                try {
                                    isLoading = true
                                    errorMessage = null
                                    val response = weatherDataRepository.getWeatherData(city.text, apiKey)
                                    weather = response
                                } catch (e: Exception) {
                                    errorMessage = "Failed to load weather data"
                                    Log.e("Weather", "Error fetching weather: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }) {
                            Text("Get Temperature")
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                            // Weather display
                            if (isLoading) {
                                CircularProgressIndicator()
                            } else if (errorMessage != null) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                weather?.let {
                                    // Show weather data
                                    WeatherCard(
                                        title = "Weather in ${it.cityName}",
                                        description = it.weatherDescription,
                                        icon = painterResource(id = R.drawable.clouds)
                                    )
                                    WeatherCard(
                                        title = "Temperature",
                                        description = "${it.temperature}°C",
                                        icon = painterResource(id = R.drawable.temperature)
                                    )
                                    WeatherCard(
                                        title = "Humidity",
                                        description = "${it.humidity}%",
                                        icon = painterResource(id = R.drawable.humidity)
                                    )
                                    WeatherCard(
                                        title = "Pressure",
                                        description = "${it.pressure} hPa",
                                        icon = painterResource(id = R.drawable.atmospheric)
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

    )
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
                    .width(280.dp), // Set a fixed width for the drawer
                color = MaterialTheme.colorScheme.surface
            ) {
                drawerContent()
            }
        },
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    )
}

@Composable
fun DrawerContent(
    onAddProductClick: () -> Unit,
    onMyProductsClick: () -> Unit,
    onTransactionsClick: () -> Unit,
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
            text = "Farmer Dashboard",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.sp
            )
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        DrawerMenuItem(label = "Add Product", onClick = onAddProductClick)
        DrawerMenuItem(label = "My Products", onClick = onMyProductsClick)
        DrawerMenuItem(label = "Transactions", onClick = onTransactionsClick)
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

@Composable
fun WeatherCard(title: String, description: String, icon: Painter) {
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
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}