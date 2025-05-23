package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class Meal(
    val name: String,
    val description: String,
    val nutrition: Nutrition
)