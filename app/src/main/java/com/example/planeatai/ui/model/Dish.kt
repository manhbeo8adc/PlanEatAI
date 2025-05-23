package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class Dish(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val description: String,
    val prepTime: Int,
    val cookTime: Int,
    val nutrition: Nutrition,
    val ingredients: List<Ingredient>,
    val steps: List<String>
)