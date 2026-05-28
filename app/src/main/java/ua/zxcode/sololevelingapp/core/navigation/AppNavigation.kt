package ua.zxcode.sololevelingapp.core.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.presentation.achievements.AchievementsScreen
import ua.zxcode.sololevelingapp.presentation.home.HomeScreen
import ua.zxcode.sololevelingapp.presentation.stats.StatsScreen
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize() // БЕЗ padding(innerPadding)
        ) {
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Achievements.route) { AchievementsScreen() }
        }

        // Навбар поверх контенту, притиснутий до низу
        SoloLevelingBottomBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    Scaffold(
//        bottomBar = { SoloLevelingBottomBar(navController = navController) }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = Screen.Home.route,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Screen.Stats.route) { StatsScreen() }
//            composable(Screen.Home.route) { HomeScreen() }
//            composable(Screen.Achievements.route) { AchievementsScreen() }
//        }
//    }
//}

@Composable
fun SoloLevelingBottomBar(navController: NavController, modifier: Modifier = Modifier) {
    val items = listOf(Screen.Stats, Screen.Home, Screen.Achievements)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Прозорий контейнер, що тримає всю конструкцію навбару
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(bottom = 24.dp)
//            .background(Color.Black.copy(alpha = 0f))
        ,
        contentAlignment = Alignment.Center
    ) {
        // 1. Твій епічний неоновий фон із Figma
        Image(
            painter = painterResource(id = R.drawable.navbarframe),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 5.dp)
                .graphicsLayer {
                    renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.DECAL)
                            .asComposeRenderEffect()
                    } else null
                    colorFilter = ColorFilter.tint(Color(0xFF00E6F0)) // <- колір glow
                },
            contentScale = ContentScale.FillBounds,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                Color(0xFF00E6F0), // колір підсвітки
                blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
            )
        )

        // 2. Оригінал поверх (чіткий)
        Image(
            painter = painterResource(id = R.drawable.navbarframe),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 5.dp),
            contentScale = ContentScale.FillBounds
        )

        // 2. Чистий Row без вбудованого Material-лайна. Жодних сірих блоків чи овалів!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 86.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow-шар
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Icon(
                            painter = painterResource(id = screen.iconResId),
                            contentDescription = null,
                            tint = Color(0xFF00E6F0),
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    renderEffect = RenderEffect
                                        .createBlurEffect(
                                            if (isSelected) 14f else 4f,
                                            if (isSelected) 14f else 4f,
                                            Shader.TileMode.DECAL
                                        )
                                        .asComposeRenderEffect()
                                }
                        )
                    }

                    // Чіткий оригінал
                    Icon(
                        painter = painterResource(id = screen.iconResId),
                        contentDescription = screen.route,
                        tint = Color(0xFFB0E0E6),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

//
//fun Modifier.neonShadow(
//    color: Color = Color(0xFF00E6F0),
//    blurRadius: Dp = 4.4.dp
//) = this.drawBehind {
//    drawIntoCanvas { canvas ->
//        val paint = Paint().apply {
//            asFrameworkPaint().apply {
//                isAntiAlias = true
//                this.color = android.graphics.Color.TRANSPARENT
//                setShadowLayer(
//                    blurRadius.toPx(),
//                    0f, 0f,
//                    color.copy(alpha = 1f).toArgb()
//                )
//            }
//        }
//        canvas.drawRect(
//            left = 0f,
//            top = 0f,
//            right = size.width,
//            bottom = size.height,
//            paint = paint
//        )
//    }
//}

