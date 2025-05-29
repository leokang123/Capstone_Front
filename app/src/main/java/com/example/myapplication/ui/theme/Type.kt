package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.myapplication.R

// Set of Material typography styles to start with
val default = FontFamily(
    Font(R.font.text)
)
val AppTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = default),
        displayMedium = displayMedium.copy(fontFamily = default),
        displaySmall = displaySmall.copy(fontFamily = default),
        headlineLarge = headlineLarge.copy(fontFamily = default),
        headlineMedium = headlineMedium.copy(fontFamily = default),
        headlineSmall = headlineSmall.copy(fontFamily = default),
        titleLarge = titleLarge.copy(fontFamily = default),
        titleMedium = titleMedium.copy(fontFamily = default),
        titleSmall = titleSmall.copy(fontFamily = default),
        bodyLarge = bodyLarge.copy(fontFamily = default),
        bodyMedium = bodyMedium.copy(fontFamily = default),
        bodySmall = bodySmall.copy(fontFamily = default),
        labelLarge = labelLarge.copy(fontFamily = default),
        labelMedium = labelMedium.copy(fontFamily = default),
        labelSmall = labelSmall.copy(fontFamily = default)
    )
}