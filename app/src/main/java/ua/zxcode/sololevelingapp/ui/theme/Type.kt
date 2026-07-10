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
    titleLarge = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CloisterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)