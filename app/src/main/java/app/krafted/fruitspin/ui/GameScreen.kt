package app.krafted.fruitspin.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.fruitspin.ui.components.FruitWheel
import app.krafted.fruitspin.viewmodel.GameViewModel

@Composable
fun GameScreen(
    onGameOver: (Int) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { time ->
                if (lastTime == 0L) lastTime = time
                val deltaTime = (time - lastTime) / 1_000_000_000f
                lastTime = time

                viewModel.advanceRotation(deltaTime)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FruitWheel(
            rotationAngle = uiState.rotationAngle,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}
