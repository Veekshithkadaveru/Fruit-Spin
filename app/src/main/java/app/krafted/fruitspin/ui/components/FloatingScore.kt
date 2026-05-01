package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.fruitspin.viewmodel.Fruit
import app.krafted.fruitspin.viewmodel.TapFeedback
import kotlinx.coroutines.delay

data class FloatingText(
    val id: Int,
    val text: String,
    val color: Color
)

@Composable
fun FloatingScoreContainer(
    tapFeedback: TapFeedback,
    lastPointsEarned: Int,
    wasJackpot: Boolean,
    targetFruit: Fruit,
    modifier: Modifier = Modifier
) {
    val floatingTexts = remember { mutableStateListOf<FloatingText>() }
    var nextId by remember { mutableStateOf(0) }

    LaunchedEffect(tapFeedback) {
        when (tapFeedback) {
            TapFeedback.CORRECT -> {
                val text = "+$lastPointsEarned"
                floatingTexts.add(FloatingText(nextId++, text, Color(0xFF00FF88)))
            }
            TapFeedback.JACKPOT -> {
                val text = "🎰 JACKPOT! +$lastPointsEarned"
                floatingTexts.add(FloatingText(nextId++, text, Color(0xFFFFD700)))
            }
            TapFeedback.WRONG -> {
                floatingTexts.add(FloatingText(nextId++, "WRONG! -1 ❤️", Color(0xFFFF4444)))
            }
            TapFeedback.MISS -> {
                floatingTexts.add(FloatingText(nextId++, "MISS!", Color(0xFFFF8800)))
            }
            else -> {}
        }

        if (tapFeedback != TapFeedback.NONE) {
            delay(900)
            if (floatingTexts.isNotEmpty()) {
                floatingTexts.removeAt(0)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        floatingTexts.forEach { floatingText ->
            key(floatingText.id) {
                FloatingScoreText(
                    text = floatingText.text,
                    color = floatingText.color
                )
            }
        }
    }
}

@Composable
private fun FloatingScoreText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    var startAnimation by remember { mutableStateOf(false) }

    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) -150f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseOut),
        label = "float_y"
    )

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.4f else 1f,
        animationSpec = tween(durationMillis = 900, easing = EaseOut),
        label = "float_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 900, easing = EaseOut),
        label = "float_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(900)
        visible = false
    }

    if (visible) {
        Text(
            text = text,
            fontSize = if (text.length > 10) 22.sp else 28.sp,
            fontWeight = FontWeight.Black,
            color = color.copy(alpha = alpha),
            modifier = modifier
                .offset(y = offsetY.dp)
                .scale(scale),
            letterSpacing = 1.sp
        )
    }
}
