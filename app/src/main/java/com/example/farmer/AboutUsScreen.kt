package com.example.farmer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmer.R

@Composable
fun AboutUsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "About Us") },
                backgroundColor = Color(0xFF0288D1), // Primary color for the app
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Section
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.surface),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "FarmConnect",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0288D1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // About Description
            Text(
                text = "FarmConnect bridges the gap between farmers and consumers by enabling direct connections. Our mission is to empower farmers by reducing dependence on intermediaries, ensuring fair prices, and providing fresh produce to consumers.",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Team Section
            Text(
                text = "Our Team",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0288D1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We are a passionate group of developers, designers, and agricultural experts dedicated to building solutions that transform the farming and retail landscape.",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))


            Text(
                text = "Contact Us",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0288D1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Email: moradhital@gmail.com\nPhone: +91 97252 63985",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
