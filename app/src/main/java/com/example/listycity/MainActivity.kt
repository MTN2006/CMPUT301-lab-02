package com.example.listycity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listycity.ui.theme.ListyCityTheme

// --- Step 5 (from PDF, unchanged) ---
// Keep mutable app data private so other classes cannot change it directly
class CityRepository {
    private val _cities = mutableStateListOf(
        "Edmonton", "Vancouver", "Moscow",
        "Sydney", "Berlin", "Vienna",
        "Tokyo", "Beijing", "Osaka",
        "New Delhi"
    )

    // Get a read-only list for the UI to display
    val cities: List<String>
        get() = _cities

    fun addCity(city: String) {
        _cities.add(city)
    }

    // NEW: not in the PDF — needed for the delete requirement
    fun removeCity(city: String) {
        _cities.remove(city)
    }
}

// --- Step 6/7 (from PDF, with onDeleteCity added in step 7's call) ---
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val cityRepository = CityRepository()

        setContent {
            ListyCityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CityListScreen(
                        cities = cityRepository.cities,
                        onAddCity = { cityRepository.addCity(it) },
                        onDeleteCity = { cityRepository.removeCity(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// --- Step 8/10, rebuilt: PDF's CityListScreen only supports add-on-type.
// This version adds: an ADD CITY toggle button that reveals a text field +
// CONFIRM button, tap-to-select on a city, and a DELETE CITY button that
// only works when a city is selected. ---
@Composable
fun CityListScreen(
    cities: List<String>,
    onAddCity: (String) -> Unit,
    onDeleteCity: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCityName by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // ADD CITY / DELETE CITY buttons
        Row(modifier = Modifier.padding(all = 16.dp)) {
            Button(
                onClick = { isAdding = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add City")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    selectedCity?.let { city ->
                        onDeleteCity(city)
                        selectedCity = null
                    }
                },
                enabled = selectedCity != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete City")
            }
        }

        // Text field + Confirm, shown only while adding
        if (isAdding) {
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = newCityName,
                    onValueChange = { newCityName = it },
                    label = { Text("City name") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newCityName.isNotBlank()) {
                            onAddCity(newCityName)
                            newCityName = ""
                            isAdding = false
                        }
                    }
                ) {
                    Text("Confirm")
                }
            }
        }

        // LazyColumn is the Compose replacement for a basic scrolling ListView
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(cities) { city ->
                CityRow(
                    city = city,
                    isSelected = city == selectedCity,
                    onClick = {
                        selectedCity = if (selectedCity == city) null else city
                    }
                )
            }
        }
    }
}

// --- Step 9, extended: PDF's CityRow just displays text. This version
// adds isSelected (for highlighting) and onClick (for tap-to-select). ---
@Composable
fun CityRow(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = city,
        fontSize = 28.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp)
    )
}