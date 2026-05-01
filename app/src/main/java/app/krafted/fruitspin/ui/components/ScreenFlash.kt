package app.krafted.fruitspin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.krafted.fruitspin.viewmodel.TapFeedback
import kotlinx.coroutines.delay

@Composable
fun ScreenFlash(
    tapFeedback: TapFeedback,
    modifier: Modifier = Modifier
) {
    var showFlash by remember { mutableStateOf(false) }
    var currentFeedback by remember { mutableStateOf(TapFeedback.NONE) }

    LaunchedEffect(tapFeedback) {
        if (tapFeedback != TapFeedback.NONE && tapFeedback != TapFeedback.MISS) {
            currentFeedback = tapFeedback
            showFlash = true
            val duration = if (tapFeedback == TapFeedback.JACKPOT) 500L else 350L
            delay(duration)
            showFlash = false
        }
    }

    val flashColor = when (currentFeedback) {
        TapFeedback.CORRECT -> Color(0xFF00FF55).copy(alpha = 0.25f)
        TapFeedback.WRONG -> Color(0xFFFF0000).copy(alpha = 0.35f)
        TapFeedback.JACKPOT -> Color(0xFFFFD700).copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    AnimatedVisibility(
        visible = showFlash,
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(flashColor)
        )
    }
}
