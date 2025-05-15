package com.example.planeatai.ui.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MealPlan(
    val date: String,
    val breakfast: String,
    val lunch: String,
    val dinner: String
) 