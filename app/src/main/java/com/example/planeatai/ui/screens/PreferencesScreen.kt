package com.example.planeatai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.example.planeatai.ui.theme.Pink50
import com.example.planeatai.ui.theme.Pink200
import com.example.planeatai.ui.theme.Pink400
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import kotlinx.serialization.Serializable
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    initialPreferences: PreferencesData? = null,
    onSave: (PreferencesData) -> Unit,
    onBack: () -> Unit
) {
    var servings by remember { mutableStateOf(initialPreferences?.servings ?: 4) }
    var favoriteDishes by remember { mutableStateOf(initialPreferences?.favoriteDishes ?: listOf<String>()) }
    var dislikeDishes by remember { mutableStateOf(initialPreferences?.dislikeDishes ?: listOf<String>()) }
    var favoriteIngredients by remember { mutableStateOf(initialPreferences?.favoriteIngredients ?: listOf<String>()) }
    var dislikeIngredients by remember { mutableStateOf(initialPreferences?.dislikeIngredients ?: listOf<String>()) }
    var selectedCuisines by remember { mutableStateOf(initialPreferences?.cuisines ?: listOf<String>()) }
    var maxPrepBreakfast by remember { mutableStateOf((initialPreferences?.maxPrepBreakfast ?: 20).toFloat()) }
    var maxPrepLunch by remember { mutableStateOf((initialPreferences?.maxPrepLunch ?: 30).toFloat()) }
    var maxPrepDinner by remember { mutableStateOf((initialPreferences?.maxPrepDinner ?: 30).toFloat()) }
    var costBreakfast by remember { mutableStateOf(initialPreferences?.costBreakfast ?: 30) }
    var costLunch by remember { mutableStateOf(initialPreferences?.costLunch ?: 50) }
    var costDinner by remember { mutableStateOf(initialPreferences?.costDinner ?: 40) }
    var caloBreakfast by remember { mutableStateOf(initialPreferences?.caloBreakfast ?: 300) }
    var caloLunch by remember { mutableStateOf(initialPreferences?.caloLunch ?: 500) }
    var caloDinner by remember { mutableStateOf(initialPreferences?.caloDinner ?: 400) }

    var dishInput by remember { mutableStateOf(TextFieldValue()) }
    var dislikeDishInput by remember { mutableStateOf(TextFieldValue()) }
    var ingredientInput by remember { mutableStateOf(TextFieldValue()) }
    var dislikeIngredientInput by remember { mutableStateOf(TextFieldValue()) }

    val allCuisines = listOf(
        "Việt Nam", "Miền Bắc", "Miền Trung", "Miền Nam", "Hàn Quốc", "Nhật Bản", "Trung Quốc", "Thái Lan", "Ấn Độ", "Châu Âu", "Địa Trung Hải", "Mỹ", "Mexico", "Brazil", "Chay", "Healthy", "Low-carb", "Keto", "Fusion", "Truyền thống", "Nhanh gọn", "Ăn kiêng", "Đặc sản vùng miền", "Đồ nướng", "Đồ biển", "Ăn sáng", "Ăn trưa", "Ăn tối"
    )

    var showSaveDialog by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    // Lưu state ban đầu để so sánh
    val initialPrefs = remember {
        PreferencesData(
            servings,
            favoriteDishes,
            dislikeDishes,
            favoriteIngredients,
            dislikeIngredients,
            selectedCuisines,
            maxPrepBreakfast.toInt(),
            maxPrepLunch.toInt(),
            maxPrepDinner.toInt(),
            costBreakfast,
            costLunch,
            costDinner,
            caloBreakfast,
            caloLunch,
            caloDinner
        )
    }

    fun currentPrefs() = PreferencesData(
        servings,
        favoriteDishes,
        dislikeDishes,
        favoriteIngredients,
        dislikeIngredients,
        selectedCuisines,
        maxPrepBreakfast.toInt(),
        maxPrepLunch.toInt(),
        maxPrepDinner.toInt(),
        costBreakfast,
        costLunch,
        costDinner,
        caloBreakfast,
        caloLunch,
        caloDinner
    )

    var showBackDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Pink50)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (currentPrefs() != initialPrefs) showBackDialog = true else onBack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
            Spacer(Modifier.width(4.dp))
            Text("Cài đặt sở thích", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Pink400)
        }
        // Khẩu phần ăn
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Khẩu phần ăn (số người)", fontWeight = FontWeight.SemiBold, color = Pink400)
                Row(
                    Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { if (servings > 1) servings-- },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Pink200),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                    }
                    Text(
                        "$servings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Pink400,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Button(
                        onClick = { if (servings < 10) servings++ },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Pink200),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                    }
                }
            }
        }
        // Món yêu thích
        TagInputCard(
            label = "Món yêu thích",
            tags = favoriteDishes,
            input = dishInput,
            onInputChange = { dishInput = it },
            onAdd = {
                if (dishInput.text.isNotBlank()) {
                    favoriteDishes = favoriteDishes + dishInput.text.trim()
                    dishInput = TextFieldValue()
                }
            },
            onRemove = { favoriteDishes = favoriteDishes - it }
        )
        // Món không thích
        TagInputCard(
            label = "Món không thích",
            tags = dislikeDishes,
            input = dislikeDishInput,
            onInputChange = { dislikeDishInput = it },
            onAdd = {
                if (dislikeDishInput.text.isNotBlank()) {
                    dislikeDishes = dislikeDishes + dislikeDishInput.text.trim()
                    dislikeDishInput = TextFieldValue()
                }
            },
            onRemove = { dislikeDishes = dislikeDishes - it }
        )
        // Nguyên liệu yêu thích
        TagInputCard(
            label = "Nguyên liệu yêu thích",
            tags = favoriteIngredients,
            input = ingredientInput,
            onInputChange = { ingredientInput = it },
            onAdd = {
                if (ingredientInput.text.isNotBlank()) {
                    favoriteIngredients = favoriteIngredients + ingredientInput.text.trim()
                    ingredientInput = TextFieldValue()
                }
            },
            onRemove = { favoriteIngredients = favoriteIngredients - it }
        )
        // Nguyên liệu không thích
        TagInputCard(
            label = "Nguyên liệu không thích",
            tags = dislikeIngredients,
            input = dislikeIngredientInput,
            onInputChange = { dislikeIngredientInput = it },
            onAdd = {
                if (dislikeIngredientInput.text.isNotBlank()) {
                    dislikeIngredients = dislikeIngredients + dislikeIngredientInput.text.trim()
                    dislikeIngredientInput = TextFieldValue()
                }
            },
            onRemove = { dislikeIngredients = dislikeIngredients - it }
        )
        // Phong cách ẩm thực yêu thích
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Phong cách ẩm thực yêu thích", fontWeight = FontWeight.SemiBold, color = Pink400)
                Column(Modifier.padding(top = 8.dp)) {
                    allCuisines.chunked(3).forEach { rowCuisines ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowCuisines.forEach { cuisine ->
                                val selected = cuisine in selectedCuisines
                                Box(
                                    Modifier
                                        .background(if (selected) Pink200 else Pink50, shape = RoundedCornerShape(16.dp))
                                        .border(1.dp, Pink200, shape = RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .clickable {
                                            selectedCuisines = if (selected) selectedCuisines - cuisine else selectedCuisines + cuisine
                                        }
                                ) {
                                    Text(cuisine, color = if (selected) Color.White else Pink400, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
        // Thời gian chuẩn bị tối đa cho từng bữa
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Thời gian chuẩn bị tối đa cho từng bữa (phút)", fontWeight = FontWeight.SemiBold, color = Pink400)
                Spacer(Modifier.height(8.dp))
                PrettySlider(
                    value = maxPrepBreakfast,
                    onValueChange = { maxPrepBreakfast = it },
                    valueRange = 5f..60f,
                    steps = 11,
                    label = "Sáng"
                )
                PrettySlider(
                    value = maxPrepLunch,
                    onValueChange = { maxPrepLunch = it },
                    valueRange = 5f..90f,
                    steps = 17,
                    label = "Trưa"
                )
                PrettySlider(
                    value = maxPrepDinner,
                    onValueChange = { maxPrepDinner = it },
                    valueRange = 5f..90f,
                    steps = 17,
                    label = "Tối"
                )
            }
        }
        // Chi phí dự kiến cho từng bữa
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Chi phí dự kiến cho từng bữa (nghìn VND)", fontWeight = FontWeight.SemiBold, color = Pink400)
                Spacer(Modifier.height(8.dp))
                PrettySlider(
                    value = costBreakfast.toFloat(),
                    onValueChange = { costBreakfast = (it/10).toInt()*10 },
                    valueRange = 0f..500f,
                    steps = 49,
                    label = "Sáng",
                    unit = "k"
                )
                PrettySlider(
                    value = costLunch.toFloat(),
                    onValueChange = { costLunch = (it/10).toInt()*10 },
                    valueRange = 0f..500f,
                    steps = 49,
                    label = "Trưa",
                    unit = "k"
                )
                PrettySlider(
                    value = costDinner.toFloat(),
                    onValueChange = { costDinner = (it/10).toInt()*10 },
                    valueRange = 0f..500f,
                    steps = 49,
                    label = "Tối",
                    unit = "k"
                )
            }
        }
        // Calories cho từng bữa
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Lượng calo mục tiêu cho từng bữa (kcal)", fontWeight = FontWeight.SemiBold, color = Pink400)
                Spacer(Modifier.height(8.dp))
                PrettySlider(
                    value = caloBreakfast.toFloat(),
                    onValueChange = { caloBreakfast = (it/50).toInt()*50 },
                    valueRange = 50f..1500f,
                    steps = 29,
                    label = "Sáng",
                    unit = ""
                )
                PrettySlider(
                    value = caloLunch.toFloat(),
                    onValueChange = { caloLunch = (it/50).toInt()*50 },
                    valueRange = 50f..1500f,
                    steps = 29,
                    label = "Trưa",
                    unit = ""
                )
                PrettySlider(
                    value = caloDinner.toFloat(),
                    onValueChange = { caloDinner = (it/50).toInt()*50 },
                    valueRange = 50f..1500f,
                    steps = 29,
                    label = "Tối",
                    unit = ""
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                isSaved = true
                onSave(currentPrefs())
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pink400)
        ) {
            Text("Lưu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }
    }
    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("Bạn có muốn lưu lại thay đổi?", color = Pink400, fontWeight = FontWeight.Bold) },
            confirmButton = {
                TextButton(onClick = {
                    isSaved = true
                    showBackDialog = false
                    onSave(currentPrefs())
                }) {
                    Text("Lưu", color = Pink400, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBackDialog = false
                    isSaved = true
                    onBack()
                }) {
                    Text("Không lưu", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun TagInputCard(
    label: String,
    tags: List<String>,
    input: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(label, fontWeight = FontWeight.SemiBold, color = Pink400)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text("Nhập...", color = Pink200) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Pink200,
                        unfocusedBorderColor = Pink50,
                        focusedLabelColor = Pink400
                    ),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onAdd,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Pink200),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                }
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                tags.forEach { tag ->
                    Box(
                        Modifier
                            .background(Pink50, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, Pink200, shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tag, color = Pink400, fontWeight = FontWeight.Medium)
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Xoá",
                                tint = Pink200,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onRemove(tag) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom slider cho từng bữa, thêm màu tick/step
@Composable
fun PrettySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    label: String,
    unit: String = "p"
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Pink400, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Box(Modifier.height(36.dp)) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = Pink400,
                    activeTrackColor = Pink200,
                    inactiveTrackColor = Pink50,
                    activeTickColor = Pink200,
                    inactiveTickColor = Pink200
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            )
            Text(
                "${value.toInt()}$unit",
                color = Pink400,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-16).dp)
            )
        }
    }
}

// Data class lưu preferences
@Serializable
data class PreferencesData(
    val servings: Int,
    val favoriteDishes: List<String>,
    val dislikeDishes: List<String>,
    val favoriteIngredients: List<String>,
    val dislikeIngredients: List<String>,
    val cuisines: List<String>,
    val maxPrepBreakfast: Int,
    val maxPrepLunch: Int,
    val maxPrepDinner: Int,
    val costBreakfast: Int,
    val costLunch: Int,
    val costDinner: Int,
    val caloBreakfast: Int,
    val caloLunch: Int,
    val caloDinner: Int
) 