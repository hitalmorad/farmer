package com.example.farmer.ua.screens

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.farmer.CloudinaryConfig
import com.example.farmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors

@Composable
fun SignupScreen(onNavigateToLogin: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val cloudinary = CloudinaryConfig.cloudinary
    val context = LocalContext.current

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var role by remember { mutableStateOf(TextFieldValue("")) } // e.g., "Farmer", "Consumer"

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf("") }
    val backgroundPainter: Painter = painterResource(id = R.drawable.authback)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
          //  .padding(16.dp)
    ) {
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
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Signup",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Image Picker
            Box(
                modifier = Modifier
                    .size(150.dp)  // Circular size
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)  // Add border to circle
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    Icon(
                        painter = painterResource(id = R.drawable.user),  // Default placeholder icon
                        contentDescription = "Select Profile Image",
                        tint = Color.Gray,
                        modifier = Modifier.size(80.dp)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Username Field
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address Field
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Field
            TextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Role (Farmer/Consumer)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF32CD32))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Signup Button
            Button(
                onClick = {
                    if (selectedImageUri != null && username.text.isNotEmpty() &&
                        email.text.isNotEmpty() && password.text.isNotEmpty() &&
                        confirmPassword.text.isNotEmpty() && password.text == confirmPassword.text &&
                        phoneNumber.text.isNotEmpty() && address.text.isNotEmpty() &&
                        role.text.isNotEmpty()
                    ) {
                        isUploading = true
                        uploadMessage = ""
                        signUpWithImage(
                            context = context,
                            cloudinary = cloudinary,
                            firestore = firestore,
                            auth = auth,
                            imageUri = selectedImageUri!!,
                            username = username.text,
                            email = email.text,
                            password = password.text,
                            phoneNumber = phoneNumber.text,
                            address = address.text,
                            role = role.text,
                            onSignupSuccess = {
                                uploadMessage = "Signup successful!"
                                isUploading = false
                                onNavigateToLogin()

                            },
                            onSignupError = { error ->
                                uploadMessage = error ?: "Error during signup."
                                isUploading = false
                            }
                        )
                    } else {
                        uploadMessage = "Please fill all fields, ensure passwords match, and select an image."
                    }
                },

                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32CD32))
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Message
            if (uploadMessage.isNotEmpty()) {
                Text(text = uploadMessage, color = Color.Red)
            }
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login", color = Color.White)
            }
        }
    }
}

private fun signUpWithImage(
    context: Context,
    cloudinary: Cloudinary,
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    imageUri: Uri,
    username: String,
    email: String,
    password: String,
    phoneNumber: String,
    address: String,
    role: String,
    onSignupSuccess: () -> Unit,
    onSignupError: (String?) -> Unit
) {
    val filePath = getFilePathFromUri(context, imageUri)

    if (filePath != null) {
        val options = ObjectUtils.asMap(
            "public_id", "users/${System.currentTimeMillis()}"
        )

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val uploadResult = cloudinary.uploader().upload(filePath, options)
                val imageUrl = uploadResult["secure_url"] as String

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val userId = result.user?.uid ?: return@addOnSuccessListener
                        val userData = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "profileImage" to imageUrl,
                            "phoneNumber" to phoneNumber,
                            "address" to address,
                            "role" to role
                        )
                        firestore.collection("users").document(userId).set(userData)
                            .addOnSuccessListener { onSignupSuccess() }
                            .addOnFailureListener { exception -> onSignupError(exception.message) }
                    }
                    .addOnFailureListener { exception -> onSignupError(exception.message) }
            } catch (e: Exception) {
                onSignupError("Cloudinary upload failed: ${e.message}")
            }
        }
    } else {
        onSignupError("Failed to retrieve file path.")
    }
}

private fun getFilePathFromUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    return try {
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        }
    } catch (e: Exception) {
        null
    }
}
