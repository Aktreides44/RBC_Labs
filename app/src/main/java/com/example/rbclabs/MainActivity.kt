package com.example.rbclabs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rbclabs.ui.theme.RBCLabsTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // Redirect to login if not authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            RBCLabsTheme {
                RBCMainPage { destination ->
                    when (destination) {
                        "MEDICAL" -> startActivity(
                            Intent(this, MedicalDetailsActivity::class.java)
                        )

                        "BOOK" -> startActivity(
                            Intent(this, BookAppointmentActivity::class.java)
                        )

                        "CHANGE" -> startActivity(
                            Intent(this, ChangeBookingActivity::class.java)
                        )

                        "HISTORY" -> startActivity(
                            Intent(this, BookingHistoryActivity::class.java)
                        )

                        "DELETE" -> startActivity(
                            Intent(this, DeleteBookingActivity::class.java)
                        )
                    }
                }
            }
        }
    }
}

data class MainMenuItem(
    val title: String,
    val subtitle: String,
    val route: String,
    val colorStart: Color,
    val colorEnd: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RBCMainPage(
    onItemClick: (String) -> Unit
) {
    val menuItems = listOf(
        MainMenuItem(
            title = "Add Medical Details",
            subtitle = "Blood group, weight, health info",
            route = "MEDICAL",
            colorStart = Color(0xFFEF5350),
            colorEnd = Color(0xFFF44336)
        ),
        MainMenuItem(
            title = "Book Appointment",
            subtitle = "Schedule your blood donation",
            route = "BOOK",
            colorStart = Color(0xFF42A5F5),
            colorEnd = Color(0xFF1E88E5)
        ),
        MainMenuItem(
            title = "Change Booking",
            subtitle = "Modify your existing appointment",
            route = "CHANGE",
            colorStart = Color(0xFF66BB6A),
            colorEnd = Color(0xFF43A047)
        ),
        MainMenuItem(
            title = "Booking History",
            subtitle = "View all past appointments",
            route = "HISTORY",
            colorStart = Color(0xFFAB47BC),
            colorEnd = Color(0xFF8E24AA)
        ),
        MainMenuItem(
            title = "Delete Booking",
            subtitle = "Cancel an existing appointment",
            route = "DELETE",
            colorStart = Color(0xFFFF7043),
            colorEnd = Color(0xFFF4511E)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RBC Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                vertical = 24.dp,
                horizontal = 16.dp
            )
        ) {
            items(menuItems) { item ->
                MenuCard(
                    item = item,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    item: MainMenuItem,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onItemClick(item.route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(item.colorStart, item.colorEnd)
                    )
                )
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RBCMainPagePreview() {
    RBCLabsTheme {
        RBCMainPage {}
    }
}
