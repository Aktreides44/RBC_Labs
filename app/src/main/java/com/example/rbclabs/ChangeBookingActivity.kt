package com.example.rbclabs

import android.app.DatePickerDialog
import android.os.Bundle
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
import kotlinx.coroutines.tasks.await
import java.util.*

class ChangeBookingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RBCLabsTheme {
                ChangeBookingScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeBookingScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedDate by remember { mutableStateOf("") }

    // Load existing booking
    LaunchedEffect(Unit) {
        if (userId != null) {
            try {
                val doc = db.collection("users")
                    .document(userId)
                    .collection("bookings")
                    .document("current")
                    .get()
                    .await()

                if (doc.exists()) {
                    selectedDate = doc.getString("date") ?: ""
                }
            } catch (_: Exception) { }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Change Booking") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedButton(
                onClick = {
                    val datePicker = DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            selectedDate = "$day/${month + 1}/$year"
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

                    val tomorrow = Calendar.getInstance()
                    tomorrow.add(Calendar.DAY_OF_MONTH, 1)
                    datePicker.datePicker.minDate = tomorrow.timeInMillis

                    datePicker.show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Current Booking: $selectedDate\nTap to Change")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedDate.isNotEmpty()) {
                Button(
                    onClick = {
                        if (userId != null) {
                            val bookingData = mapOf("date" to selectedDate)

                            db.collection("users")
                                .document(userId)
                                .collection("bookings")
                                .document("current")
                                .set(bookingData)
                                .addOnSuccessListener {

                                    // 🔔 LOCAL NOTIFICATION
                                    NotificationHelper.showNotification(
                                        context,
                                        "Booking Updated",
                                        "Your appointment is now on $selectedDate"
                                    )
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Booking")
                }
            }
        }
    }
}
