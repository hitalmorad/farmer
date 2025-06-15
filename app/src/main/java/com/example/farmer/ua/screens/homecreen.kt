package com.example.farmer.ua.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.farmer.R

@Composable
fun HomeScreen(
    onFarmerClick: () -> Unit,
    onConsumerClick: () -> Unit
) {
    // Background image
    val backgroundPainter: Painter = painterResource(id = R.drawable.authback)

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
                .zIndex(-1f), // Ensure it stays in the background
            contentScale = ContentScale.FillBounds // Ensure the image covers the full screen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground // Adjust text color for visibility
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onFarmerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = "Farmer")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConsumerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = "Consumer")
            }
        }
    }
}
