package com.example.planeatai.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planeatai.ui.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.example.planeatai.config.ApiConfig

@Serializable
data class SavedMealPlan(
    val date: String,
    val meals: List<MealPlan>
)

class MealPlanViewModel : ViewModel() {
    private val _mealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val mealPlans: StateFlow<List<MealPlan>> = _mealPlans.asStateFlow()

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = ApiConfig.MODEL_NAME,
        apiKey = ApiConfig.GEMINI_API_KEY
    )

    private fun parseFloatFromAny(value: Any?): Float {
        return when (value) {
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull() ?: 0f
            else -> 0f
        }
    }

    private fun parseIntFromAny(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
    }

    private fun extractJsonFromResponse(rawResponse: String): String {
        // Loại bỏ markdown wrapper
        val cleaned = rawResponse
            .replace("```json", "")
            .replace("```JSON", "")
            .replace("```", "")
            .trim()
        
        // Tìm vị trí bắt đầu của JSON object
        val start = cleaned.indexOf('{')
        if (start == -1) return cleaned
        
        // Đếm braces để tìm vị trí kết thúc JSON object
        var braceCount = 0
        var end = start
        
        for (i in start until cleaned.length) {
            when (cleaned[i]) {
                '{' -> braceCount++
                '}' -> {
                    braceCount--
                    if (braceCount == 0) {
                        end = i
                        break
                    }
                }
            }
        }
        
        // Extract chỉ JSON object, bỏ qua text thêm
        return if (end > start && braceCount == 0) {
            cleaned.substring(start, end + 1)
        } else {
            cleaned
        }
    }

    fun savePreferences(preferences: UserPreferences) {
        _userPreferences.value = preferences
    }

    fun generateMealPlan(goals: String, preferences: String, additionalRequests: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val prompt = """
                Tạo kế hoạch ăn uống 7 ngày cho mục tiêu: $goals
                Sở thích ăn uống: $preferences
                Yêu cầu bổ sung: $additionalRequests
                
                CHỈ TRẢ VỀ JSON, KHÔNG CÓ TEXT GIẢI THÍCH THÊM!
                
                Format JSON chính xác:
                {
                  "meals": [
                    {
                      "day": "Thứ Hai",
                      "breakfast": {
                        "name": "Tên món ăn",
                        "description": "Mô tả ngắn",
                        "calories": 400,
                        "protein": 20.5,
                        "carbs": 45.0,
                        "fat": 15.0,
                        "fiber": 8.0,
                        "sugar": 12.0
                      },
                      "lunch": { ... },
                      "dinner": { ... }
                    },
                    ...
                  ]
                }
                
                Lưu ý: Món ăn Việt Nam, thông tin dinh dưỡng chính xác. CHỈ JSON, KHÔNG TEXT THÊM!
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val jsonText = response.text ?: ""
                
                Log.d("MealPlanViewModel", "Raw response: $jsonText")
                
                // Extract chỉ JSON object, bỏ qua text thêm
                val cleanedJson = extractJsonFromResponse(jsonText)
                
                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(cleanedJson).jsonObject
                val mealsArray = jsonObject["meals"]?.jsonArray
                
                val mealPlanList = mutableListOf<MealPlan>()
                
                mealsArray?.forEach { mealElement ->
                    val mealObject = mealElement.jsonObject
                    val day = mealObject["day"]?.jsonPrimitive?.content ?: ""
                    
                    val breakfast = parseMeal(mealObject["breakfast"]?.jsonObject)
                    val lunch = parseMeal(mealObject["lunch"]?.jsonObject)
                    val dinner = parseMeal(mealObject["dinner"]?.jsonObject)
                    
                    mealPlanList.add(MealPlan(day, breakfast, lunch, dinner))
                }
                
                _mealPlans.value = mealPlanList
                
            } catch (e: Exception) {
                Log.e("MealPlanViewModel", "Error generating meal plan", e)
                val errorMsg = when {
                    e.message?.contains("API key not valid") == true ->
                        "⚠️ API Key không hợp lệ!\n\nVui lòng:\n1. Lấy API key mới từ: https://aistudio.google.com/\n2. Cập nhật vào file ApiConfig.kt\n3. Rebuild ứng dụng"
                    e.message?.contains("quota") == true ->
                        "⚠️ Đã hết quota API!\n\nVui lòng đợi reset quota hoặc upgrade plan."
                    e.message?.contains("permission") == true ->
                        "⚠️ Không có quyền truy cập!\n\nKiểm tra API key và permissions."
                    else ->
                        "Không thể tạo kế hoạch ăn uống: ${e.message}"
                }
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseMeal(mealJson: JsonObject?): Meal {
        if (mealJson == null) {
            return Meal("", "", Nutrition(0, 0f, 0f, 0f, 0f, 0f))
        }
        
        val name = mealJson["name"]?.jsonPrimitive?.content ?: ""
        val description = mealJson["description"]?.jsonPrimitive?.content ?: ""
        
        val calories = mealJson["calories"]?.jsonPrimitive?.intOrNull ?: 0
        val protein = mealJson["protein"]?.jsonPrimitive?.floatOrNull ?: 0f
        val carbs = mealJson["carbs"]?.jsonPrimitive?.floatOrNull ?: 0f
        val fat = mealJson["fat"]?.jsonPrimitive?.floatOrNull ?: 0f
        val fiber = mealJson["fiber"]?.jsonPrimitive?.floatOrNull ?: 0f
        val sugar = mealJson["sugar"]?.jsonPrimitive?.floatOrNull ?: 0f
        
        val nutrition = Nutrition(calories, protein, carbs, fat, fiber, sugar)
        
        return Meal(name, description, nutrition)
    }

    suspend fun fetchDishDetailByName(dishName: String): Dish? {
        return try {
            _isLoading.value = true
            _errorMessage.value = null
            
            val prompt = """
            CHỈ TRẢ VỀ JSON, KHÔNG CÓ TEXT GIẢI THÍCH THÊM!
            
            Thông tin chi tiết món ăn "$dishName":
            {
              "id": "unique_id",
              "name": "$dishName",
              "imageUrl": null,
              "description": "Mô tả chi tiết về món ăn",
              "prepTime": 15,
              "cookTime": 30,
              "nutrition": {
                "calories": 450,
                "protein": 25.5,
                "carbs": 35.0,
                "fat": 18.0,
                "fiber": 6.0,
                "sugar": 8.0
              },
              "ingredients": [
                {
                  "name": "Tên nguyên liệu",
                  "amount": "Số lượng",
                  "calories": 100,
                  "protein": 5.0,
                  "carbs": 15.0,
                  "fat": 2.0,
                  "fiber": 3.0,
                  "sugar": 1.0
                }
              ],
              "steps": [
                "Bước 1: ...",
                "Bước 2: ..."
              ]
            }
            
            Lưu ý: Món Việt Nam, thông tin chính xác. CHỈ JSON, KHÔNG TEXT THÊM!
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text ?: ""
            
            Log.d("DishDetail", "Raw response: $jsonText")
            
            // Extract chỉ JSON object, bỏ qua text thêm
            val cleanedJson = extractJsonFromResponse(jsonText)
            
            val json = Json { ignoreUnknownKeys = true }
            val dishObject = json.parseToJsonElement(cleanedJson).jsonObject
            
            val id = dishObject["id"]?.jsonPrimitive?.content ?: "dish_${System.currentTimeMillis()}"
            val name = dishObject["name"]?.jsonPrimitive?.content ?: dishName
            val imageUrl = dishObject["imageUrl"]?.jsonPrimitive?.contentOrNull
            val description = dishObject["description"]?.jsonPrimitive?.content ?: ""
            val prepTime = parseIntFromAny(dishObject["prepTime"]?.jsonPrimitive?.intOrNull)
            val cookTime = parseIntFromAny(dishObject["cookTime"]?.jsonPrimitive?.intOrNull)
            
            // Parse nutrition
            val nutritionObject = dishObject["nutrition"]?.jsonObject
            val nutrition = if (nutritionObject != null) {
                Nutrition(
                    calories = parseIntFromAny(nutritionObject["calories"]?.jsonPrimitive?.intOrNull),
                    protein = parseFloatFromAny(nutritionObject["protein"]?.jsonPrimitive?.floatOrNull),
                    carbs = parseFloatFromAny(nutritionObject["carbs"]?.jsonPrimitive?.floatOrNull),
                    fat = parseFloatFromAny(nutritionObject["fat"]?.jsonPrimitive?.floatOrNull),
                    fiber = parseFloatFromAny(nutritionObject["fiber"]?.jsonPrimitive?.floatOrNull),
                    sugar = parseFloatFromAny(nutritionObject["sugar"]?.jsonPrimitive?.floatOrNull)
                )
            } else {
                Nutrition(0, 0f, 0f, 0f, 0f, 0f)
            }
            
            // Parse ingredients
            val ingredientsArray = dishObject["ingredients"]?.jsonArray
            val ingredients = ingredientsArray?.map { ingredientElement ->
                val ingredientObject = ingredientElement.jsonObject
                Ingredient(
                    name = ingredientObject["name"]?.jsonPrimitive?.content ?: "",
                    amount = ingredientObject["amount"]?.jsonPrimitive?.content ?: "",
                    calories = parseIntFromAny(ingredientObject["calories"]?.jsonPrimitive?.intOrNull),
                    protein = parseFloatFromAny(ingredientObject["protein"]?.jsonPrimitive?.floatOrNull),
                    carbs = parseFloatFromAny(ingredientObject["carbs"]?.jsonPrimitive?.floatOrNull),
                    fat = parseFloatFromAny(ingredientObject["fat"]?.jsonPrimitive?.floatOrNull),
                    fiber = parseFloatFromAny(ingredientObject["fiber"]?.jsonPrimitive?.floatOrNull),
                    sugar = parseFloatFromAny(ingredientObject["sugar"]?.jsonPrimitive?.floatOrNull)
                )
            } ?: emptyList()
            
            // Parse steps
            val stepsArray = dishObject["steps"]?.jsonArray
            val steps = stepsArray?.map { it.jsonPrimitive.content } ?: emptyList()
            
            Dish(
                id = id,
                name = name,
                imageUrl = imageUrl,
                description = description,
                prepTime = prepTime,
                cookTime = cookTime,
                nutrition = nutrition,
                ingredients = ingredients,
                steps = steps
            )
            
        } catch (e: Exception) {
            Log.e("DishDetail", "Error fetching dish detail", e)
            val errorMsg = when {
                e.message?.contains("API key not valid") == true ->
                    "⚠️ API Key không hợp lệ! Vui lòng cập nhật API key trong ApiConfig.kt"
                e.message?.contains("quota") == true ->
                    "⚠️ Đã hết quota API! Vui lòng đợi reset quota."
                else ->
                    "Không thể tải thông tin món ăn: ${e.message}"
            }
            _errorMessage.value = errorMsg
            null
        } finally {
            _isLoading.value = false
        }
    }

    fun saveMealPlan(date: String) {
        // Implementation for saving meal plan
        Log.d("MealPlanViewModel", "Saving meal plan for $date")
    }

    fun loadMealPlan() {
        // Implementation for loading saved meal plans
        Log.d("MealPlanViewModel", "Loading saved meal plans")
    }

    fun openSavedMenu() {
        // Implementation for opening saved menu
        Log.d("MealPlanViewModel", "Opening saved menu")
    }
}