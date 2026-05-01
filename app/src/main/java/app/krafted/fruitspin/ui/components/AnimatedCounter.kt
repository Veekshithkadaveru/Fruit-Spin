package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.animations.BouncySpring
import app.krafted.fruitspin.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun AnimatedCounter(
    value: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = CounterTextStyle,
    prefix: String = "",
    suffix: String = "",
    digitCount: Int? = null,
    animateOnMount: Boolean = true,
    glowColor: Color = MetallicGold
) {
    val displayValue = remember(value) { value.coerceAtLeast(0) }
    val digits = displayValue.toString().map { it.toString() }
    val targetDigitCount = digitCount ?: digits.size

    val paddedDigits = List(targetDigitCount - digits.size) { "0" } + digits

    var animateTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(value) {
        if (animateOnMount || animateTrigger > 0) {
            animateTrigger++
        }
    }

    LaunchedEffect(Unit) {
        if (animateOnMount) {
            delay(100)
            animateTrigger = 1
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefix.isNotEmpty()) {
            Text(
                text = prefix,
                style = style,
                color = Platinum
            )
        }

        paddedDigits.forEachIndexed { index, digit ->
            AnimatedDigit(
                digit = digit,
                index = index,
                totalDigits = paddedDigits.size,
                trigger = animateTrigger,
                style = style,
                glowColor = glowColor
            )
        }

        if (suffix.isNotEmpty()) {
            Text(
                text = suffix,
                style = style.copy(fontSize = style.fontSize * 0.6),
                color = Platinum
            )
        }
    }
}

@Composable
private fun AnimatedDigit(
    digit: String,
    index: Int,
    totalDigits: Int,
    trigger: Int,
    style: TextStyle,
    glowColor: Color
) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(trigger, digit) {
        val target = digit.toIntOrNull() ?: 0
        val startValue = (target + 10 - 3) % 10 // Start a few numbers back

        animatedValue.snapTo(startValue.toFloat())
        animatedValue.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(
                durationMillis = 400 + (index * 80),
                easing = EaseOutCubic
            )
        )
    }

    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            scaleAnim.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(100)
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = BouncySpring
            )
        }
    }

    val goldBrush = Brush.verticalGradient(
        colors = listOf(
            MetallicGoldLight,
            MetallicGold,
            MetallicGoldDark,
            MetallicGold
        )
    )

    Box(
        modifier = Modifier
            .size(width = style.fontSize.value.dp * 0.7f, height = style.fontSize.value.dp * 1.1f)
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            },
        contentAlignment = Alignment.Center
    ) {
        val currentDigit = animatedValue.value.toInt() % 10
        val nextDigit = (currentDigit + 1) % 10
        val fraction = animatedValue.value - currentDigit

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentDigit.toString(),
                style = style,
                modifier = Modifier
                    .graphicsLayer {
                        translationY = -fraction * style.fontSize.value * 1.5f
                        alpha = 1f - fraction
                    },
                textAlign = TextAlign.Center,
                color = glowColor
            )

            if (fraction > 0.1f) {
                Text(
                    text = nextDigit.toString(),
                    style = style,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = (1f - fraction) * style.fontSize.value * 1.5f
                            alpha = fraction
                        },
                    textAlign = TextAlign.Center,
                    color = glowColor
                )
            }
        }
    }
}

@Composable
fun ScoreCounter(
    score: Int,
    modifier: Modifier = Modifier,
    isBestScore: Boolean = false,
    showLabel: Boolean = true
) {
    val formattedScore = remember(score) {
        score.toString().reversed().chunked(3).joinToString(",").reversed()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showLabel) {
            Text(
                text = if (isBestScore) "BEST" else "SCORE",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = Platinum
            )
        }

        AnimatedCounter(
            value = score,
            style = ScoreDisplayStyle,
            glowColor = if (isBestScore) MetallicGold else Color.White
        )
    }
}

@Composable
fun StatCounter(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonGold
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedCounter(
            value = value,
            style = CounterTextStyle.copy(
                fontSize = CounterTextStyle.fontSize * 0.8,
                color = accentColor
            ),
            glowColor = accentColor
        )

        Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = Platinum
        )
    }
}

@Composable
fun TimerCounter(
    seconds: Int,
    modifier: Modifier = Modifier,
    isLowTime: Boolean = false
) {
    val minutes = seconds / 60
    val secs = seconds % 60

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NeonCard(
            modifier = Modifier.size(48.dp, 56.dp),
            neonColor = if (isLowTime) RubyRed else MetallicGold,
            cornerRadius = 8.dp,
            glowIntensity = if (isLowTime) 1.5f else 1f
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", minutes),
                    style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.7),
                    color = Color.White
                )
            }
        }

        Text(
            text = ":",
            style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.6),
            color = if (isLowTime) RubyRed else MetallicGold
        )

        NeonCard(
            modifier = Modifier.size(48.dp, 56.dp),
            neonColor = if (isLowTime) RubyRed else MetallicGold,
            cornerRadius = 8.dp,
            glowIntensity = if (isLowTime) 1.5f else 1f
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", secs),
                    style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.7),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ComboDisplay(
    combo: Int,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(combo) {
        if (combo > 1) {
            scale.snapTo(0.5f)
            alpha.snapTo(0f)

            launch {
                scale.animateTo(1.3f, tween(150, easing = EaseOutCubic))
                scale.animateTo(1f, BouncySpring)
            }

            launch {
                alpha.animateTo(1f, tween(100))
                delay(1500)
                alpha.animateTo(0f, tween(300))
            }
        }
    }

    if (combo > 1) {
        Box(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                }
        ) {
            NeonCard(
                neonColor = NeonOrange,
                cornerRadius = 12.dp,
                glowIntensity = 1.5f
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${combo}x",
                        style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
                        color = NeonOrange
                    )
                    Text(
                        text = "COMBO",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = Platinum
                    )
                }
            }
        }
    }
}