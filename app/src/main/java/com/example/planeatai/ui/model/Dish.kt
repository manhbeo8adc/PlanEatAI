package com.example.planeatai.ui.model

import com.example.planeatai.ui.model.Nutrition

data class Dish(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val description: String,
    val prepTime: Int,
    val cookTime: Int,
    val nutrition: Nutrition,
    val ingredients: List<Pair<String, String>>,
    val steps: List<String>
) 