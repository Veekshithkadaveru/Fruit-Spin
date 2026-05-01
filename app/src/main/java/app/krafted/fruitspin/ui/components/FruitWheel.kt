package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.theme.*
import app.krafted.fruitspin.viewmodel.Fruit
import app.krafted.fruitspin.viewmodel.TapFeedback
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FruitWheel(
    rotationAngle: Float,
    tapFeedback: TapFeedback?,
    targetFruit: Fruit?,
    modifier: Modifier = Modifier
) {
    val fruits = Fruit.values()
    val segmentAngle = 360f / fruits.size

    val imageBitmaps = fruits.associateWith { fruit ->
        ImageBitmap.imageResource(id = fruit.drawableRes)
    }

    val colors = fruits.map { it.glowColor }

    val (borderColor, shadowColor) = when (tapFeedback) {
        TapFeedback.CORRECT -> Pair(EmeraldGreen, GlowGreen)
        TapFeedback.WRONG -> Pair(RubyRed, GlowRed)
        TapFeedback.JACKPOT -> Pair(MetallicGold, GlowGold)
        else -> Pair(MetallicGold, GlowGold)
    }

    val borderWidth = if (tapFeedback != null && tapFeedback != TapFeedback.NONE) 8.dp else 5.dp

    val infiniteTransition = rememberInfiniteTransition(label = "fruit_pulse")
    val targetSizePulse by infiniteTransition.animateFloat(
        initialValue = 0.26f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_size_pulse"
    )

    val flashAnim = remember { Animatable(0f) }
    LaunchedEffect(tapFeedback) {
        if (tapFeedback != null && tapFeedback != TapFeedback.NONE) {
            flashAnim.animateTo(0.5f, tween(100))
            flashAnim.animateTo(0f, tween(200))
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 24.dp,
                spotColor = shadowColor.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .border(
                width = borderWidth,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        borderColor,
                        MetallicGoldLight,
                        borderColor,
                        MetallicGoldLight,
                        borderColor
                    )
                ),
                shape = CircleShape
            )
            .drawWithContent {
                drawContent()

                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            shadowColor.copy(alpha = 0.4f),
                            shadowColor.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.1f
                    ),
                    radius = radius * 1.1f,
                    center = center
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        MetallicGold.copy(alpha = 0.3f),
                        MetallicGoldLight.copy(alpha = 0.5f),
                        MetallicGold.copy(alpha = 0.3f),
                        MetallicGoldLight.copy(alpha = 0.5f),
                        MetallicGold.copy(alpha = 0.3f)
                    ),
                    center = center
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            rotate(degrees = rotationAngle, pivot = center) {
                fruits.forEachIndexed { index, fruit ->
                    val startAngle = index * segmentAngle
                    val sweepAngle = segmentAngle

                    val segmentCenterAngle =
                        Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val gradCenterX = center.x + (radius * 0.4f) * cos(segmentCenterAngle).toFloat()
                    val gradCenterY = center.y + (radius * 0.4f) * sin(segmentCenterAngle).toFloat()

                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                lightenColor(colors[index], 0.35f),
                                colors[index],
                                darkenColor(colors[index], 0.2f)
                            ),
                            center = Offset(gradCenterX, gradCenterY),
                            radius = radius
                        ),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.3f)
                            ),
                            start = Offset(center.x - radius, center.y - radius),
                            end = Offset(center.x + radius, center.y + radius)
                        ),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        style = Stroke(width = 3.dp.toPx()),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    val imageBitmap = imageBitmaps[fruit]
                    if (imageBitmap != null) {
                        val angleInRadians =
                            Math.toRadians((startAngle + segmentAngle / 2).toDouble())
                        val imageRadius = radius * 0.55f

                        val imageX = center.x + imageRadius * cos(angleInRadians).toFloat()
                        val imageY = center.y + imageRadius * sin(angleInRadians).toFloat()

                        val isTarget = fruit == targetFruit
                        // Size is always relative to wheel radius — immune to bitmap resolution
                        val drawSize = radius * (if (isTarget) targetSizePulse else 0.33f)
                        val half = drawSize / 2f

                        rotate(
                            degrees = startAngle + segmentAngle / 2 + 90f,
                            pivot = Offset(imageX, imageY)
                        ) {
                            // Glow halo sized to match draw area
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        fruit.glowColor.copy(alpha = if (isTarget) 0.7f else 0.4f),
                                        Color.Transparent
                                    ),
                                    center = Offset(imageX, imageY),
                                    radius = drawSize * 0.8f
                                ),
                                radius = drawSize * 0.8f,
                                center = Offset(imageX, imageY)
                            )

                            drawImage(
                                image = imageBitmap,
                                dstOffset = IntOffset(
                                    (imageX - half).toInt(),
                                    (imageY - half).toInt()
                                ),
                                dstSize = IntSize(
                                    drawSize.toInt(),
                                    drawSize.toInt()
                                )
                            )
                        }
                    }
                }

                val hubRadius = radius * 0.15f

                drawCircle(
                    color = Color.Black.copy(alpha = 0.3f),
                    radius = hubRadius + 4.dp.toPx(),
                    center = Offset(center.x + 2.dp.toPx(), center.y + 2.dp.toPx())
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MetallicGoldShine,
                            MetallicGoldLight,
                            MetallicGold,
                            MetallicGoldDark
                        ),
                        center = Offset(center.x - hubRadius * 0.3f, center.y - hubRadius * 0.3f),
                        radius = hubRadius * 1.2f
                    ),
                    radius = hubRadius,
                    center = center
                )

                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            MetallicGoldDark,
                            MetallicGold,
                            MetallicGoldLight,
                            MetallicGold,
                            MetallicGoldDark
                        ),
                        center = center
                    ),
                    radius = hubRadius,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            RubyRedDark,
                            GlossyRedDark
                        ),
                        center = center,
                        radius = hubRadius * 0.5f
                    ),
                    radius = hubRadius * 0.5f,
                    center = center
                )

                drawCircle(
                    color = MetallicGoldShine,
                    radius = hubRadius * 0.15f,
                    center = center
                )

                if (flashAnim.value > 0f) {
                    drawCircle(
                        color = when (tapFeedback) {
                            TapFeedback.CORRECT -> EmeraldGreen
                            TapFeedback.WRONG -> RubyRed
                            TapFeedback.JACKPOT -> MetallicGold
                            else -> Color.Transparent
                        }.copy(alpha = flashAnim.value),
                        radius = radius,
                        center = center
                    )
                }
            }
        }
    }
}

private fun lightenColor(color: Color, factor: Float): Color {
    return Color(
        red = (color.red + factor).coerceIn(0f, 1f),
        green = (color.green + factor).coerceIn(0f, 1f),
        blue = (color.blue + factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

private fun darkenColor(color: Color, factor: Float): Color {
    return Color(
        red = (color.red - factor).coerceIn(0f, 1f),
        green = (color.green - factor).coerceIn(0f, 1f),
        blue = (color.blue - factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}