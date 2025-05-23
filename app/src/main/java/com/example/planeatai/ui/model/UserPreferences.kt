package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class MealPreferences(
    val prepTime: Int = 30, // thời gian chuẩn bị (phút)
    val calories: Int = 500, // calo mong muốn
    val budget: Int = 50000 // ngân sách (VND)
)

@Serializable
data class UserPreferences(
    val favoriteFood: String = "",
    val dislikedFood: String = "",
    val breakfastPrefs: MealPreferences = MealPreferences(15, 400, 30000),
    val lunchPrefs: MealPreferences = MealPreferences(45, 600, 60000),
    val dinnerPrefs: MealPreferences = MealPreferences(60, 500, 80000),
    val cuisineStyles: List<String> = listOf("Việt Nam"),
    val servings: Int = 2,
    val additionalRequests: String = ""
)