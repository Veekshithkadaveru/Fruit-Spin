package app.krafted.fruitspin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.fruitspin.ui.components.AnimatedBackground
import app.krafted.fruitspin.ui.components.FruitWheel
import app.krafted.fruitspin.ui.components.PointerIndicator
import app.krafted.fruitspin.ui.components.TapButton
import app.krafted.fruitspin.ui.components.TargetDisplay
import app.krafted.fruitspin.ui.components.TopBar
import app.krafted.fruitspin.viewmodel.GameViewModel

@Composable
fun GameScreen(
    onGameOver: (Int) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val bestScore by viewModel.bestScore.collectAsState()

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

    LaunchedEffect(uiState.isGameOver) {
        if (uiState.isGameOver) {
            viewModel.saveBestScore(uiState.score)
            onGameOver(uiState.score)
        }
    }

    AnimatedBackground(score = uiState.score) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                score = uiState.score,
                bestScore = bestScore,
                lives = uiState.lives,
                modifier = Modifier.padding(top = 16.dp)
            )

            TargetDisplay(
                targetFruit = uiState.targetFruit,
                correctTapsForCurrentTarget = uiState.correctTapsForCurrentTarget,
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PointerIndicator()

                Spacer(modifier = Modifier.height(8.dp))

                FruitWheel(
                    rotationAngle = uiState.rotationAngle,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TapButton(
                    onTap = { viewModel.onTap() },
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}
