package com.example.planeatai.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import com.example.planeatai.BuildConfig
import android.util.Log
import com.example.planeatai.ui.model.Nutrition
import com.example.planeatai.ui.model.MealPlan
import com.example.planeatai.ui.model.Dish
import android.app.Application
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import com.example.planeatai.UiState
import com.example.planeatai.ui.screens.PreferencesData

class MealPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val _mealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val mealPlans: StateFlow<List<MealPlan>> = _mealPlans.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    private val mealPlanFileName = "mealplans.json"
    private val preferencesFileName = "user_preferences.json"
    
    private fun getMealPlanFile(): File = getApplication<Application>().filesDir.resolve(mealPlanFileName)
    private fun getPreferencesFile(): File = getApplication<Application>().filesDir.resolve(preferencesFileName)

    companion object {
        private val dishDetailCache = mutableMapOf<String, Dish>()
        private const val MAX_RETRIES = 3
        private const val TIMEOUT_MS = 30000L
    }

    var userPreferences: PreferencesData? = null
        private set

    init {
        loadUserPreferences()
    }

    private fun loadUserPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = getPreferencesFile()
                if (file.exists()) {
                    val json = file.readText()
                    userPreferences = Json.decodeFromString<PreferencesData>(json)
                    Log.d("MealPlanViewModel", "Loaded user preferences: $userPreferences")
                } else {
                    Log.d("MealPlanViewModel", "No saved preferences found")
                }
            } catch (e: Exception) {
                Log.e("MealPlanViewModel", "Error loading preferences", e)
            }
        }
    }

    fun savePreferences(preferences: PreferencesData) {
        Log.d("MealPlanViewModel", "savePreferences CALLED with: $preferences")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = Json.encodeToString(preferences)
                getPreferencesFile().writeText(json)
                userPreferences = preferences
                Log.d("MealPlanViewModel", "Saved preferences to file: $preferences")
            } catch (e: Exception) {
                Log.e("MealPlanViewModel", "Error saving preferences", e)
            }
        }
    }

    fun generateMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            var retryCount = 0
            var success = false

            while (retryCount < MAX_RETRIES && !success) {
                try {
                    Log.d("MealPlanViewModel", "generateMealPlan called (attempt ${retryCount + 1})")
                    val prefs = userPreferences
                    val prefsText = prefs?.let {
                        """
                        Sở thích người dùng:
                        - Món yêu thích: ${it.favoriteDishes.joinToString()}
                        - Món không thích: ${it.dislikeDishes.joinToString()}
                        - Nguyên liệu thích: ${it.favoriteIngredients.joinToString()}
                        - Nguyên liệu không thích: ${it.dislikeIngredients.joinToString()}
                        - Phong cách: ${it.cuisines.joinToString()}
                        - Khẩu phần: ${it.servings} người
                        - Thời gian tối đa: 
                          + Sáng: ${it.maxPrepBreakfast}p
                          + Trưa: ${it.maxPrepLunch}p
                          + Tối: ${it.maxPrepDinner}p
                        - Chi phí:
                          + Sáng: ${it.costBreakfast}k
                          + Trưa: ${it.costLunch}k
                          + Tối: ${it.costDinner}k
                        - Calo:
                          + Sáng: ${it.caloBreakfast}
                          + Trưa: ${it.caloLunch}
                          + Tối: ${it.caloDinner}
                        """.trimIndent()
                    } ?: "Không có thông tin sở thích người dùng"
                    
                    val prompt = """
                    $prefsText
                    
                    Hãy tạo thực đơn cho 7 ngày, mỗi ngày gồm bữa sáng, trưa, tối, theo sở thích người Việt, trả về JSON dạng: [{"date": "yyyy-MM-dd", "breakfast": "...", "lunch": "...", "dinner": "..."}, ...]
                    """.trimIndent()
                    
                    Log.d("MealPlanViewModel", "Full prompt for Gemini:\n$prompt")
                    
                    withTimeout(TIMEOUT_MS) {
                        val response = generativeModel.generateContent(
                            content {
                                text(prompt)
                            }
                        )
                        val raw = response.text ?: throw IllegalStateException("Empty response from Gemini")
                        Log.d("MealPlanViewModel", "Gemini raw response:\n$raw")
                        
                        val json = raw
                            .replace("```json", "")
                            .replace("```", "")
                            .trim()
                        Log.d("MealPlanViewModel", "Cleaned JSON:\n$json")
                        
                        if (!isValidJsonArray(json)) {
                            throw IllegalStateException("Invalid JSON array response")
                        }
                        
                        _mealPlans.value = parseMealPlans(json)
                        success = true
                        _uiState.value = UiState.Success("Generated meal plan successfully")
                    }
                } catch (e: Exception) {
                    retryCount++
                    Log.e("MealPlanViewModel", "Error generateMealPlan (attempt $retryCount)", e)
                    if (retryCount >= MAX_RETRIES) {
                        _uiState.value = UiState.Error("Failed to generate meal plan after $MAX_RETRIES attempts: ${e.message}")
                        _mealPlans.value = emptyList()
                    } else {
                        delay(1000L * retryCount)
                    }
                }
            }
        }
    }

    private fun parseMealPlans(json: String): List<MealPlan> {
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                MealPlan(
                    date = obj.getString("date"),
                    breakfast = obj.optString("breakfast", ""),
                    lunch = obj.optString("lunch", ""),
                    dinner = obj.optString("dinner", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = Json.encodeToString(_mealPlans.value)
                getMealPlanFile().writeText(json)
                Log.d("MealPlanViewModel", "Saved meal plan to file")
            } catch (e: Exception) {
                Log.e("MealPlanViewModel", "Error saving meal plan", e)
            }
        }
    }

    fun loadMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = getMealPlanFile()
                if (file.exists()) {
                    val json = file.readText()
                    val list = Json.decodeFromString<List<MealPlan>>(json)
                    _mealPlans.value = list
                    Log.d("MealPlanViewModel", "Loaded meal plan from file")
                }
            } catch (e: Exception) {
                Log.e("MealPlanViewModel", "Error loading meal plan", e)
            }
        }
    }

    suspend fun generateDishNutrition(dishName: String): Nutrition? {
        Log.d("GeminiAPI", "generateDishNutrition called for $dishName")
        return try {
            val prompt = "Hãy phân tích thành phần dinh dưỡng cho món '$dishName' và trả về JSON: {\"calories\": số, \"protein\": số, \"carbs\": số, \"fat\": số, \"fiber\": số, \"sugar\": số} (đơn vị: kcal, g)"
            Log.d("GeminiAPI", "Gemini nutrition prompt: $prompt")
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            val raw = response.text?.replace("```json", "")?.replace("```", "")?.trim() ?: return null
            Log.d("GeminiAPI", "Gemini nutrition raw response for $dishName: $raw")
            val obj = JSONObject(raw)
            val nutrition = Nutrition(
                calories = obj.optInt("calories", 0),
                protein = obj.optInt("protein", 0),
                carbs = obj.optInt("carbs", 0),
                fat = obj.optInt("fat", 0),
                fiber = obj.optInt("fiber", 0),
                sugar = obj.optInt("sugar", 0)
            )
            Log.d("GeminiAPI", "Gemini nutrition parsed for $dishName: $nutrition")
            nutrition
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error generating nutrition for $dishName", e)
            null
        }
    }

    private fun extractFirstJsonObject(raw: String): String {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start < 0 || end < 0 || end <= start) return raw
        return raw.substring(start, end + 1)
    }

    private fun parseIntFromAny(value: Any?): Int {
        return when (value) {
            is Int -> value
            is Number -> value.toInt()
            is String -> Regex("\\d+").find(value)?.value?.toIntOrNull() ?: 0
            else -> 0
        }
    }

    suspend fun fetchDishDetailByName(dishName: String): Dish? {
        dishDetailCache[dishName]?.let {
            Log.d("GeminiAPI", "Lấy chi tiết món từ cache: $dishName")
            return it
        }
        Log.d("GeminiAPI", "Gọi AI để lấy chi tiết món: $dishName")
        return try {
            val prompt = "Hãy mô tả chi tiết món '$dishName' theo JSON: {\"name\": tên, \"description\": mô tả ngắn, \"ingredients\": [{\"name\": tên, \"amount\": số lượng}], \"steps\": [danh sách bước], \"nutrition\": {\"calories\": số, \"protein\": số, \"carbs\": số, \"fat\": số, \"fiber\": số, \"sugar\": số}}. Không trả về gì ngoài JSON."
            Log.d("GeminiAPI", "Gemini detail prompt: $prompt")
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            val raw = response.text?.replace("```json", "")?.replace("```", "")?.trim() ?: return null
            Log.d("GeminiAPI", "Gemini detail raw response for $dishName: $raw")
            val jsonBlock = extractFirstJsonObject(raw)
            val obj = try { JSONObject(jsonBlock) } catch (e: Exception) {
                Log.e("GeminiAPI", "JSON parse error: $jsonBlock", e)
                return null
            }
            val nutritionObj = obj.getJSONObject("nutrition")
            val nutrition = Nutrition(
                calories = parseIntFromAny(nutritionObj.opt("calories")),
                protein = parseIntFromAny(nutritionObj.opt("protein")),
                carbs = parseIntFromAny(nutritionObj.opt("carbs")),
                fat = parseIntFromAny(nutritionObj.opt("fat")),
                fiber = parseIntFromAny(nutritionObj.opt("fiber")),
                sugar = parseIntFromAny(nutritionObj.opt("sugar"))
            )
            val ingredientsArr = obj.getJSONArray("ingredients")
            val ingredients = mutableListOf<Pair<String, String>>()
            for (i in 0 until ingredientsArr.length()) {
                try {
                    val ing = ingredientsArr.getJSONObject(i)
                    ingredients.add(ing.optString("name", "") to ing.optString("amount", ""))
                } catch (_: Exception) {
                    // skip lỗi ingredient
                }
            }
            val stepsArr = obj.getJSONArray("steps")
            val steps = List(stepsArr.length()) { i -> stepsArr.getString(i) }
            val dish = com.example.planeatai.ui.model.Dish(
                id = dishName,
                name = obj.optString("name", dishName),
                imageUrl = null,
                description = obj.optString("description", ""),
                prepTime = 0,
                cookTime = 0,
                nutrition = nutrition,
                ingredients = ingredients,
                steps = steps
            )
            Log.d("GeminiAPI", "Gemini detail parsed for $dishName: $dish")
            dishDetailCache[dishName] = dish
            dish
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error fetching detail for $dishName", e)
            null
        }
    }

    fun openSavedMenu() {
        // TODO: Hiện dialog chọn thực đơn đã lưu hoặc gọi loadMealPlan()
        loadMealPlan()
    }

    private fun isValidJsonArray(json: String): Boolean {
        return try {
            JSONArray(json)
            true
        } catch (e: Exception) {
            false
        }
    }
} 