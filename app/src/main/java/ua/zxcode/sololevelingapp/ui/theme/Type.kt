package ua.zxcode.sololevelingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ua.zxcode.sololevelingapp.R

val CloisterFont = FontFamily(
    Font(R.font.cloister_a, FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = CloisterFont),
    displayMedium = TextStyle(fontFamily = CloisterFont),
    displaySmall = TextStyle(fontFamily = CloisterFont),
    
    headlineLarge = TextStyle(fontFamily = CloisterFont),
    headlineMedium = TextStyle(fontFamily = CloisterFont),
    headlineSmall = TextStyle(fontFamily = CloisterFont),
    
    titleLarge = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(fontFamily = CloisterFont),
    titleSmall = TextStyle(fontFamily = CloisterFont),
    
    bodyLarge = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(fontFamily = CloisterFont),
    
    labelLarge = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(fontFamily = CloisterFont)
)