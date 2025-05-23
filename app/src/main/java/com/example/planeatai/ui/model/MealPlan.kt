package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class MealPlan(
    val day: String,
    val breakfast: Meal,
    val lunch: Meal,
    val dinner: Meal
)