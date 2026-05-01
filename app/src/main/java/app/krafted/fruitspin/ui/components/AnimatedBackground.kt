package app.krafted.fruitspin.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import app.krafted.fruitspin.R

@Composable
fun AnimatedBackground(
    score: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val targetBackground = when {
        score < 150 -> R.drawable.back_1
        score < 300 -> R.drawable.back_2
        else -> R.drawable.back_3
    }

    Crossfade(
        targetState = targetBackground,
        animationSpec = tween(durationMillis = 1000),
        modifier = modifier
    ) { backgroundRes ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            content()
        }
    }
}
