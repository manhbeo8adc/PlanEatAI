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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planeatai.ui.viewmodels.MealPlanViewModel
import android.util.Log
import org.json.JSONObject

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
        dishDetail = viewModel.fetchDishDetailByName(mealName)
        loading = false
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
                        dishDetail!!.ingredients.forEach { (name, amount) ->
                            Row(Modifier.padding(vertical = 4.dp)) {
                                Text(name, Modifier.weight(1f), fontSize = 15.sp)
                                Text(amount, color = Color(0xFF616161), fontSize = 15.sp)
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
                        // Card dinh dưỡng
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
                                Text("Thông tin dinh dưỡng", fontWeight = FontWeight.Bold, color = Color(0xFFAD1457), fontSize = 17.sp)
                                Spacer(Modifier.height(8.dp))
                                val n = dishDetail!!.nutrition
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Calories", color = Color(0xFFAD1457), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(Modifier.weight(1f))
                                    Text("${n.calories} kcal", color = Color(0xFFAD1457), fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                                            .fillMaxWidth((n.calories / 400f).coerceAtMost(1f))
                                            .background(Color(0xFFFF69B4))
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Text("Protein", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("${n.protein}g", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("Carbs", color = Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("${n.carbs}g", color = Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Text("Chất béo", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("${n.fat}g", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("Chất xơ", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("${n.fiber}g", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Text("Sugar", color = Color(0xFF8D6E63), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Text("${n.sugar}g", color = Color(0xFF8D6E63), fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
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
            if (!loading && dishDetail == null) {
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
                        Text("Không thể lấy chi tiết món ăn.\nVui lòng thử lại sau.", color = Color.Red)
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