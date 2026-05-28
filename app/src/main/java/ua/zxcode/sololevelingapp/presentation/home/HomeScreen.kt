package ua.zxcode.sololevelingapp.presentation.home

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground

@Composable
fun HomeScreen() {
    // Контекст і допоміжна функція для показу повідомлення під час натискання
    val context = LocalContext.current
    fun showMessage() {
        Toast.makeText(context, "Поки що повідомлення", Toast.LENGTH_SHORT).show()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141C2B),
                        Color(0xFF120A1F),
                    )
                )
            ), // Градієнтний фон
        contentAlignment = Alignment.Center
    ) {
        // Наш кастомний анімований фон з паралелепіпедами
        SoloLevelingBackground()

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {


            Row(
                modifier = Modifier
                    .width(384.dp) // Або .fillMaxWidth(), якщо блок має тягнутися на всю ширину
                    .height(100.dp), // height: 100px;
                    //.padding(vertical = 28.dp), // padding: 28px 0;
                // justify-content: center; та gap: 10px;
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                // align-items: center;
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lvlbar),
                        contentDescription = null,
                        modifier = Modifier
                            .width(300.dp)
                            .height(46.66667.dp)
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

                    Image(
                        painter = painterResource(id = R.drawable.lvlbar),
                        contentDescription = null,
                        modifier = Modifier
                            .width(300.dp)
                            .height(46.66667.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }
                Box(
                    modifier = Modifier.padding(top = 20.dp).clickable(
                        onClick = { showMessage() },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
//                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profilebtn),
                        contentDescription = null,
                        modifier = Modifier
                            .width(44.dp)
                            .height(44.dp)
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

                    Image(
                        painter = painterResource(id = R.drawable.profilebtn),
                        contentDescription = null,
                        modifier = Modifier
                            .width(44.dp)
                            .height(44.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }

        // Вміст екрана поверх фону
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "DAILY QUESTS",
//                color = Color(0xFFBB86FC),
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
}
