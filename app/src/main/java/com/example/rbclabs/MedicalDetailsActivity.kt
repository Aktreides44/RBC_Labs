package com.example.rbclabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rbclabs.ui.theme.RBCLabsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MedicalDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RBCLabsTheme {
                MedicalDetailsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDetailsScreen() {

    // Form states
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var medicalCondition by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedBloodGroup by remember { mutableStateOf("A+") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val genders = listOf("Male", "Female", "Other")

    // Firebase references
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    // Load user's existing medical data
    LaunchedEffect(Unit) {
        if (userId != null) {
            try {
                val doc = db.collection("users")
                    .document(userId)
                    .collection("medical")
                    .document("details")
                    .get()
                    .await()

                if (doc.exists()) {
                    age = doc.getString("age") ?: ""
                    weight = doc.getString("weight") ?: ""
                    medicalCondition = doc.getString("medicalCondition") ?: ""
                    selectedBloodGroup = doc.getString("bloodGroup") ?: "A+"
                    selectedGender = doc.getString("gender") ?: "Male"
                }
            } catch (_: Exception) {
                // optional: handle error
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Details", color = MaterialTheme.colorScheme.primary) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {

            // Blood Group
            Text("Blood Group", color = MaterialTheme.colorScheme.primary)
            DropdownField(
                label = "Select Blood Group",
                items = bloodGroups,
                selectedItem = selectedBloodGroup,
                onItemSelected = { selectedBloodGroup = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Age
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Gender
            Text("Gender", color = MaterialTheme.colorScheme.primary)
            Row {
                genders.forEach { gender ->
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(gender)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Medical condition
            OutlinedTextField(
                value = medicalCondition,
                onValueChange = { medicalCondition = it },
                label = { Text("Medical Conditions (if any)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    if (userId != null) {
                        val medicalData = mapOf(
                            "age" to age,
                            "weight" to weight,
                            "bloodGroup" to selectedBloodGroup,
                            "gender" to selectedGender,
                            "medicalCondition" to medicalCondition
                        )
                        db.collection("users")
                            .document(userId)
                            .collection("medical")
                            .document("details")
                            .set(medicalData)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save Medical Details")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .menuAnchor() // very important to anchor the menu
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MedicalDetailsPreview() {
    RBCLabsTheme {
        MedicalDetailsScreen()
    }
}
