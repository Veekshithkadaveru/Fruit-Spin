package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.animations.BouncySpring
import app.krafted.fruitspin.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val id: Int,
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val gravity: Float,
    val drag: Float,
    val lifespan: Float
)

@Composable
fun ParticleExplosion(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    colors: List<Color> = listOf(MetallicGold, NeonOrange, MetallicGoldLight, NeonGold, Color.White),
    centerColor: Color = MetallicGold,
    trigger: Boolean = false,
    onComplete: () -> Unit = {},
    explosionForce: Float = 1f,
    durationMillis: Int = 1500
) {
    var hasTriggered by remember { mutableStateOf(false) }
    var shouldShow by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (trigger && !hasTriggered) {
            hasTriggered = true
            shouldShow = true
            delay(durationMillis.toLong())
            shouldShow = false
            onComplete()
        }
    }

    if (!shouldShow) return

    val particles = remember(particleCount, colors, explosionForce) {
        List(particleCount) { index ->
            val angle = (index / particleCount.toFloat()) * 2 * PI.toFloat() + Random.nextFloat() * 0.5f - 0.25f
            Particle(
                id = index,
                angle = angle,
                speed = 5f + Random.nextFloat() * 10f * explosionForce,
                size = 4f + Random.nextFloat() * 12f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 20f,
                gravity = 0.15f + Random.nextFloat() * 0.1f,
                drag = 0.98f,
                lifespan = 0.8f + Random.nextFloat() * 0.2f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis, easing = EaseOutCubic)
        )
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxDistance = size.width.coerceAtLeast(size.height) * 0.6f

            particles.forEach { particle ->
                val lifeProgress = (progress.value / particle.lifespan).coerceIn(0f, 1f)
                if (lifeProgress >= 1f) return@forEach

                val distance = particle.speed * progress.value * maxDistance * 0.5f
                val gravityOffset = particle.gravity * progress.value * progress.value * maxDistance

                val x = centerX + cos(particle.angle) * distance
                val y = centerY + sin(particle.angle) * distance + gravityOffset

                val alpha = (1f - lifeProgress).coerceIn(0f, 1f)

                val currentSize = particle.size * (1f - lifeProgress * 0.3f)

                val rotation = particle.rotationSpeed * progress.value * 360f

                if (particle.id % 3 == 0) {
                    drawRotatedSquare(
                        x = x,
                        y = y,
                        size = currentSize,
                        rotation = rotation,
                        color = particle.color.copy(alpha = alpha),
                        centerColor = centerColor
                    )
                    drawCircle(
                        color = particle.color.copy(alpha = alpha),
                        radius = currentSize / 2,
                        center = Offset(x, y)
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = alpha * 0.6f),
                        radius = currentSize / 4,
                        center = Offset(x - currentSize / 8, y - currentSize / 8)
                    )
                    drawSparkle(
                        x = x,
                        y = y,
                        size = currentSize,
                        color = particle.color.copy(alpha = alpha)
                    )
                }
            }

            if (progress.value < 0.2f) {
                val flashAlpha = (1f - progress.value / 0.2f) * 0.8f
                drawCircle(
                    color = centerColor.copy(alpha = flashAlpha),
                    radius = size.width * 0.3f * (1f - progress.value / 0.2f),
                    center = Offset(centerX, centerY)
                )
            }
        }
    }
}

@Composable
fun CoinExplosion(
    modifier: Modifier = Modifier,
    coinCount: Int = 30,
    trigger: Boolean = false,
    onComplete: () -> Unit = {}
) {
    val goldColors = listOf(
        MetallicGold,
        MetallicGoldLight,
        MetallicGoldShine,
        NeonGold,
        Color(0xFFFFA500)
    )

    ParticleExplosion(
        modifier = modifier,
        particleCount = coinCount,
        colors = goldColors,
        centerColor = MetallicGold,
        trigger = trigger,
        onComplete = onComplete,
        explosionForce = 1.2f,
        durationMillis = 2000
    )
}

@Composable
fun ConfettiExplosion(
    modifier: Modifier = Modifier,
    confettiCount: Int = 80,
    trigger: Boolean = false,
    onComplete: () -> Unit = {}
) {
    val confettiColors = listOf(
        MetallicGold,
        NeonOrange,
        RubyRed,
        EmeraldGreen,
        CosmicPurple,
        NeonCyan,
        NeonPink
    )

    ParticleExplosion(
        modifier = modifier,
        particleCount = confettiCount,
        colors = confettiColors,
        centerColor = MetallicGoldLight,
        trigger = trigger,
        onComplete = onComplete,
        explosionForce = 1.5f,
        durationMillis = 2500
    )
}

@Composable
fun FireworkExplosion(
    modifier: Modifier = Modifier,
    trigger: Boolean = false,
    primaryColor: Color = MetallicGold,
    onComplete: () -> Unit = {}
) {
    val shiftedColor = shiftHue(primaryColor, 30f)
    ParticleExplosion(
        modifier = modifier,
        particleCount = 60,
        colors = listOf(primaryColor, shiftedColor, Color.White),
        centerColor = primaryColor,
        trigger = trigger,
        onComplete = onComplete,
        explosionForce = 1.8f,
        durationMillis = 1800
    )
}

@Composable
fun MiniBurst(
    modifier: Modifier = Modifier,
    color: Color,
    trigger: Boolean = false
) {
    val miniColors = listOf(color, Color.White, color.copy(alpha = 0.7f))

    ParticleExplosion(
        modifier = modifier.size(100.dp),
        particleCount = 15,
        colors = miniColors,
        centerColor = color,
        trigger = trigger,
        explosionForce = 0.6f,
        durationMillis = 600
    )
}

private fun DrawScope.drawRotatedSquare(
    x: Float,
    y: Float,
    size: Float,
    rotation: Float,
    color: Color,
    centerColor: Color
) {
    val halfSize = size / 2
    val angleRad = Math.toRadians(rotation.toDouble())
    val cos = cos(angleRad).toFloat()
    val sin = sin(angleRad).toFloat()

    val corners = listOf(
        Offset(-halfSize, -halfSize),
        Offset(halfSize, -halfSize),
        Offset(halfSize, halfSize),
        Offset(-halfSize, halfSize)
    ).map { corner ->
        Offset(
            x + corner.x * cos - corner.y * sin,
            y + corner.x * sin + corner.y * cos
        )
    }

    drawPath(
        path = Path().apply {
            moveTo(corners[0].x, corners[0].y)
            corners.drop(1).forEach { lineTo(it.x, it.y) }
            close()
        },
        color = color
    )
}

private fun DrawScope.drawSparkle(
    x: Float,
    y: Float,
    size: Float,
    color: Color
) {
    val armLength = size / 2

    drawLine(
        color = color,
        start = Offset(x - armLength, y),
        end = Offset(x + armLength, y),
        strokeWidth = size / 4
    )

    drawLine(
        color = color,
        start = Offset(x, y - armLength),
        end = Offset(x, y + armLength),
        strokeWidth = size / 4
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = size / 6,
        center = Offset(x, y)
    )
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
fun JackpotCelebration(
    modifier: Modifier = Modifier,
    trigger: Boolean = false,
    onComplete: () -> Unit = {}
) {
    var showConfetti by remember { mutableStateOf(false) }
    var showCoins by remember { mutableStateOf(false) }
    var showFirework by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (trigger) {
            showFirework = true
            delay(200)
            showConfetti = true
            delay(300)
            showCoins = true
            delay(2000)
            showFirework = false
            showConfetti = false
            showCoins = false
            onComplete()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (showFirework) {
            FireworkExplosion(
                primaryColor = MetallicGold,
                trigger = true
            )
        }

        if (showConfetti) {
            ConfettiExplosion(
                trigger = true
            )
        }

        if (showCoins) {
            CoinExplosion(
                coinCount = 40,
                trigger = true
            )
        }
    }
}