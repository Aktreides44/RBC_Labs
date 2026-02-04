package com.example.rbclabs

import android.app.DatePickerDialog
import android.os.Build
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
import kotlinx.coroutines.tasks.await
import java.util.*

class BookAppointmentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        setContent {
            RBCLabsTheme {
                BookAppointmentScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedDate by remember { mutableStateOf("") }
    var bookingExists by remember { mutableStateOf(false) }

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
                    bookingExists = selectedDate.isNotEmpty()
                }
            } catch (_: Exception) { }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Book Appointment") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (!bookingExists) {

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
                    Text(
                        if (selectedDate.isEmpty())
                            "Select Appointment Date"
                        else
                            "Appointment: $selectedDate"
                    )
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

                                        bookingExists = true

                                        Toast.makeText(
                                            context,
                                            "Booking Confirmed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // 🔔 LOCAL NOTIFICATION
                                        NotificationHelper.showNotification(
                                            context,
                                            "Booking Confirmed",
                                            "Your appointment is booked for $selectedDate"
                                        )
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Booking")
                    }
                }

            } else {
                Text(
                    "You already have a booking on $selectedDate.\nPlease change it if needed.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
