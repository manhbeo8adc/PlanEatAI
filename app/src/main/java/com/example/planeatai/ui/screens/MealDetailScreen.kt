package com.example.planeatai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planeatai.R
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.planeatai.ui.model.Dish
import com.example.planeatai.ui.model.Nutrition
import com.example.planeatai.ui.model.Ingredient
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planeatai.ui.viewmodels.MealPlanViewModel
import android.util.Log
import org.json.JSONObject
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    mealType: String,
    mealName: String,
    mealDesc: String,
    mealImage: Int,
    tags: List<String>,
    time: String,
    servings: Int,
    dishes: List<Dish>,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: MealPlanViewModel = viewModel()
) {
    val context = LocalContext.current
    var dishDetail by remember(mealName) { mutableStateOf<Dish?>(null) }
    var loading by remember(mealName) { mutableStateOf(true) }
    var errorMessage by remember(mealName) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    // Nếu mealName rỗng thì báo người dùng tạo thực đơn
    if (mealName.isBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Chưa có món ăn nào. Hãy tạo thực đơn trước!", color = Color(0xFFAD1457), style = MaterialTheme.typography.titleMedium)
        }
        return
    }
    // Fetch chi tiết món bằng AI khi vào màn
    LaunchedEffect(mealName) {
        loading = true
        errorMessage = null
        try {
            dishDetail = viewModel.fetchDishDetailByName(mealName)
            if (dishDetail == null) {
                errorMessage = "Không thể lấy được chi tiết món ăn từ AI.\nVui lòng kiểm tra kết nối mạng và thử lại."
            }
        } catch (e: Exception) {
            Log.e("MealDetailScreen", "Error fetching dish detail", e)
            errorMessage = when {
                e.message?.contains("timeout", true) == true -> 
                    "Timeout khi lấy thông tin món ăn.\nVui lòng thử lại sau."
                e.message?.contains("network", true) == true -> 
                    "Lỗi kết nối mạng.\nVui lòng kiểm tra internet và thử lại."
                else -> 
                    "Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}\nVui lòng thử lại sau."
            }
        } finally {
            loading = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết món ăn", color = Color(0xFFAD1457)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa", tint = Color(0xFFAD1457))
                    }
                }
            )
        }
    ) { paddingValues ->
        // Banner, tên món, mô tả luôn hiện
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = mealImage),
                    contentDescription = mealName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(mealName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(mealDesc, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Nguyên liệu
            if (loading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Đang tải chi tiết món ăn...", color = Color(0xFFB266B2))
                    }
                }
            } else if (dishDetail != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Nguyên liệu", fontWeight = FontWeight.Bold, color = Color(0xFFAD1457), fontSize = 17.sp)
                        Spacer(Modifier.height(8.dp))
                        dishDetail!!.ingredients.forEach { ingredient ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE7F3)),
                                border = BorderStroke(1.dp, Color(0xFFE1BEE7))
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            ingredient.name, 
                                            Modifier.weight(1f), 
                                            fontSize = 15.sp, 
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFAD1457)
                                        )
                                        Text(
                                            ingredient.amount, 
                                            color = Color(0xFF616161), 
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    if (ingredient.calories > 0 || ingredient.protein > 0 || ingredient.carbs > 0 || ingredient.fat > 0) {
                                        Spacer(Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            if (ingredient.calories > 0) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        "${ingredient.calories}", 
                                                        fontSize = 12.sp, 
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFF6F00)
                                                    )
                                                    Text(
                                                        "kcal", 
                                                        fontSize = 10.sp, 
                                                        color = Color(0xFF616161)
                                                    )
                                                }
                                            }
                                            if (ingredient.protein > 0) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        String.format("%.1f", ingredient.protein), 
                                                        fontSize = 12.sp, 
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1976D2)
                                                    )
                                                    Text(
                                                        "protein", 
                                                        fontSize = 10.sp, 
                                                        color = Color(0xFF616161)
                                                    )
                                                }
                                            }
                                            if (ingredient.carbs > 0) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        String.format("%.1f", ingredient.carbs), 
                                                        fontSize = 12.sp, 
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFFA000)
                                                    )
                                                    Text(
                                                        "carbs", 
                                                        fontSize = 10.sp, 
                                                        color = Color(0xFF616161)
                                                    )
                                                }
                                            }
                                            if (ingredient.fat > 0) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        String.format("%.1f", ingredient.fat), 
                                                        fontSize = 12.sp, 
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFD32F2F)
                                                    )
                                                    Text(
                                                        "fat", 
                                                        fontSize = 10.sp, 
                                                        color = Color(0xFF616161)
                                                    )
                                                }
                                            }
                                        }
                                        if (ingredient.fiber > 0 || ingredient.sugar > 0) {
                                            Spacer(Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                if (ingredient.fiber > 0) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier.padding(end = 16.dp)
                                                    ) {
                                                        Text(
                                                            String.format("%.1f", ingredient.fiber), 
                                                            fontSize = 12.sp, 
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF388E3C)
                                                        )
                                                        Text(
                                                            "fiber", 
                                                            fontSize = 10.sp, 
                                                            color = Color(0xFF616161)
                                                        )
                                                    }
                                                }
                                                if (ingredient.sugar > 0) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text(
                                                            String.format("%.1f", ingredient.sugar), 
                                                            fontSize = 12.sp, 
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF8D6E63)
                                                        )
                                                        Text(
                                                            "sugar", 
                                                            fontSize = 10.sp, 
                                                            color = Color(0xFF616161)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Tab dinh dưỡng/hướng dẫn (luôn hiện)
            var tabIndex by remember { mutableStateOf(0) }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFCE7F3),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(4.dp)) {
                        Tab(
                            selected = tabIndex == 0,
                            onClick = { tabIndex = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (tabIndex == 0) Color.White else Color(0xFFFCE7F3))
                        ) {
                            Text(
                                "Thông tin dinh dưỡng",
                                color = if (tabIndex == 0) Color(0xFFAD1457) else Color(0xFF616161),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        Tab(
                            selected = tabIndex == 1,
                            onClick = { tabIndex = 1 },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (tabIndex == 1) Color.White else Color(0xFFFCE7F3))
                        ) {
                            Text(
                                "Hướng dẫn",
                                color = if (tabIndex == 1) Color(0xFFAD1457) else Color(0xFF616161),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            when (tabIndex) {
                0 -> {
                    if (loading) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE7F3)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(8.dp))
                                Text("Đang tải thông tin dinh dưỡng...", color = Color(0xFFB266B2))
                            }
                        }
                    } else if (dishDetail != null) {
                        // Card dinh dưỡng tổng cộng
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE7F3)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Thông tin dinh dưỡng tổng cộng", fontWeight = FontWeight.Bold, color = Color(0xFFAD1457), fontSize = 17.sp)
                                Spacer(Modifier.height(8.dp))
                                // Tính tổng dinh dưỡng từ nguyên liệu
                                val totalCalories = dishDetail!!.ingredients.sumOf { it.calories }
                                val totalProtein = dishDetail!!.ingredients.sumOf { it.protein.toDouble() }.toFloat()
                                val totalCarbs = dishDetail!!.ingredients.sumOf { it.carbs.toDouble() }.toFloat()
                                val totalFat = dishDetail!!.ingredients.sumOf { it.fat.toDouble() }.toFloat()
                                val totalFiber = dishDetail!!.ingredients.sumOf { it.fiber.toDouble() }.toFloat()
                                val totalSugar = dishDetail!!.ingredients.sumOf { it.sugar.toDouble() }.toFloat()
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Calories", color = Color(0xFFAD1457), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(Modifier.weight(1f))
                                    Text("${totalCalories} kcal", color = Color(0xFFAD1457), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFBCFE8))
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth((totalCalories / 400f).coerceAtMost(1f))
                                            .background(Color(0xFFFF69B4))
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Text("Protein", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text(String.format("%.1fg", totalProtein), color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("Carbs", color = Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text(String.format("%.1fg", totalCarbs), color = Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Text("Chất béo", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text(String.format("%.1fg", totalFat), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("Chất xơ", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text(String.format("%.1fg", totalFiber), color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Text("Sugar", color = Color(0xFF8D6E63), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text(String.format("%.1fg", totalSugar), color = Color(0xFF8D6E63), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Card hướng dẫn (luôn hiện nếu dishDetail != null)
                    if (loading) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(8.dp))
                                Text("Đang tải hướng dẫn...", color = Color(0xFFB266B2))
                            }
                        }
                    } else if (dishDetail != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Hướng dẫn chế biến", fontWeight = FontWeight.Bold, color = Color(0xFFAD1457), fontSize = 17.sp)
                                Spacer(Modifier.height(8.dp))
                                dishDetail!!.steps.forEachIndexed { idx, step ->
                                    Row(Modifier.padding(vertical = 4.dp)) {
                                        Text("${idx + 1}.", fontWeight = FontWeight.Bold, color = Color(0xFFAD1457))
                                        Spacer(Modifier.width(8.dp))
                                        Text(step, fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Nếu lỗi AI (dishDetail == null và !loading), hiện thông báo lỗi
            if (!loading && dishDetail == null && !errorMessage.isNullOrEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ Lỗi tải dữ liệu",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFF616161),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                // Retry fetch
                                loading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        dishDetail = viewModel.fetchDishDetailByName(mealName)
                                        if (dishDetail == null) {
                                            errorMessage = "Vẫn không thể lấy được chi tiết món ăn.\nVui lòng thử lại sau."
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Lỗi: ${e.message ?: "Không xác định"}"
                                    } finally {
                                        loading = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAD1457)
                            )
                        ) {
                            Text("Thử lại", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DishCard(dish: Dish, isSelected: Boolean, onSelect: () -> Unit, onShowRecipe: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onSelect() },
        border = if (isSelected) BorderStroke(2.dp, Color(0xFFEC4899)) else BorderStroke(1.dp, Color(0xFFFBCFE8)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(dish.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFAD1457), modifier = Modifier.padding(top = 8.dp))
            Text(dish.description, fontSize = 13.sp, color = Color(0xFF616161), maxLines = 2)
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                Button(onClick = onShowRecipe, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp), modifier = Modifier.height(28.dp)) {
                    Text("Công thức", fontSize = 13.sp)
                }
            }
        }
    }
}