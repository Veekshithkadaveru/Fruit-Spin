package app.krafted.fruitspin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.fruitspin.ui.theme.*

@Composable
fun SpeedIndicator(
    speedDps: Float,
    speedBurst: Boolean,
    modifier: Modifier = Modifier
) {
    val maxSpeed = 210f
    val progress = (speedDps / maxSpeed).coerceIn(0f, 1f)
    val isTurbo = progress > 0.85f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400, easing = EaseOutCubic),
        label = "speed_progress"
    )

    val turboTransition = rememberInfiniteTransition(label = "turbo_pulse")
    val turboGlow by turboTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "turbo_glow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isTurbo,
            enter = expandVertically(tween(200)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(RubyRed, NeonOrange, RubyRed)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MetallicGold,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "TURBO MODE",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    ),
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SPEED",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = NeonOrange,
                letterSpacing = 2.sp
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .shadow(
                        elevation = if (isTurbo) (turboGlow * 8).dp else 4.dp,
                        spotColor = if (isTurbo) RubyRed else MetallicGold,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF2A0000))
                    .border(
                        width = 1.dp,
                        color = if (isTurbo) RubyRed.copy(alpha = 0.5f) else MetallicGold.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(5.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .drawWithContent {
                            drawContent()
                            val gradientColors = when {
                                isTurbo -> listOf(RubyRed, NeonOrange, NeonYellow)
                                progress > 0.6f -> listOf(NeonOrange, MetallicGold, MetallicGoldLight)
                                progress > 0.3f -> listOf(MetallicGoldDark, MetallicGold)
                                else -> listOf(PlatinumDark, MetallicGoldDark)
                            }

                            drawRect(
                                brush = Brush.horizontalGradient(colors = gradientColors),
                                size = size
                            )

                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                ),
                                size = size.copy(height = size.height * 0.5f)
                            )

                            if (isTurbo) {
                                val particleAlpha = turboGlow * 0.8f
                                drawCircle(
                                    color = NeonOrange.copy(alpha = particleAlpha),
                                    radius = 4f,
                                    center = Offset(size.width - 8f, size.height / 2)
                                )
                            }
                        }
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight(0.6f)
                                .background(
                                    color = if (animatedProgress > (index + 1) / 5f)
                                        Color.White.copy(alpha = 0.5f)
                                    else
                                        Color.White.copy(alpha = 0.1f)
                                )
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AnimatedCounter(
                    value = speedDps.toInt(),
                    style = CounterTextStyle.copy(
                        fontSize = CounterTextStyle.fontSize * 0.35
                    ),
                    glowColor = if (isTurbo) NeonOrange else MetallicGold
                )

                Text(
                    text = "°/s",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = if (isTurbo) NeonOrange else Platinum
                )
            }
        }
    }
}

@Composable
fun SpeedBadge(
    speedDps: Float,
    modifier: Modifier = Modifier
) {
    val isHighSpeed = speedDps > 150f

    val glowTransition = rememberInfiniteTransition(label = "badge_glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_glow"
    )

    Row(
        modifier = modifier
            .shadow(
                elevation = if (isHighSpeed) 8.dp else 4.dp,
                spotColor = if (isHighSpeed) NeonOrange else MetallicGold,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (isHighSpeed) NeonOrange else MetallicGold,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = GlassBackground.copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            )
            .drawWithContent {
                drawContent()
                if (isHighSpeed) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonOrange.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.width * 0.6f
                        )
                    )
                }
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "⚡",
            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
            color = if (isHighSpeed) NeonOrange else MetallicGold
        )

        Text(
            text = "${speedDps.toInt()}°/s",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}