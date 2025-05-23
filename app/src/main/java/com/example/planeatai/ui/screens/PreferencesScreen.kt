package com.example.planeatai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.example.planeatai.ui.model.UserPreferences
import com.example.planeatai.ui.model.MealPreferences
import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    initialPreferences: UserPreferences,
    onSave: (UserPreferences) -> Unit,
    onBack: () -> Unit
) {
    var favoriteFood by remember { mutableStateOf(initialPreferences.favoriteFood) }
    var dislikedFood by remember { mutableStateOf(initialPreferences.dislikedFood) }
    var breakfastPrefs by remember { mutableStateOf(initialPreferences.breakfastPrefs) }
    var lunchPrefs by remember { mutableStateOf(initialPreferences.lunchPrefs) }
    var dinnerPrefs by remember { mutableStateOf(initialPreferences.dinnerPrefs) }
    var cuisineStyles by remember { mutableStateOf(initialPreferences.cuisineStyles) }
    var servings by remember { mutableStateOf(initialPreferences.servings) }
    var additionalRequests by remember { mutableStateOf(initialPreferences.additionalRequests) }
    
    var showExitDialog by remember { mutableStateOf(false) }

    // Function để kiểm tra có thay đổi không
    fun hasChanges(): Boolean {
        return favoriteFood != initialPreferences.favoriteFood ||
                dislikedFood != initialPreferences.dislikedFood ||
                breakfastPrefs != initialPreferences.breakfastPrefs ||
                lunchPrefs != initialPreferences.lunchPrefs ||
                dinnerPrefs != initialPreferences.dinnerPrefs ||
                cuisineStyles != initialPreferences.cuisineStyles ||
                servings != initialPreferences.servings ||
                additionalRequests != initialPreferences.additionalRequests
    }

    // Function để lưu preferences
    fun saveCurrentPreferences() {
        val updatedPreferences = UserPreferences(
            favoriteFood = favoriteFood,
            dislikedFood = dislikedFood,
            breakfastPrefs = breakfastPrefs,
            lunchPrefs = lunchPrefs,
            dinnerPrefs = dinnerPrefs,
            cuisineStyles = cuisineStyles,
            servings = servings,
            additionalRequests = additionalRequests
        )
        onSave(updatedPreferences)
    }

    // Handle back button
    BackHandler {
        if (hasChanges()) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    // Dialog hỏi có lưu không khi thoát
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { 
                Text(
                    "💾 Lưu cài đặt?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    "Bạn đã thay đổi một số cài đặt. Bạn có muốn lưu những thay đổi này không?",
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        saveCurrentPreferences()
                        showExitDialog = false
                        onBack()
                    }
                ) {
                    Text("💾 Lưu & Thoát")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showExitDialog = false
                        onBack()
                    }
                ) {
                    Text("🚫 Không lưu")
                }
            }
        )
    }

    val availableCuisines = listOf(
        "Việt Nam", "Trung Quốc", "Nhật Bản", "Hàn Quốc", "Thái Lan",
        "Ấn Độ", "Ý", "Pháp", "Mỹ", "Địa Trung Hải", "Chay/Thuần chay"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔧 Cài đặt sở thích ăn uống") },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (hasChanges()) {
                            showExitDialog = true
                        } else {
                            onBack()
                        }
                    }) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Phần 1: Sở thích cá nhân
            PreferenceSection(
                title = "🍽️ Sở thích cá nhân",
                content = {
                    OutlinedTextField(
                        value = favoriteFood,
                        onValueChange = { favoriteFood = it },
                        label = { Text("💝 Món ăn yêu thích") },
                        placeholder = { Text("Ví dụ: Phở, Bún bò Huế, Sushi...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = dislikedFood,
                        onValueChange = { dislikedFood = it },
                        label = { Text("🚫 Món ăn không thích") },
                        placeholder = { Text("Ví dụ: Đồ cay, hải sản, nội tạng...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            )

            // Phần 2: Phong cách ẩm thực
            PreferenceSection(
                title = "🌍 Phong cách ẩm thực",
                content = {
                    Text(
                        "Chọn nhiều phong cách để có thực đơn đa dạng trong tuần:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Grid layout cho cuisine styles
                    for (i in availableCuisines.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = cuisineStyles.contains(availableCuisines[i]),
                                onClick = {
                                    cuisineStyles = if (cuisineStyles.contains(availableCuisines[i])) {
                                        cuisineStyles - availableCuisines[i]
                                    } else {
                                        cuisineStyles + availableCuisines[i]
                                    }
                                },
                                label = { Text(availableCuisines[i]) },
                                modifier = Modifier.weight(1f)
                            )
                            
                            if (i + 1 < availableCuisines.size) {
                                FilterChip(
                                    selected = cuisineStyles.contains(availableCuisines[i + 1]),
                                    onClick = {
                                        cuisineStyles = if (cuisineStyles.contains(availableCuisines[i + 1])) {
                                            cuisineStyles - availableCuisines[i + 1]
                                        } else {
                                            cuisineStyles + availableCuisines[i + 1]
                                        }
                                    },
                                    label = { Text(availableCuisines[i + 1]) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            )

            // Phần 3: Số người ăn
            PreferenceSection(
                title = "👥 Thông tin khẩu phần",
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Số người ăn:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        OutlinedTextField(
                            value = servings.toString(),
                            onValueChange = { value ->
                                value.toIntOrNull()?.let { servings = it.coerceIn(1, 10) }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                        
                        Text("người")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "ℹ️ Lưu ý về khẩu phần:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "• Thông tin dinh dưỡng: cho 1 người\n" +
                                "• Nguyên liệu & công thức: cho $servings người\n" +
                                "• Ngân sách: tổng cho $servings người",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )

            // Phần 4: Cài đặt từng bữa ăn
            MealPreferenceCard(
                title = "🌅 Bữa sáng",
                mealPrefs = breakfastPrefs,
                onPrefsChange = { breakfastPrefs = it },
                borderColor = MaterialTheme.colorScheme.tertiary,
                servings = servings
            )

            MealPreferenceCard(
                title = "🌞 Bữa trưa", 
                mealPrefs = lunchPrefs,
                onPrefsChange = { lunchPrefs = it },
                borderColor = MaterialTheme.colorScheme.primary,
                servings = servings
            )

            MealPreferenceCard(
                title = "🌙 Bữa tối",
                mealPrefs = dinnerPrefs,
                onPrefsChange = { dinnerPrefs = it },
                borderColor = MaterialTheme.colorScheme.secondary,
                servings = servings
            )

            // Phần 5: Yêu cầu bổ sung
            PreferenceSection(
                title = "📝 Yêu cầu bổ sung",
                content = {
                    OutlinedTextField(
                        value = additionalRequests,
                        onValueChange = { additionalRequests = it },
                        label = { Text("Yêu cầu khác") },
                        placeholder = { Text("Ví dụ: Ít dầu mỡ, nhiều rau xanh, dễ nấu, không gia vị nặng...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            )

            // Nút lưu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onBack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("❌ Hủy")
                }
                
                Button(
                    onClick = {
                        saveCurrentPreferences()
                        onBack()
                    },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "💾 Lưu cài đặt", 
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PreferenceSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun MealPreferenceCard(
    title: String,
    mealPrefs: MealPreferences,
    onPrefsChange: (MealPreferences) -> Unit,
    borderColor: androidx.compose.ui.graphics.Color,
    servings: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = borderColor.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = borderColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Thời gian chuẩn bị
            MealPreferenceItem(
                label = "⏱️ Thời gian chuẩn bị",
                value = mealPrefs.prepTime.toString(),
                unit = "phút",
                onValueChange = { value ->
                    value.toIntOrNull()?.let { newValue ->
                        onPrefsChange(mealPrefs.copy(prepTime = newValue.coerceIn(5, 180)))
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Calo mong muốn  
            MealPreferenceItem(
                label = "🔥 Calo mong muốn",
                value = mealPrefs.calories.toString(),
                unit = "kcal/người",
                onValueChange = { value ->
                    value.toIntOrNull()?.let { newValue ->
                        onPrefsChange(mealPrefs.copy(calories = newValue.coerceIn(100, 1500)))
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ngân sách
            MealPreferenceItem(
                label = "💰 Ngân sách",
                value = (mealPrefs.budget / 1000).toString(),
                unit = "k VND ($servings người)",
                onValueChange = { value ->
                    value.toIntOrNull()?.let { newValue ->
                        val budgetInVnd = (newValue * 1000).coerceIn(10000, 500000)
                        onPrefsChange(mealPrefs.copy(budget = budgetInVnd))
                    }
                }
            )
        }
    }
}

@Composable
private fun MealPreferenceItem(
    label: String,
    value: String,
    unit: String,
    onValueChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(80.dp),
            singleLine = true
        )
        
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )
    }
}