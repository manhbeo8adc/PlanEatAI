package com.example.planeatai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planeatai.ui.viewmodels.MealPlanViewModel
import com.example.planeatai.ui.model.Dish
import com.example.planeatai.ui.model.Ingredient
import com.example.planeatai.ui.model.Nutrition
import kotlinx.coroutines.launch

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
    var dish by remember { mutableStateOf<Dish?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mealName) {
        if (mealName.isNotEmpty() && mealName != "Chưa có thực đơn") {
            isLoading = true
            scope.launch {
                dish = viewModel.fetchDishDetailByName(mealName)
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mealType) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header với tên món ăn
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = mealName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (dish?.description?.isNotEmpty() == true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dish!!.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Thông tin dinh dưỡng tổng
                dish?.let { dishData ->
                    item {
                        NutritionCard(
                            title = "📊 Thông tin dinh dưỡng tổng",
                            nutrition = dishData.nutrition
                        )
                    }
                }

                // Danh sách nguyên liệu
                dish?.let { dishData ->
                    if (dishData.ingredients.isNotEmpty()) {
                        item {
                            Text(
                                text = "🥕 Nguyên liệu chi tiết",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        items(dishData.ingredients) { ingredient ->
                            IngredientCard(ingredient = ingredient)
                        }

                        // Tính tổng dinh dưỡng từ nguyên liệu
                        item {
                            val totalNutrition = calculateTotalNutritionFromIngredients(dishData.ingredients)
                            NutritionCard(
                                title = "📈 Tổng dinh dưỡng (tính từ nguyên liệu)",
                                nutrition = totalNutrition
                            )
                        }
                    }
                }

                // Hướng dẫn nấu ăn
                dish?.let { dishData ->
                    if (dishData.steps.isNotEmpty()) {
                        item {
                            Text(
                                text = "👨‍🍳 Hướng dẫn nấu ăn",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        items(dishData.steps.size) { index ->
                            StepCard(
                                stepNumber = index + 1,
                                stepDescription = dishData.steps[index]
                            )
                        }
                    }
                }

                // Thông tin thời gian và khẩu phần
                dish?.let { dishData ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                InfoItem(
                                    icon = "⏱️",
                                    label = "Chuẩn bị",
                                    value = "${dishData.prepTime} phút"
                                )
                                InfoItem(
                                    icon = "🔥",
                                    label = "Nấu",
                                    value = "${dishData.cookTime} phút"
                                )
                                InfoItem(
                                    icon = "👥",
                                    label = "Khẩu phần",
                                    value = "$servings người"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionCard(title: String, nutrition: Nutrition) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("🟠", "Calo", "${nutrition.calories} kcal")
                NutritionItem("🔵", "Protein", "${nutrition.protein}g")
                NutritionItem("🟡", "Carbs", "${nutrition.carbs}g")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("🔴", "Fat", "${nutrition.fat}g")
                NutritionItem("🟢", "Fiber", "${nutrition.fiber}g")
                NutritionItem("🟤", "Sugar", "${nutrition.sugar}g")
            }
        }
    }
}

@Composable
private fun IngredientCard(ingredient: Ingredient) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = ingredient.amount,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("🟠", "Calo", "${ingredient.calories}")
                NutritionItem("🔵", "Protein", "${ingredient.protein}g")
                NutritionItem("🟡", "Carbs", "${ingredient.carbs}g")
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("🔴", "Fat", "${ingredient.fat}g")
                NutritionItem("🟢", "Fiber", "${ingredient.fiber}g")
                NutritionItem("🟤", "Sugar", "${ingredient.sugar}g")
            }
        }
    }
}

@Composable
private fun StepCard(stepNumber: Int, stepDescription: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stepDescription,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NutritionItem(emoji: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$emoji $label",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoItem(icon: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun calculateTotalNutritionFromIngredients(ingredients: List<Ingredient>): Nutrition {
    return Nutrition(
        calories = ingredients.sumOf { it.calories },
        protein = ingredients.sumOf { it.protein.toDouble() }.toFloat(),
        carbs = ingredients.sumOf { it.carbs.toDouble() }.toFloat(),
        fat = ingredients.sumOf { it.fat.toDouble() }.toFloat(),
        fiber = ingredients.sumOf { it.fiber.toDouble() }.toFloat(),
        sugar = ingredients.sumOf { it.sugar.toDouble() }.toFloat()
    )
}