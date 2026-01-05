package com.example.lumos.data

import androidx.compose.ui.graphics.Color

data class LightBulb(
    val id: String,
    val name: String,
    var color: Color,
    var sliderPosition: Float,
    var isLedOn: Boolean
)