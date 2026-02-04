package com.example.rbclabs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rbclabs.ui.theme.RBCLabsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RBCLabsTheme {
                BookingHistoryScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val context = LocalContext.current

    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

    // 🔥 Load booking history
    LaunchedEffect(Unit) {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("bookings")
                .get()
                .addOnSuccessListener { snapshot ->
                    bookings = snapshot.documents.map { doc ->
                        Booking(
                            bookingId = doc.id,
                            date = doc.getString("date") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0L
                        )
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Booking History") })
        }
    ) { padding ->

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No booking history available")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(
                                    context,
                                    DeleteBookingActivity::class.java
                                )
                                intent.putExtra("BOOKING_ID", booking.bookingId)
                                intent.putExtra("BOOKING_DATE", booking.date)
                                context.startActivity(intent)
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Appointment Date",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = booking.date,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
