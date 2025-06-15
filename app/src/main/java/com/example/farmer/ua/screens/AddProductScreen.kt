package com.example.farmer.ua.screens

import android.content.Context
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.farmer.*
import com.example.farmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.util.concurrent.Executors

@Composable
fun AddProductScreen(onNavigateBack: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val cloudinary = CloudinaryConfig.cloudinary
    val context = LocalContext.current // Cloudinary instance

    var productName by remember { mutableStateOf(TextFieldValue("")) }
    var productPrice by remember { mutableStateOf(TextFieldValue("")) }
    var productWeight by remember { mutableStateOf(TextFieldValue("")) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )


    val backgroundImage = painterResource(id = R.drawable.product)

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(brush = Brush.verticalGradient(listOf(Color.Gray, Color.White)))
    ) {

        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Product",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .then(
                        if (selectedImageUri == null) {
                            Modifier.border(2.dp, Color.LightGray)
                                .padding(4.dp)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {

                    Text(
                        text = "Tap to select image",
                        color = Color.Black,
                        fontSize = 18.sp
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


            TextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("ProductName", color = Color.Gray) },
                //visualTransformation = PasswordVisualTransformation(),
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .border(1.dp, Color.Gray),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF90EE90))
            )

            Spacer(modifier = Modifier.height(24.dp))




           TextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Product Price",color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .border(1.dp, Color.Gray),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF90EE90))
            )

            Spacer(modifier = Modifier.height(24.dp))


            TextField(
                value = productWeight,
                onValueChange = { productWeight = it },
                label = { Text("Product Weight" , color = Color.Gray)  },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .border(1.dp, Color.Gray),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF90EE90))
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (selectedImageUri != null && productName.text.isNotEmpty() &&
                        productPrice.text.isNotEmpty() && productWeight.text.isNotEmpty() && userId != null
                    ) {
                        isUploading = true
                        uploadMessage = ""
                        uploadProductToCloudinary(
                            context = context,
                            cloudinary = cloudinary,
                            firestore = firestore,
                            imageUri = selectedImageUri!!,
                            name = productName.text,
                            price = productPrice.text,
                            weight = productWeight.text,
                            userId = userId,
                            onUploadSuccess = {
                                uploadMessage = "Product uploaded successfully!"
                                isUploading = false
                                onNavigateBack()
                            },
                            onUploadError = { error ->
                                uploadMessage = error ?: "Error uploading product."
                                isUploading = false
                            }
                        )
                    } else {
                        uploadMessage = "Please fill all fields and select an image."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Add Product")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upload Message
            if (uploadMessage.isNotEmpty()) {
                Text(text = uploadMessage, color = Color.Blue)
            }
        }
    }
}


private fun uploadProductToCloudinary(
    context: Context,
    cloudinary: Cloudinary,
    firestore: FirebaseFirestore,
    imageUri: Uri,
    name: String,
    price: String,
    weight: String,
    userId: String,
    onUploadSuccess: () -> Unit,
    onUploadError: (String?) -> Unit
) {
    val filePath = getFilePathFromUri(context, imageUri)

    if (filePath != null) {
        val options = ObjectUtils.asMap(
            "public_id", "products/${userId}/${System.currentTimeMillis()}"
        )

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val uploadResult = cloudinary.uploader().upload(filePath, options)
                val imageUrl = uploadResult["secure_url"] as String

                val productData = hashMapOf(
                    "name" to name,
                    "price" to price,
                    "weight" to weight,
                    "imageUrl" to imageUrl
                )

                Handler(Looper.getMainLooper()).post {
                    firestore.collection("users")
                        .document(userId)
                        .collection("products")
                        .add(productData)
                        .addOnSuccessListener { onUploadSuccess() }
                        .addOnFailureListener { exception -> onUploadError(exception.message) }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    onUploadError("Cloudinary upload failed: ${e.message}")
                }
            }
        }
    } else {
        onUploadError("Failed to retrieve file path.")
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

        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        tempFile.absolutePath
    }
}
