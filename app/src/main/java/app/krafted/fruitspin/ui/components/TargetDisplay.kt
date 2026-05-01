package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.fruitspin.ui.animations.BouncySpring
import app.krafted.fruitspin.ui.animations.pulseScale
import app.krafted.fruitspin.ui.theme.*
import app.krafted.fruitspin.viewmodel.Fruit

@Composable
fun TargetDisplay(
    targetFruit: Fruit,
    correctTapsForCurrentTarget: Int,
    isFlipping: Boolean,
    modifier: Modifier = Modifier
) {
    val tapsRemaining = (5 - correctTapsForCurrentTarget).coerceAtLeast(0)
    val progress = correctTapsForCurrentTarget / 5f
    val points = if (targetFruit == Fruit.LUCKY_7) targetFruit.basePoints * 3 else targetFruit.basePoints
    val isJackpot = targetFruit == Fruit.LUCKY_7

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipping) 90f else 0f,
        animationSpec = tween(durationMillis = 250, easing = EaseInOutCubic),
        label = "target_flip_y"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFlipping) 0.8f else 1f,
        animationSpec = BouncySpring,
        label = "target_scale"
    )

    val colorTransition = rememberInfiniteTransition(label = "border_color")
    val hueShift by colorTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isJackpot) 60f else 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_shift"
    )

    val baseColor = if (isJackpot) MetallicGold else targetFruit.glowColor
    val animatedBorderColor = remember(hueShift, baseColor) {
        shiftHue(baseColor, hueShift)
    }

    val glowTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val showTrophy by remember(correctTapsForCurrentTarget) {
        derivedStateOf { correctTapsForCurrentTarget >= 4 }
    }

    val trophyScale by animateFloatAsState(
        targetValue = if (showTrophy) 1f else 0f,
        animationSpec = spring(stiffness = 400f, dampingRatio = 0.5f),
        label = "trophy_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                baseColor.copy(alpha = glowAlpha * 0.5f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width * 0.6f
                        )
                    )
                }
                .shadow(
                    elevation = 16.dp,
                    spotColor = baseColor.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            animatedBorderColor,
                            MetallicGoldLight,
                            animatedBorderColor
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(GlassBackground.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    this.rotationY = rotationY
                    this.cameraDistance = 12f * density
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = baseColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )

                        Text(
                            text = "TARGET FRUIT",
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            color = NeonOrange,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = targetFruit.displayName.uppercase(),
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            shadow = ScoreGlow
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = baseColor,
                        trackColor = Color.DarkGray.copy(alpha = 0.5f),
                        drawStopIndicator = {}
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hit ",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = Platinum.copy(alpha = 0.7f)
                        )

                        AnimatedCounter(
                            value = tapsRemaining,
                            style = CounterTextStyle.copy(
                                fontSize = CounterTextStyle.fontSize * 0.4
                            ),
                            glowColor = baseColor
                        )

                        Text(
                            text = " more · +${points}pts",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = Platinum.copy(alpha = 0.7f)
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            baseColor.copy(alpha = 0.6f),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = size.width * 0.6f,
                                    center = center
                                )
                            }
                    )

                    Image(
                        painter = painterResource(id = targetFruit.drawableRes),
                        contentDescription = targetFruit.displayName,
                        modifier = Modifier
                            .size(72.dp)
                            .then(if (isJackpot) Modifier.pulseScale(0.9f, 1.1f, 800) else Modifier)
                    )

                    if (isJackpot) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-4).dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(MetallicGold, MetallicGoldLight)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.Black.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "3×",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (trophyScale > 0f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .graphicsLayer {
                                    scaleX = trophyScale
                                    scaleY = trophyScale
                                }
                                .background(
                                    color = MetallicGold.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
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
fun CompactTargetDisplay(
    targetFruit: Fruit,
    modifier: Modifier = Modifier
) {
    val baseColor = targetFruit.glowColor

    Row(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                spotColor = baseColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = baseColor.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(GlassBackground.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = targetFruit.drawableRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )

        Column {
            Text(
                text = "TARGET",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = NeonOrange,
                letterSpacing = 1.sp
            )
            Text(
                text = targetFruit.displayName.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
    }
}