package app.krafted.fruitspin.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val BouncySpring = spring<Float>(
    stiffness = 300f,
    dampingRatio = 0.6f
)

val GentleSpring = spring<Float>(
    stiffness = 200f,
    dampingRatio = 0.8f
)

val StiffSpring = spring<Float>(
    stiffness = 400f,
    dampingRatio = 0.5f
)

val QuickSpring = spring<Float>(
    stiffness = 500f,
    dampingRatio = 0.7f
)

val EnterEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
val ExitEasing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
val EmphasizedEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
val BounceEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)

const val QUICK_DURATION = 150
const val NORMAL_DURATION = 300
const val SLOW_DURATION = 500
const val ENTRANCE_DURATION = 600

fun Modifier.shimmerEffect(
    shimmerColor: Color = ShimmerStart,
    shimmerWidth: Float = 200f,
    durationMillis: Int = 2000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = -shimmerWidth,
        targetValue = shimmerWidth * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                shimmerColor.copy(alpha = 0.3f),
                shimmerColor.copy(alpha = 0.5f),
                shimmerColor.copy(alpha = 0.3f),
                Color.Transparent
            ),
            start = Offset(translateAnimation.value - shimmerWidth, 0f),
            end = Offset(translateAnimation.value + shimmerWidth, 0f)
        )
    )
}

fun Modifier.neonGlow(
    color: Color = NeonGold,
    glowRadius: Float = 20f,
    pulseDuration: Int = 1500
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "neon_pulse")
    val pulseAnimation = transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_pulse_alpha"
    )

    drawWithContent {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                this.color = color.copy(alpha = pulseAnimation.value * 0.4f)
                this.asFrameworkPaint().apply {
                    maskFilter = android.graphics.BlurMaskFilter(
                        glowRadius,
                        android.graphics.BlurMaskFilter.Blur.OUTER
                    )
                }
            }
            canvas.drawRect(0f, 0f, size.width, size.height, paint)
        }
        drawContent()
    }
}

fun Modifier.pulseScale(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = 800
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulse_scale")
    val scaleAnimation = transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale_anim"
    )

    graphicsLayer {
        scaleX = scaleAnimation.value
        scaleY = scaleAnimation.value
    }
}

fun Modifier.floatingAnimation(
    verticalRange: Float = 10f,
    durationMillis: Int = 2000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "float")
    val floatAnimation = transition.animateFloat(
        initialValue = -verticalRange,
        targetValue = verticalRange,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_anim"
    )

    graphicsLayer {
        translationY = floatAnimation.value
    }
}

@Composable
fun floatingAnimationAsState(
    verticalRange: Float = 10f,
    durationMillis: Int = 2000
): State<Float> {
    val transition = rememberInfiniteTransition(label = "float_state")
    return transition.animateFloat(
        initialValue = -verticalRange,
        targetValue = verticalRange,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_state_anim"
    )
}

fun Modifier.shakeEffect(
    shakeIntensity: Float = 10f,
    shakeCount: Int = 8
): Modifier = composed {
    var trigger by remember { mutableIntStateOf(0) }
    val shakeAnimation = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger > 0) {
            for (i in 0 until shakeCount) {
                val target = if (i % 2 == 0) shakeIntensity else -shakeIntensity
                shakeAnimation.animateTo(
                    targetValue = target * (1f - i / shakeCount.toFloat()),
                    animationSpec = tween(50)
                )
            }
            shakeAnimation.animateTo(0f, tween(100))
        }
    }

    graphicsLayer {
        translationX = shakeAnimation.value
    }
}

@Composable
fun staggeredEntranceAnimation(
    itemCount: Int,
    delayBetweenItems: Int = 100,
    initialDelay: Int = 0
): List<State<Float>> {
    return List(itemCount) { index ->
        val animation = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(initialDelay + (index * delayBetweenItems).toLong())
            animation.animateTo(
                targetValue = 1f,
                animationSpec = tween(ENTRANCE_DURATION, easing = EnterEasing)
            )
        }
        animation.asState()
    }
}

@Composable
fun slideInFromBottom(
    delayMillis: Int = 0,
    durationMillis: Int = ENTRANCE_DURATION
): State<Float> {
    val animation = remember { Animatable(-100f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        animation.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis, easing = EnterEasing)
        )
    }
    return animation.asState()
}

@Composable
fun fadeInAnimation(
    delayMillis: Int = 0,
    durationMillis: Int = NORMAL_DURATION
): State<Float> {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis, easing = EnterEasing)
        )
    }
    return animation.asState()
}

@Composable
fun scaleInAnimation(
    delayMillis: Int = 0
): State<Float> {
    val animation = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        animation.animateTo(
            targetValue = 1f,
            animationSpec = spring(stiffness = 300f, dampingRatio = 0.7f)
        )
    }
    return animation.asState()
}

@Composable
fun infiniteRotation(
    durationMillis: Int = 2000
): State<Float> {
    val transition = rememberInfiniteTransition(label = "rotation")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_anim"
    )
}

@Composable
fun breathingAnimation(
    minAlpha: Float = 0.7f,
    maxAlpha: Float = 1f,
    durationMillis: Int = 2000
): State<Float> {
    val transition = rememberInfiniteTransition(label = "breathing")
    return transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_anim"
    )
}

@Composable
fun colorCyclingAnimation(
    colors: List<Color> = listOf(NeonGold, NeonOrange, NeonPink, NeonGold),
    durationMillis: Int = 3000
): State<Color> {
    val transition = rememberInfiniteTransition(label = "color_cycle")
    val colorIndex = transition.animateFloat(
        initialValue = 0f,
        targetValue = colors.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_cycle_anim"
    )

    return derivedStateOf {
        val index = colorIndex.value.toInt() % colors.size
        val nextIndex = (index + 1) % colors.size
        val fraction = colorIndex.value - index
        lerp(colors[index], colors[nextIndex], fraction)
    }
}

private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = start.red + (stop.red - start.red) * fraction,
        green = start.green + (stop.green - start.green) * fraction,
        blue = start.blue + (stop.blue - start.blue) * fraction,
        alpha = start.alpha + (stop.alpha - start.alpha) * fraction
    )
}

@Composable
fun bounceAnimation(
    targetValue: Float = 1f,
    initialValue: Float = 0f,
    delayMillis: Int = 0
): State<Float> {
    val animation = remember { Animatable(initialValue) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        animation.animateTo(
            targetValue = targetValue,
            animationSpec = keyframes {
                durationMillis = 800
                initialValue at 0 using BounceEasing
                targetValue * 1.2f at 400 using BounceEasing
                targetValue at 800 using EaseOutCubic
            }
        )
    }
    return animation.asState()
}

@Composable
fun counterTickAnimation(
    targetValue: Int,
    durationMillis: Int = 600
): State<Int> {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(targetValue) {
        animation.snapTo(0f)
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis, easing = EaseOutCubic)
        )
    }
    return derivedStateOf {
        (animation.value * targetValue).toInt()
    }
}