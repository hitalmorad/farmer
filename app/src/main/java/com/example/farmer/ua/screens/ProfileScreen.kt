package com.example.farmer.ua.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.farmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun ProfileScreen(onNavigateToLogin: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var user by remember { mutableStateOf<FirebaseUser?>(null) }
    var profileImageUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    fun updateUserProfile(document: DocumentSnapshot) {
        profileImageUrl = document.getString("profileImage") ?: ""
        username = document.getString("username") ?: ""
        email = document.getString("email") ?: ""
        phoneNumber = document.getString("phoneNumber") ?: ""
        address = document.getString("address") ?: ""
        role = document.getString("role") ?: ""
    }

    LaunchedEffect(key1 = auth.currentUser) {
        auth.currentUser?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        updateUserProfile(document)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileScreen", "Error fetching user data", exception)
                    onNavigateToLogin()
                }
        } ?: onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
           // .padding(16.dp)
            .background(Color.Black) // Black background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                fontSize = 24.sp,
                color = Color.Green,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Profile Image in Circular Frame
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUrl),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Default Profile Image",
                        tint = Color.Gray,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cards for User Details
            ProfileDetailCard(label = "Username", value = username)
            ProfileDetailCard(label = "Email", value = email)
            ProfileDetailCard(label = "Phone Number", value = phoneNumber)
            ProfileDetailCard(label = "Address", value = address)
            ProfileDetailCard(label = "Role", value = role)

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Button
            Button(
                onClick = {
                    auth.signOut()
                    onNavigateToLogin()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black)
            ) {
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun ProfileDetailCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                color = Color.Green,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                color = Color.Green,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
