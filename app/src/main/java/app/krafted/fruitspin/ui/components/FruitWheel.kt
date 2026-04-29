package app.krafted.fruitspin.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.viewmodel.Fruit
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FruitWheel(
    rotationAngle: Float,
    modifier: Modifier = Modifier
) {
    val fruits = Fruit.values()
    val segmentAngle = 360f / fruits.size
    
    val imageBitmaps = fruits.associateWith { fruit ->
        ImageBitmap.imageResource(id = fruit.drawableRes)
    }

    val colors = listOf(
        Color(0xFF9C27B0), // Grapes
        Color(0xFFE91E63), // Strawberry
        Color(0xFFFF9800), // Orange
        Color(0xFFFFEB3B), // Banana
        Color(0xFF4CAF50), // Watermelon
        Color(0xFF673AB7), // Plum
        Color(0xFFFFD700)  // Lucky 7
    )

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            rotate(degrees = rotationAngle, pivot = center) {
                fruits.forEachIndexed { index, fruit ->
                    val startAngle = index * segmentAngle
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = segmentAngle,
                        useCenter = true,
                        size = Size(radius * 2, radius * 2)
                    )

                    val imageBitmap = imageBitmaps[fruit]
                    if (imageBitmap != null) {
                        val angleInRadians = Math.toRadians((startAngle + segmentAngle / 2).toDouble())
                        val imageRadius = radius * 0.7f
                        
                        val imageX = center.x + imageRadius * cos(angleInRadians).toFloat()
                        val imageY = center.y + imageRadius * sin(angleInRadians).toFloat()

                        translate(left = imageX - imageBitmap.width / 2, top = imageY - imageBitmap.height / 2) {
                            rotate(degrees = startAngle + segmentAngle / 2 + 90f, pivot = Offset(imageBitmap.width / 2f, imageBitmap.height / 2f)) {
                                drawImage(image = imageBitmap)
                            }
                        }
                    }
                }
            }
        }
    }
}
