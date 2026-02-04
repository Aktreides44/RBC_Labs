package com.example.rbclabs

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rbclabs.ui.theme.RBCLabsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeleteBookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RBCLabsTheme {
                DeleteBookingScreen(
                    bookingId = intent.getStringExtra("BOOKING_ID") ?: "",
                    bookingDate = intent.getStringExtra("BOOKING_DATE") ?: "",
                    onFinish = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBookingScreen(
    bookingId: String,
    bookingDate: String,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Delete Booking") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Are you sure you want to delete this booking?",
                style = MaterialTheme.typography.titleMedium
            )

            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Appointment Date")
                    Text(
                        text = bookingDate,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Button(
                onClick = {
                    if (userId != null && bookingId.isNotEmpty()) {
                        db.collection("users")
                            .document(userId)
                            .collection("bookings")
                            .document(bookingId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Booking deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onFinish()
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Booking")
            }

            OutlinedButton(
                onClick = { onFinish() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
