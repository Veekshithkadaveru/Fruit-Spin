package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.animations.breathingAnimation
import app.krafted.fruitspin.ui.animations.fadeInAnimation
import app.krafted.fruitspin.ui.animations.slideInFromBottom
import app.krafted.fruitspin.ui.animations.pulseScale
import app.krafted.fruitspin.ui.theme.*

@Composable
fun TopBar(
    score: Int,
    bestScore: Int,
    lives: Int,
    modifier: Modifier = Modifier,
    justLostLife: Boolean = false
) {
    val slideIn = slideInFromBottom(100)
    val fadeIn = fadeInAnimation(100, 400)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .graphicsLayer {
                translationY = slideIn.value
                alpha = fadeIn.value
            }
    ) {
        NeonCard(
            modifier = Modifier.fillMaxWidth(),
            neonColor = MetallicGold,
            backgroundAlpha = 0.75f,
            cornerRadius = 20.dp,
            glowIntensity = 0.6f,
            animatedBorder = false
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreSection(score = score, bestScore = bestScore)
                LivesSection(lives = lives, justLostLife = justLostLife)
            }
        }
    }
}

@Composable
private fun ScoreSection(score: Int, bestScore: Int) {
    Row(verticalAlignment = Alignment.Bottom) {
        AnimatedCounter(
            value = score,
            style = ScoreDisplayStyle,
            glowColor = if (score >= bestScore && bestScore > 0) MetallicGold else Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (bestScore > 0) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "BEST",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = MetallicGold.copy(alpha = 0.8f)
                )

                AnimatedCounter(
                    value = bestScore,
                    style = CounterTextStyle.copy(
                        fontSize = CounterTextStyle.fontSize * 0.5
                    ),
                    glowColor = MetallicGold.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun LivesSection(lives: Int, justLostLife: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "LIVES",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = Platinum.copy(alpha = 0.7f)
        )

        LivesIndicator(
            lives = lives,
            maxLives = 3,
            justLostLife = justLostLife
        )
    }
}

@Composable
fun BestScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val isNewBest by remember(score) { mutableStateOf(true) }
    val breathingAlpha = breathingAnimation(0.6f, 1f, 1500)

    NeonCard(
        modifier = modifier,
        neonColor = MetallicGold,
        cornerRadius = 12.dp,
        glowIntensity = 1.2f,
        shimmerEnabled = isNewBest
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "🏆",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.graphicsLayer {
                    alpha = breathingAlpha.value
                }
            )

            Column {
                Text(
                    text = "BEST SCORE",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = Platinum
                )
                AnimatedCounter(
                    value = score,
                    style = CounterTextStyle.copy(
                        fontSize = CounterTextStyle.fontSize * 0.7
                    ),
                    glowColor = MetallicGold
                )
            }
        }
    }
}

@Composable
fun ScorePill(
    score: Int,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    NeonCard(
        modifier = modifier,
        neonColor = NeonOrange,
        cornerRadius = 24.dp,
        backgroundAlpha = 0.8f,
        glowIntensity = 0.8f
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showIcon) {
                Text(
                    text = "★",
                    style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                    color = NeonGold
                )
            }

            AnimatedCounter(
                value = score,
                style = ScoreDisplayStyle.copy(fontSize = ScoreDisplayStyle.fontSize * 0.6),
                glowColor = NeonGold
            )
        }
    }
}

@Composable
fun StreakIndicator(
    streak: Int,
    modifier: Modifier = Modifier
) {
    if (streak <= 0) return

    val isHotStreak = streak >= 5
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(streak) {
        pulseScale.animateTo(1.3f, tween(100))
        pulseScale.animateTo(1f, tween(200, easing = EaseOutCubic))
    }

    val glowColor = when {
        streak >= 10 -> CosmicPurple
        streak >= 5 -> NeonOrange
        else -> MetallicGold
    }

    NeonCard(
        modifier = modifier.graphicsLayer {
            scaleX = pulseScale.value
            scaleY = pulseScale.value
        },
        neonColor = glowColor,
        cornerRadius = 16.dp,
        glowIntensity = if (isHotStreak) 1.5f else 0.8f,
        animatedBorder = isHotStreak
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🔥",
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall
            )

            AnimatedCounter(
                value = streak,
                style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.6),
                glowColor = glowColor
            )
        }
    }
}