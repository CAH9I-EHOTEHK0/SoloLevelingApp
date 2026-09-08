package ua.zxcode.sololevelingapp.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class CyberShape(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val shearX: Float,
    val speed: Float,
    val alphaMax: Float
)

@Composable
fun SoloLevelingBackground(modifier: Modifier = Modifier) {
    val shapes = remember {
        List(150) {
            CyberShape(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                width = Random.nextFloat() * 100f + 80f,
                height = Random.nextFloat() * 50f + 30f,
                shearX = Random.nextFloat() * 0.4f - 0.2f,
                speed = Random.nextFloat() * 0.0001f + 0.0005f,
                alphaMax = Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shapesTransition")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.fillMaxSize().graphicsLayer(rotationZ = 45f, scaleX = 1.5f, scaleY = 1.5f)) {
        val strokeWidth = 2.dp.toPx()
        val neonColor = Color(0xFFB0E0E6)

        val triggerAnimationFrame = progress

        shapes.forEach { shape ->
            shape.x -= shape.speed * 0.05f
            shape.y -= shape.speed

            if (shape.y < -0.1f) {
                shape.y = 1.1f
                shape.x = Random.nextFloat()
            }
            if (shape.x < -0.1f) shape.x = 1.1f

            val canvasX = shape.x * size.width
            val canvasY = shape.y * size.height

            withTransform({
                translate(left = canvasX, top = canvasY)
            }) {
                val depth = shape.width * 0.5f
                val perspectiveShift = 0.3f

                val p0 = Offset(0f, 0f)
                val p1 = Offset(shape.width, 0f)
                val p2 = Offset(shape.width + (shape.shearX * shape.height), shape.height)
                val p3 = Offset(shape.shearX * shape.height, shape.height)

                val p4 = Offset(depth * perspectiveShift, depth * perspectiveShift)
                val p5 = Offset(shape.width + depth * perspectiveShift, depth * perspectiveShift)
                val p6 = Offset(shape.width + (shape.shearX * shape.height) + depth * perspectiveShift, shape.height + depth * perspectiveShift)
                val p7 = Offset(shape.shearX * shape.height + depth * perspectiveShift, shape.height + depth * perspectiveShift)

                val edges = listOf(
                    p0 to p1,
                    p1 to p2,
                    p2 to p3,
                    p3 to p0,
                    p4 to p5,
                    p5 to p6,
                    p6 to p7,
                    p7 to p4,
                    p0 to p4,
                    p1 to p5,
                    p2 to p6,
                    p3 to p7
                )

                edges.forEach { (start, end) ->
                    drawLine(
                        color = neonColor.copy(alpha = shape.alphaMax),
                        start = start,
                        end = end,
                        strokeWidth = strokeWidth
                    )
                }
            }
        }
    }
}