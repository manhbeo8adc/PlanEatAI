package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val name: String,
    val amount: String,
    val calories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val sugar: Float = 0f
)