package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    neonColor: Color = NeonGold,
    backgroundAlpha: Float = 0.85f,
    cornerRadius: Dp = 20.dp,
    glowIntensity: Float = 1f,
    animatedBorder: Boolean = true,
    shimmerEnabled: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val borderColor by animateBorderColor(if (animatedBorder) neonColor else neonColor.copy(alpha = 0.5f))

    val glowTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha = glowTransition.animateFloat(
        initialValue = 0.3f * glowIntensity,
        targetValue = 0.6f * glowIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(GlassBackground.copy(alpha = backgroundAlpha))
            .border(
                width = 2.dp,
                color = borderColor.copy(alpha = 0.6f),
                shape = shape
            )
            .drawWithContent {
                drawContent()

                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        this.color = neonColor.copy(alpha = glowAlpha.value)
                        this.asFrameworkPaint().apply {
                            maskFilter = android.graphics.BlurMaskFilter(
                                25f,
                                android.graphics.BlurMaskFilter.Blur.OUTER
                            )
                        }
                    }
                    canvas.drawRect(0f, 0f, size.width, size.height, paint)
                }

                val cornerRadiusPx = cornerRadius.toPx()

                drawCircle(
                    color = neonColor.copy(alpha = 0.9f),
                    radius = 6f,
                    center = Offset(cornerRadiusPx * 0.5f, cornerRadiusPx * 0.5f)
                )

                drawCircle(
                    color = neonColor.copy(alpha = 0.9f),
                    radius = 6f,
                    center = Offset(size.width - cornerRadiusPx * 0.5f, cornerRadiusPx * 0.5f)
                )
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                neonColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.3f),
                            radius = size.width * 0.8f
                        )
                    )
                }
        )

        if (shimmerEnabled) {
            ShimmerOverlay(neonColor, Modifier.matchParentSize())
        }

        Box(
            modifier = Modifier,
            content = content
        )
    }
}

@Composable
private fun animateBorderColor(baseColor: Color): State<Color> {
    val transition = rememberInfiniteTransition(label = "border_color")
    val hueShift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hue_shift"
    )

    return derivedStateOf {
        shiftHue(baseColor, hueShift.value)
    }
}

private fun shiftHue(color: Color, shiftDegrees: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        ),
        hsv
    )
    hsv[0] = (hsv[0] + shiftDegrees) % 360f
    val newColor = android.graphics.Color.HSVToColor((color.alpha * 255).toInt(), hsv)
    return Color(newColor)
}

@Composable
private fun ShimmerOverlay(neonColor: Color, modifier: Modifier = Modifier.fillMaxSize()) {
    val shimmerProgress = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerTranslate = shimmerProgress.animateFloat(
        initialValue = -300f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .drawWithContent {
                drawContent()
                val shimmerWidth = 150f
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            neonColor.copy(alpha = 0.1f),
                            neonColor.copy(alpha = 0.2f),
                            neonColor.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerTranslate.value - shimmerWidth, 0f),
                        end = Offset(shimmerTranslate.value + shimmerWidth, size.height)
                    ),
                    blendMode = BlendMode.Overlay
                )
            }
    )
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    neonColor: Color = NeonGold,
    icon: (@Composable () -> Unit)? = null
) {
    NeonCard(
        modifier = modifier,
        neonColor = neonColor,
        cornerRadius = 16.dp,
        glowIntensity = 0.8f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(24.dp)) {
                    icon()
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            androidx.compose.material3.Text(
                text = value,
                style = CounterTextStyle,
                color = Color.White
            )

            androidx.compose.material3.Text(
                text = label,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = Platinum
            )
        }
    }
}

@Composable
fun BadgeCard(
    title: String,
    modifier: Modifier = Modifier,
    badgeColor: Color = MetallicGold,
    icon: @Composable () -> Unit
) {
    NeonCard(
        modifier = modifier.size(80.dp),
        neonColor = badgeColor,
        cornerRadius = 24.dp,
        glowIntensity = 1.2f,
        animatedBorder = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()

            Spacer(modifier = Modifier.height(4.dp))

            androidx.compose.material3.Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoPanel(
    title: String,
    modifier: Modifier = Modifier,
    neonColor: Color = NeonOrange,
    content: @Composable ColumnScope.() -> Unit
) {
    NeonCard(
        modifier = modifier,
        neonColor = neonColor,
        cornerRadius = 16.dp,
        backgroundAlpha = 0.9f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            androidx.compose.material3.Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                color = neonColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(neonColor, Color.Transparent)
                        )
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}