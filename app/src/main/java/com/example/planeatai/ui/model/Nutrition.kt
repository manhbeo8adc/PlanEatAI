package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class Nutrition(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float,
    val sugar: Float
)