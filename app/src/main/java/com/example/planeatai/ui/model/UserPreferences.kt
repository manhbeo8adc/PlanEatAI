package com.example.planeatai.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val goals: String = "",
    val preferences: String = "",
    val additionalRequests: String = ""
)