package com.example.farmer.ua.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedOption by remember { mutableStateOf("Card") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    val isCardValid = cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Options") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose Payment Method",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PaymentOptionButton(
                        label = "Card",
                        isSelected = selectedOption == "Card",
                        onClick = { selectedOption = "Card" }
                    )
                    PaymentOptionButton(
                        label = "UPI",
                        isSelected = selectedOption == "UPI",
                        onClick = { selectedOption = "UPI" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedOption) {
                    "Card" -> {
                        CardPaymentSection(
                            cardNumber = cardNumber,
                            expiryDate = expiryDate,
                            cvv = cvv,
                            onCardNumberChange = { cardNumber = it },
                            onExpiryDateChange = { expiryDate = it },
                            onCvvChange = { cvv = it },
                            onPayClick = {
                                if (isCardValid) {
                                    Log.d("Payment", "Card Payment Successful")
                                    onPaymentSuccess()
                                } else {
                                    Log.d("Payment", "Invalid Card Details")
                                }
                            }
                        )
                    }
                    "UPI" -> {
                        UpiPaymentSection()
                    }
                }
            }
        }
    )
}

@Composable
fun PaymentOptionButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        ),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = label, color = Color.White)
    }
}

@Composable
fun CardPaymentSection(
    cardNumber: String,
    expiryDate: String,
    cvv: String,
    onCardNumberChange: (String) -> Unit,
    onExpiryDateChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onPayClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            label = { Text("Card Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expiryDate,
            onValueChange = onExpiryDateChange,
            label = { Text("Expiry Date (MM/YY)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cvv,
            onValueChange = onCvvChange,
            label = { Text("CVV") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onPayClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()
        ) {
            Text(text = "Pay")
        }
    }
}

@Composable
fun UpiPaymentSection() {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Pay using UPI",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            UpiAppIcon(
                context = context,
                label = "Google Pay",
                iconRes = R.drawable.gpay,
                upiLink = "upi://pay?pa=krushilmorad45@ybl&pn=GooglePay&tn=Payment&am=1.00"
            )
            UpiAppIcon(
                context = context,
                label = "PhonePe",
                iconRes = R.drawable.phonepy,
                upiLink = "upi://pay?pa=krushilmorad45@ybl&pn=PhonePe&tn=Payment&am=1.00"
            )
            UpiAppIcon(
                context = context,
                label = "Paytm",
                iconRes = R.drawable.paytm,
                upiLink = "upi://pay?pa=example@upi&pn=Paytm&tn=Payment&am=1.00"
            )
        }
    }
}

@Composable
fun UpiAppIcon(context: android.content.Context, label: String, iconRes: Int, upiLink: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiLink)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("UPI", "Error opening UPI app: ${e.message}")
            }
        }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(64.dp)
        )
        Text(text = label, fontSize = 14.sp)
    }
}
