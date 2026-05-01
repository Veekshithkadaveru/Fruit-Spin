package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.animations.BouncySpring
import app.krafted.fruitspin.ui.animations.QuickSpring
import app.krafted.fruitspin.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedHeart(
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    animateBeat: Boolean = true,
    isBreaking: Boolean = false,
    size: androidx.compose.ui.unit.Dp = 32.dp
) {
    val beatTransition = rememberInfiniteTransition(label = "heartbeat")
    val beatScale = if (animateBeat && filled && !isBreaking) {
        beatTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 800
                    1f at 0
                    1.05f at 150
                    1f at 300
                    1.08f at 450
                    1f at 800
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "heartbeat"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    var breakProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isBreaking) {
        if (isBreaking) {
            breakProgress = 0f
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(400, easing = EaseOutCubic)
            ) { value, _ ->
                breakProgress = value
            }
        } else {
            breakProgress = 0f
        }
    }

    val shakeOffset = if (isBreaking) {
        val shake = rememberInfiniteTransition(label = "shake")
        shake.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shake_anim"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val scale = if (isBreaking) 1f - (breakProgress * 0.3f) else beatScale.value
    val alpha = if (isBreaking) 1f - (breakProgress * 0.5f) else 1f

    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = shakeOffset.value
                this.alpha = alpha
            }
    ) {
        val heartSize = size.toPx() * 0.85f
        val centerX = size.toPx() / 2
        val centerY = size.toPx() / 2

        if (isBreaking && breakProgress > 0.3f) {
            drawBrokenHeart(centerX, centerY, heartSize, breakProgress, filled)
        } else {
            drawHeart(
                centerX = centerX,
                centerY = centerY,
                size = heartSize,
                filled = filled,
                fillBrush = if (filled) {
                    Brush.verticalGradient(
                        colors = listOf(RubyRedLight, RubyRed, RubyRedDark),
                        startY = centerY - heartSize / 2,
                        endY = centerY + heartSize / 2
                    )
                } else null,
                strokeColor = if (filled) RubyRedDark else Platinum,
                glowColor = if (filled) GlowRed else Color.Transparent
            )
        }
    }
}

private fun DrawScope.drawHeart(
    centerX: Float,
    centerY: Float,
    size: Float,
    filled: Boolean,
    fillBrush: Brush?,
    strokeColor: Color,
    glowColor: Color
) {
    val path = Path().apply {
        val halfSize = size / 2
        val topOffset = centerY - halfSize * 0.3f

        moveTo(centerX, centerY + halfSize)

        cubicTo(
            centerX - halfSize, centerY + halfSize * 0.5f,
            centerX - halfSize, centerY - halfSize * 0.3f,
            centerX - halfSize * 0.5f, topOffset
        )

        cubicTo(
            centerX - halfSize * 0.2f, centerY - halfSize * 0.8f,
            centerX, centerY - halfSize * 0.5f,
            centerX, centerY - halfSize * 0.5f
        )

        cubicTo(
            centerX, centerY - halfSize * 0.5f,
            centerX + halfSize * 0.2f, centerY - halfSize * 0.8f,
            centerX + halfSize * 0.5f, topOffset
        )
        cubicTo(
            centerX + halfSize, centerY - halfSize * 0.3f,
            centerX + halfSize, centerY + halfSize * 0.5f,
            centerX, centerY + halfSize
        )

        close()
    }

    if (glowColor != Color.Transparent) {
        drawPath(
            path = path,
            color = glowColor,
            style = Stroke(width = 8f)
        )
    }

    if (filled && fillBrush != null) {
        drawPath(path = path, brush = fillBrush)

        val shinePath = Path().apply {
            val halfSize = size / 2
            val shineSize = halfSize * 0.3f
            val shineCenterX = centerX - halfSize * 0.25f
            val shineCenterY = centerY - halfSize * 0.25f

            moveTo(shineCenterX, shineCenterY + shineSize)
            quadraticBezierTo(
                shineCenterX - shineSize, shineCenterY,
                shineCenterX, shineCenterY - shineSize * 0.5f
            )
            quadraticBezierTo(
                shineCenterX + shineSize * 0.5f, shineCenterY,
                shineCenterX + shineSize * 0.3f, shineCenterY + shineSize * 0.8f
            )
            close()
        }

        drawPath(
            path = shinePath,
            color = Color.White.copy(alpha = 0.4f)
        )
    }

    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = if (filled) 2f else 3f)
    )
}

private fun DrawScope.drawBrokenHeart(
    centerX: Float,
    centerY: Float,
    size: Float,
    breakProgress: Float,
    wasFilled: Boolean
) {
    val halfSize = size / 2
    val crackOffset = breakProgress * 10f
    val pieceOffset = breakProgress * 8f

    val leftPath = Path().apply {
        moveTo(centerX, centerY + halfSize - pieceOffset)
        cubicTo(
            centerX - halfSize + crackOffset, centerY + halfSize * 0.5f - pieceOffset,
            centerX - halfSize + crackOffset, centerY - halfSize * 0.3f,
            centerX - halfSize * 0.5f - pieceOffset, centerY - halfSize * 0.3f
        )

        lineTo(centerX - crackOffset, centerY)
        lineTo(centerX - crackOffset * 1.5f, centerY + halfSize * 0.5f)

        close()
    }

    val rightPath = Path().apply {
        moveTo(centerX, centerY + halfSize + pieceOffset)

        lineTo(centerX + crackOffset * 1.5f, centerY + halfSize * 0.5f)
        lineTo(centerX + crackOffset, centerY)

        lineTo(centerX + halfSize * 0.5f + pieceOffset, centerY - halfSize * 0.3f)
        cubicTo(
            centerX + halfSize - crackOffset, centerY - halfSize * 0.3f,
            centerX + halfSize - crackOffset, centerY + halfSize * 0.5f + pieceOffset,
            centerX, centerY + halfSize + pieceOffset
        )

        close()
    }

    val fillBrush = if (wasFilled) {
        Brush.verticalGradient(
            colors = listOf(
                RubyRedLight.copy(alpha = 0.8f),
                RubyRed.copy(alpha = 0.8f),
                RubyRedDark.copy(alpha = 0.8f)
            )
        )
    } else null

    if (fillBrush != null) {
        drawPath(path = leftPath, brush = fillBrush)
        drawPath(path = rightPath, brush = fillBrush)
    }

    drawPath(path = leftPath, color = RubyRedDark, style = Stroke(width = 2f))
    drawPath(path = rightPath, color = RubyRedDark, style = Stroke(width = 2f))

    drawLine(
        color = Color.Black.copy(alpha = 0.6f),
        start = Offset(centerX - crackOffset * 1.5f, centerY + halfSize * 0.5f),
        end = Offset(centerX + crackOffset * 1.5f, centerY + halfSize * 0.5f),
        strokeWidth = 3f
    )
}

@Composable
fun LivesIndicator(
    lives: Int,
    maxLives: Int = 3,
    modifier: Modifier = Modifier,
    justLostLife: Boolean = false
) {
    var showBreakAnimation by remember { mutableStateOf(false) }
    var brokenHeartIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(justLostLife) {
        if (justLostLife && lives < maxLives) {
            brokenHeartIndex = lives
            showBreakAnimation = true
            delay(500)
            showBreakAnimation = false
            brokenHeartIndex = -1
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxLives) { index ->
            AnimatedHeart(
                filled = index < lives,
                isBreaking = showBreakAnimation && index == brokenHeartIndex,
                animateBeat = index < lives,
                size = 28.dp
            )
        }
    }
}

@Composable
fun HeartWithBurst(
    modifier: Modifier = Modifier,
    triggerBurst: Boolean = false
) {
    var showBurst by remember { mutableStateOf(false) }

    LaunchedEffect(triggerBurst) {
        if (triggerBurst) {
            showBurst = true
            delay(600)
            showBurst = false
        }
    }

    Box(modifier = modifier.size(40.dp)) {
        AnimatedHeart(
            filled = true,
            animateBeat = true,
            size = 40.dp
        )

        if (showBurst) {
            ParticleBurst(
                particleCount = 12,
                color = RubyRed,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

@Composable
private fun ParticleBurst(
    particleCount: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val particles = remember { List(particleCount) { index ->
        val angle = (index / particleCount.toFloat()) * 2 * PI
        ParticleData(
            angle = angle.toFloat(),
            speed = 2f + (index % 3) * 1.5f,
            size = 4f + (index % 4) * 2f
        )
    } }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = EaseOutCubic)
        )
    }

    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        particles.forEach { particle ->
            val distance = particle.speed * progress.value * 40f
            val x = centerX + cos(particle.angle) * distance
            val y = centerY + sin(particle.angle) * distance
            val alpha = 1f - progress.value
            val particleSize = particle.size * (1f - progress.value * 0.5f)

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = particleSize,
                center = Offset(x, y)
            )
        }
    }
}

private data class ParticleData(
    val angle: Float,
    val speed: Float,
    val size: Float
)