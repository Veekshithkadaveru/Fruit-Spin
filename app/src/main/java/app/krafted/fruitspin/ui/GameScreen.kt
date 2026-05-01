package app.krafted.fruitspin.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.fruitspin.ui.animations.*
import app.krafted.fruitspin.ui.components.*
import app.krafted.fruitspin.ui.theme.*
import app.krafted.fruitspin.viewmodel.GameViewModel
import app.krafted.fruitspin.viewmodel.TapFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    onGameOver: (Int) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val bestScore by viewModel.bestScore.collectAsState()

    var showJackpotCelebration by remember { mutableStateOf(false) }
    var showMiniBurst by remember { mutableStateOf(false) }
    var miniBurstColor by remember { mutableStateOf(EmeraldGreen) }

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
            delay(500) // Let animations finish
            onGameOver(uiState.score)
        }
    }

    LaunchedEffect(uiState.isShaking) {
        if (uiState.isShaking) {
            delay(500)
            viewModel.clearShake()
        }
    }

    LaunchedEffect(uiState.tapFeedback) {
        when (uiState.tapFeedback) {
            TapFeedback.CORRECT -> {
                miniBurstColor = EmeraldGreen
                showMiniBurst = true
                delay(400)
                showMiniBurst = false
                viewModel.clearTapFeedback()
            }
            TapFeedback.WRONG -> {
                miniBurstColor = RubyRed
                showMiniBurst = true
                delay(400)
                showMiniBurst = false
                viewModel.clearTapFeedback()
            }
            TapFeedback.JACKPOT -> {
                showJackpotCelebration = true
                delay(2000)
                showJackpotCelebration = false
                viewModel.clearTapFeedback()
            }
            else -> {
                if (uiState.tapFeedback != TapFeedback.NONE) {
                    delay(400)
                    viewModel.clearTapFeedback()
                }
            }
        }
    }

    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(uiState.isShaking) {
        if (uiState.isShaking) {
            shakeAnim.snapTo(0f)
            repeat(4) { index ->
                val direction = if (index % 2 == 0) 1f else -1f
                val magnitude = 12f * (1f - index / 4f)
                shakeAnim.animateTo(
                    targetValue = magnitude * direction,
                    animationSpec = tween(80, easing = LinearEasing)
                )
            }
            shakeAnim.animateTo(0f, spring(stiffness = 400f, dampingRatio = 0.4f))
        }
    }

    AnimatedBackground(score = uiState.score) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenFlash(
                tapFeedback = uiState.tapFeedback,
                modifier = Modifier.fillMaxSize()
            )

            if (showJackpotCelebration) {
                JackpotCelebration(
                    modifier = Modifier.fillMaxSize(),
                    trigger = true
                )
            }

            if (showMiniBurst) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    MiniBurst(
                        color = miniBurstColor,
                        trigger = true,
                        modifier = Modifier.size(150.dp)
                    )
                }
            }

            FloatingScoreContainer(
                tapFeedback = uiState.tapFeedback,
                lastPointsEarned = uiState.lastPointsEarned,
                wasJackpot = uiState.lastTapWasJackpot,
                targetFruit = uiState.targetFruit,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = shakeAnim.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(
                    score = uiState.score,
                    bestScore = bestScore,
                    lives = uiState.lives,
                    justLostLife = uiState.isShaking,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                StreakIndicator(
                    streak = uiState.correctStreak.coerceAtLeast(0),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TargetDisplay(
                    targetFruit = uiState.targetFruit,
                    correctTapsForCurrentTarget = uiState.correctTapsForCurrentTarget,
                    isFlipping = uiState.targetIsFlipping,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                SpeedIndicator(
                    speedDps = uiState.currentSpeedDps,
                    speedBurst = uiState.speedBurst,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        PointerIndicator()
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        contentAlignment = Alignment.Center
                    ) {
                        FruitWheel(
                            rotationAngle = uiState.rotationAngle,
                            tapFeedback = uiState.tapFeedback,
                            targetFruit = uiState.targetFruit
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TapButton(
                        onTap = { viewModel.onTap() },
                        tapFeedback = uiState.tapFeedback,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        enabled = !uiState.isGameOver
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))
            }
        }
    }
}

@Composable
private fun EnhancedScreenFlash(
    tapFeedback: TapFeedback,
    modifier: Modifier = Modifier
) {
    var showFlash by remember { mutableStateOf(false) }
    var flashColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(tapFeedback) {
        when (tapFeedback) {
            TapFeedback.CORRECT -> {
                flashColor = EmeraldGreen.copy(alpha = 0.3f)
                showFlash = true
                delay(150)
                showFlash = false
            }
            TapFeedback.WRONG -> {
                flashColor = RubyRed.copy(alpha = 0.4f)
                showFlash = true
                delay(200)
                showFlash = false
            }
            TapFeedback.JACKPOT -> {
                flashColor = MetallicGold.copy(alpha = 0.5f)
                showFlash = true
                delay(300)
                showFlash = false
            }
            else -> {}
        }
    }

    if (showFlash) {
        Box(
            modifier = modifier
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                flashColor,
                                flashColor.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.width * 0.8f
                        )
                    )
                }
        )
    }
}