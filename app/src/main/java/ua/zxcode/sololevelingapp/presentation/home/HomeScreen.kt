package ua.zxcode.sololevelingapp.presentation.home

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    fun showMessage() {
        Toast.makeText(context, "Поки що повідомлення", Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF141C2B), Color(0xFF120A1F))
                )
            )
    ) {
        SoloLevelingBackground()

        // Header притиснутий до топу
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            val btnSize = 58.dp
            val barHeight = 58.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .padding(horizontal = 16.dp), // мінімальний відступ від країв
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // lvlbar займає весь простір
                Box(modifier = Modifier.weight(1.3f).height(barHeight*1.3f)) {
                    Image(
                        painter = painterResource(id = R.drawable.lvlbar),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.DECAL)
                                        .asComposeRenderEffect()
                                } else null
                            },
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(
                            Color(0xFF00E6F0),
                            blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                        )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.lvlbar),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                // profilebtn — строго 44×44
                Box(
                    modifier = Modifier
                        .size(btnSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showMessage() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profilebtn),
                        contentDescription = null,
                        modifier = Modifier
                            .size(btnSize)
                            .graphicsLayer {
                                renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    RenderEffect.createBlurEffect(14f, 14f, Shader.TileMode.DECAL)
                                        .asComposeRenderEffect()
                                } else null
                            },
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(
                            Color(0xFF00E6F0),
                            blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                        )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.profilebtn),
                        contentDescription = null,
                        modifier = Modifier.size(btnSize),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }
}