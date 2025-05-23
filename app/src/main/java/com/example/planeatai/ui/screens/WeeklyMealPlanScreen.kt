package com.example.planeatai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planeatai.R
import com.example.planeatai.ui.viewmodels.MealPlanViewModel
import com.example.planeatai.ui.viewmodels.MealPlanViewModelFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.graphics.graphicsLayer
import com.example.planeatai.ui.theme.Pink50
import com.example.planeatai.ui.theme.Pink100
import com.example.planeatai.ui.theme.Pink200
import com.example.planeatai.ui.theme.Pink400
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planeatai.ui.model.MealPlan
import com.example.planeatai.ui.model.Dish
import com.example.planeatai.ui.model.Nutrition
import com.example.planeatai.ui.model.UserPreferences
import android.util.Log
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.view.Gravity
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyMealPlanScreen() {
    val context = LocalContext.current
    val viewModel: MealPlanViewModel = viewModel(
        factory = MealPlanViewModelFactory(context)
    )
    val mealPlans by viewModel.mealPlans.collectAsState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isGenerating by remember { mutableStateOf(false) }
    val prevMealPlans = remember { mutableStateOf<List<MealPlan>>(emptyList()) }
    LaunchedEffect(Unit) {
        Log.d("WeeklyMealPlanScreen", "Kiểm tra và load thực đơn đã lưu khi mở app")
        viewModel.loadMealPlan()
    }
    LaunchedEffect(mealPlans) {
        if (isGenerating && mealPlans != prevMealPlans.value && mealPlans.isNotEmpty()) {
            isGenerating = false
            prevMealPlans.value = mealPlans
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD8B5).copy(alpha = 0.85f), // cam nhạt
                        Color(0xFFFFF9C4).copy(alpha = 0.85f), // vàng kem
                        Color.White.copy(alpha = 0.85f)
                    )
                )
            )
    ) {
        Column {
            NavHost(navController = navController, startDestination = "weekly") {
                composable("weekly") {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            Column {
                                TopAppBar(
                                    title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = R.drawable.banner_planeat_transparent),
                                                contentDescription = "Banner PlanEat",
                                                modifier = Modifier
                                                    .size(160.dp)
                                                    .padding(end = 8.dp)
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color.Transparent,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    actions = {
                                        IconButton(onClick = { 
                                            Log.d("WeeklyMealPlanScreen", "Nút save được bấm")
                                            viewModel.saveMealPlan("today")
                                            Toast.makeText(context, "Đã lưu thực đơn thành công", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(painter = painterResource(id = R.drawable.ic_save), contentDescription = "Lưu thực đơn")
                                        }
                                        IconButton(onClick = { 
                                            Log.d("WeeklyMealPlanScreen", "Nút load được bấm")
                                            viewModel.openSavedMenu()
                                            Toast.makeText(context, "Đã load thực đơn", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(painter = painterResource(id = R.drawable.ic_open_file), contentDescription = "Mở thực đơn đã lưu")
                                        }
                                        IconButton(
                                            onClick = { navController.navigate("preferences") },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(Icons.Default.Settings, contentDescription = "Cài đặt", tint = Color(0xFF616161))
                                        }
                                    }
                                )
                                AnimatedVisibility(
                                    visible = isGenerating,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                            .shadow(4.dp, RoundedCornerShape(16.dp))
                                            .background(Color(0xFFFBCFE8).copy(alpha = 0.95f), RoundedCornerShape(16.dp))
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            "Đang tạo thực đơn mới, vui lòng chờ...",
                                            Modifier.padding(vertical = 10.dp, horizontal = 24.dp),
                                            color = Color(0xFFB266B2),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    Log.d("WeeklyMealPlanScreen", "Nút cộng được bấm, gọi generateMealPlan")
                                    isGenerating = true
                                    viewModel.generateMealPlan()
                                },
                                containerColor = Pink400,
                                shape = CircleShape,
                                modifier = Modifier.padding(bottom = 24.dp, end = 24.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tạo thực đơn", tint = Color.White)
                            }
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { paddingValues ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (mealPlans.isEmpty()) {
                                val daysOfWeek = listOf(
                                    "Thứ Hai", "Thứ Ba", "Thứ Tư", 
                                    "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"
                                )
                                items(daysOfWeek) { dayName ->
                                    DayMealCard(
                                        date = dayName,
                                        mealPlan = null,
                                        navController = navController
                                    )
                                }
                            } else {
                                // Sắp xếp thực đơn theo thứ tự đúng
                                val sortedMealPlans = mealPlans.sortedBy { mealPlan ->
                                    when (mealPlan.day) {
                                        "Thứ Hai" -> 1
                                        "Thứ Ba" -> 2
                                        "Thứ Tư" -> 3
                                        "Thứ Năm" -> 4
                                        "Thứ Sáu" -> 5
                                        "Thứ Bảy" -> 6
                                        "Chủ Nhật" -> 7
                                        else -> 8
                                    }
                                }
                                items(sortedMealPlans) { mealPlan ->
                                    DayMealCard(
                                        date = mealPlan.day,
                                        mealPlan = mealPlan,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
                composable("mealDetail/{mealType}/{dishName}") { backStackEntry ->
                    val mealType = backStackEntry.arguments?.getString("mealType") ?: "Bữa ăn"
                    val dishName = backStackEntry.arguments?.getString("dishName") ?: ""
                    val prefs by viewModel.userPreferences.collectAsState()
                    MealDetailScreen(
                        mealType = mealType,
                        mealName = dishName,
                        mealDesc = "",
                        mealImage = R.drawable.banner_breakfast,
                        tags = listOf(),
                        time = "",
                        servings = prefs.servings,
                        dishes = emptyList(),
                        onBack = { navController.popBackStack() },
                        onEdit = { /* TODO: Edit meal */ },
                        viewModel = viewModel
                    )
                }
                composable("preferences") {
                    val prefs by viewModel.userPreferences.collectAsState()
                    PreferencesScreen(
                        initialPreferences = prefs,
                        onSave = { preferences ->
                            viewModel.savePreferences(preferences)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayMealCard(date: String, mealPlan: MealPlan?, navController: NavHostController) {
    // Nếu date là tên thứ, sử dụng trực tiếp. Nếu là LocalDate, parse và lấy tên thứ
    val dayName = if (date.startsWith("Thứ") || date == "Chủ Nhật") {
        date
    } else {
        try { 
            LocalDate.parse(date).dayOfWeek.getDisplayName(TextStyle.FULL, Locale("vi"))
        } catch (_: Exception) { 
            "Thứ Hai" 
        }
    }
    
    // Tạo short name cho circle
    val shortName = when (dayName) {
        "Thứ Hai" -> "T2"
        "Thứ Ba" -> "T3"
        "Thứ Tư" -> "T4"
        "Thứ Năm" -> "T5"
        "Thứ Sáu" -> "T6"
        "Thứ Bảy" -> "T7"
        "Chủ Nhật" -> "CN"
        else -> "T2"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Pink50.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.dp, Pink200),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Pink100,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = shortName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Pink400,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            MealSection(
                title = "Bữa sáng",
                content = mealPlan?.breakfast?.name ?: "Chưa có thực đơn",
                borderColor = Color(0xFFFFD8B5),
                titleColor = Color(0xFFFFB300),
                icon = "☀️",
                onClick = { navController.navigate("mealDetail/Bữa sáng/${mealPlan?.breakfast?.name ?: ""}") },
                dishName = mealPlan?.breakfast?.name ?: ""
            )
            MealSection(
                title = "Bữa trưa",
                content = mealPlan?.lunch?.name ?: "Chưa có thực đơn",
                borderColor = Color(0xFFC4FFD8),
                titleColor = Color(0xFF26A69A),
                icon = "🥗",
                onClick = { navController.navigate("mealDetail/Bữa trưa/${mealPlan?.lunch?.name ?: ""}") },
                dishName = mealPlan?.lunch?.name ?: ""
            )
            MealSection(
                title = "Bữa tối",
                content = mealPlan?.dinner?.name ?: "Chưa có thực đơn",
                borderColor = Color(0xFFD8B5FF),
                titleColor = Color(0xFF7C4DFF),
                icon = "🌙",
                onClick = { navController.navigate("mealDetail/Bữa tối/${mealPlan?.dinner?.name ?: ""}") },
                dishName = mealPlan?.dinner?.name ?: ""
            )
        }
    }
}

@Composable
fun MealSection(
    title: String,
    content: String,
    borderColor: Color,
    titleColor: Color,
    icon: String,
    onClick: () -> Unit,
    dishName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = borderColor.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, modifier = Modifier.padding(end = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa", tint = borderColor)
            }
        }
    }
} 