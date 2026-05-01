package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.animations.BouncySpring
import app.krafted.fruitspin.ui.theme.*
import app.krafted.fruitspin.viewmodel.TapFeedback

@Composable
fun PointerIndicator(modifier: Modifier = Modifier) {
    val pulseTransition = rememberInfiniteTransition(label = "pointer_pulse")
    val pulseScale = pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pointer_scale"
    )

    val glowAlpha = pulseTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pointer_glow"
    )

    Canvas(
        modifier = modifier
            .size(40.dp)
            .graphicsLayer {
                scaleX = pulseScale.value
                scaleY = pulseScale.value
            }
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val topY = 0f
        val bottomY = height * 0.6f
        val leftX = centerX - width * 0.25f
        val rightX = centerX + width * 0.25f

        drawCircle(
            color = NeonGold.copy(alpha = glowAlpha.value * 0.5f),
            radius = width * 0.6f,
            center = Offset(centerX, bottomY * 0.6f)
        )

        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(NeonGold, MetallicGold),
                startY = topY,
                endY = bottomY
            ),
            start = Offset(centerX, topY),
            end = Offset(centerX, bottomY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(NeonGold, MetallicGold),
                start = Offset(leftX, bottomY * 0.5f),
                end = Offset(centerX, bottomY)
            ),
            start = Offset(leftX, bottomY * 0.5f),
            end = Offset(centerX, bottomY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(NeonGold, MetallicGold),
                start = Offset(rightX, bottomY * 0.5f),
                end = Offset(centerX, bottomY)
            ),
            start = Offset(rightX, bottomY * 0.5f),
            end = Offset(centerX, bottomY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TapButton(
    onTap: () -> Unit,
    tapFeedback: TapFeedback,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleAnim = animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = BouncySpring,
        label = "tap_button_scale"
    )

    val (buttonBrush, borderColor, shadowColor, innerGlowColor) = when (tapFeedback) {
        TapFeedback.CORRECT -> Quadruple(
            Brush.verticalGradient(listOf(EmeraldGreen, EmeraldGreenDark, Color(0xFF1A5F3C))),
            EmeraldGreenLight,
            Color(0x4050C878),
            EmeraldGreenLight
        )
        TapFeedback.WRONG -> Quadruple(
            Brush.verticalGradient(listOf(RubyRed, RubyRedDark, Color(0xFF6B0830))),
            RubyRedLight,
            Color(0x40E0115F),
            RubyRedLight
        )
        TapFeedback.JACKPOT -> Quadruple(
            Brush.verticalGradient(listOf(MetallicGold, MetallicGoldDark, MetallicGoldDark)),
            MetallicGoldLight,
            Color(0x60FFD700),
            MetallicGoldShine
        )
        else -> Quadruple(
            Brush.verticalGradient(listOf(GlossyRedLight, GlossyRedMid, GlossyRedDark)),
            MetallicGold,
            Color(0x40CC0000),
            MetallicGold.copy(alpha = 0.5f)
        )
    }

    val shimmerTransition = rememberInfiniteTransition(label = "tap_shimmer")
    val shimmerTranslate = shimmerTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_sweep"
    )

    val feedbackScale = remember { Animatable(1f) }
    LaunchedEffect(tapFeedback) {
        if (tapFeedback != TapFeedback.NONE) {
            feedbackScale.animateTo(1.1f, tween(100))
            feedbackScale.animateTo(1f, BouncySpring)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(
                    elevation = if (isPressed) 4.dp else 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = shadowColor
                )
                .graphicsLayer {
                    scaleX = scaleAnim.value * feedbackScale.value
                    scaleY = scaleAnim.value * feedbackScale.value
                }
                .clip(RoundedCornerShape(20.dp))
                .background(buttonBrush)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(borderColor, MetallicGold, borderColor)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onTap
                )
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.5f
                        ),
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.5f)
                    )
                    val shimmerWidth = 100f
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                ShimmerStart.copy(alpha = 0.3f),
                                ShimmerMid.copy(alpha = 0.5f),
                                ShimmerStart.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerTranslate.value - shimmerWidth, 0f),
                            end = Offset(shimmerTranslate.value + shimmerWidth, size.height)
                        ),
                        blendMode = BlendMode.Overlay
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                innerGlowColor.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height * 0.7f),
                            radius = size.width * 0.4f
                        ),
                        blendMode = BlendMode.Overlay
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TAP!",
                style = ButtonTextStyle.copy(
                    fontSize = ButtonTextStyle.fontSize * 1.2,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(0f, 3f),
                        blurRadius = 6f
                    )
                ),
                color = when (tapFeedback) {
                    TapFeedback.CORRECT -> EmeraldGreenLight
                    TapFeedback.WRONG -> RubyRedLight
                    TapFeedback.JACKPOT -> MetallicGoldShine
                    else -> MetallicGold
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)