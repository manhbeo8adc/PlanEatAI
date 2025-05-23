package com.example.planeatai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.planeatai.ui.model.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    initialPreferences: UserPreferences,
    onSave: (UserPreferences) -> Unit,
    onBack: () -> Unit
) {
    var goals by remember { mutableStateOf(initialPreferences.goals) }
    var preferences by remember { mutableStateOf(initialPreferences.preferences) }
    var additionalRequests by remember { mutableStateOf(initialPreferences.additionalRequests) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt sở thích") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Mục tiêu ăn uống",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = goals,
                onValueChange = { goals = it },
                label = { Text("Mục tiêu") },
                placeholder = { Text("Ví dụ: Giảm cân, tăng cân, ăn uống cân bằng...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text(
                "Sở thích ăn uống",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = preferences,
                onValueChange = { preferences = it },
                label = { Text("Sở thích") },
                placeholder = { Text("Ví dụ: Thích ăn chay, không ăn hải sản, thích đồ chua ngọt...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text(
                "Yêu cầu bổ sung",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = additionalRequests,
                onValueChange = { additionalRequests = it },
                label = { Text("Yêu cầu khác") },
                placeholder = { Text("Ví dụ: Ít dầu mỡ, nhiều rau xanh, dễ nấu...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSave(UserPreferences(goals, preferences, additionalRequests))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu cài đặt")
            }
        }
    }
}