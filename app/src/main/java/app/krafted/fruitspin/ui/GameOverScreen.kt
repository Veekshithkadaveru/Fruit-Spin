package app.krafted.fruitspin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.fruitspin.R
import app.krafted.fruitspin.ui.animations.*
import app.krafted.fruitspin.ui.components.*
import app.krafted.fruitspin.ui.theme.*
import app.krafted.fruitspin.viewmodel.Fruit
import app.krafted.fruitspin.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameOverScreen(
    score: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val bestScore by viewModel.bestScore.collectAsState()
    val isNewBestScore = score > 0 && score >= bestScore

    var showTitle by remember { mutableStateOf(false) }
    var showBanner by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showTitle = true
        delay(300)
        if (isNewBestScore) {
            showBanner = true
            delay(200)
            showConfetti = true
        }
        delay(200)
        showStats = true
        delay(400)
        showButtons = true
    }

    val bannerScale by animateFloatAsState(
        targetValue = if (showBanner) 1f else 0f,
        animationSpec = spring(stiffness = 300f, dampingRatio = 0.5f),
        label = "banner_bounce"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "title_shimmer")
    val shimmerTranslate by shimmerTransition.animateFloat(
        initialValue = -100f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.back_3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            center = center,
                            radius = size.width * 0.9f
                        )
                    )
                }
        )

        if (showConfetti && isNewBestScore) {
            ConfettiExplosion(
                modifier = Modifier.fillMaxSize(),
                trigger = true
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showTitle,
                enter = slideInVertically(
                    initialOffsetY = { -150 },
                    animationSpec = tween(600, easing = EaseOutCubic)
                ) + fadeIn(tween(400))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box {
                        Text(
                            text = "GAME",
                            style = androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 6.sp,
                                shadow = OutlineShadow
                            ),
                            color = RubyRed
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                RubyRedLight.copy(alpha = 0.5f),
                                                Color.Transparent
                                            ),
                                            start = Offset(shimmerTranslate - 50, 0f),
                                            end = Offset(shimmerTranslate + 50, 0f)
                                        ),
                                        blendMode = androidx.compose.ui.graphics.BlendMode.Overlay
                                    )
                                }
                        )
                    }

                    Text(
                        text = "OVER",
                        style = androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 6.sp,
                            shadow = OutlineShadow
                        ),
                        color = RubyRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isNewBestScore) {
                AnimatedVisibility(
                    visible = showBanner,
                    enter = fadeIn(tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = bannerScale
                                scaleY = bannerScale
                            }
                    ) {
                        NeonCard(
                            neonColor = MetallicGold,
                            cornerRadius = 16.dp,
                            glowIntensity = 1.5f,
                            shimmerEnabled = true,
                            animatedBorder = true
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "🏆",
                                    fontSize = 28.sp
                                )
                                Text(
                                    text = "NEW BEST SCORE!",
                                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    ),
                                    color = MetallicGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            AnimatedVisibility(
                visible = showStats,
                enter = fadeIn(tween(500, delayMillis = 100))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val statAnimations = staggeredEntranceAnimation(
                        itemCount = 4,
                        delayBetweenItems = 100,
                        initialDelay = 0
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = statAnimations[0].value
                                    translationY = (1f - statAnimations[0].value) * 50f
                                }
                        ) {
                            NeonCard(
                                neonColor = MetallicGold,
                                cornerRadius = 16.dp,
                                glowIntensity = 1f
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "SCORE",
                                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                        color = NeonOrange,
                                        letterSpacing = 2.sp
                                    )
                                    AnimatedCounter(
                                        value = score,
                                        style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.8),
                                        glowColor = MetallicGold
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = statAnimations[1].value
                                    translationY = (1f - statAnimations[1].value) * 50f
                                }
                        ) {
                            NeonCard(
                                neonColor = Platinum,
                                cornerRadius = 16.dp,
                                glowIntensity = 0.6f
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "BEST",
                                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                        color = Platinum,
                                        letterSpacing = 2.sp
                                    )
                                    AnimatedCounter(
                                        value = maxOf(score, bestScore),
                                        style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.8),
                                        glowColor = Platinum
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = statAnimations[2].value
                                    translationY = (1f - statAnimations[2].value) * 50f
                                }
                        ) {
                            NeonCard(
                                neonColor = EmeraldGreen,
                                cornerRadius = 16.dp,
                                glowIntensity = 0.6f
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "HITS",
                                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                        color = EmeraldGreen,
                                        letterSpacing = 2.sp
                                    )
                                    AnimatedCounter(
                                        value = 0,
                                        style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.8),
                                        glowColor = EmeraldGreen
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = statAnimations[3].value
                                    translationY = (1f - statAnimations[3].value) * 50f
                                }
                        ) {
                            NeonCard(
                                neonColor = RubyRed,
                                cornerRadius = 16.dp,
                                glowIntensity = 0.6f
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "LIVES",
                                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                        color = RubyRed,
                                        letterSpacing = 2.sp
                                    )
                                    AnimatedCounter(
                                        value = 0,
                                        style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.8),
                                        glowColor = RubyRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showButtons,
                enter = slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(500, easing = EaseOutCubic)
                ) + fadeIn(tween(400))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlossyButton(
                        text = "▶ PLAY AGAIN",
                        onClick = onPlayAgain,
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.RUBY,
                        pulseAnimation = true,
                        cornerRadius = 18.dp,
                        height = 68.dp,
                        shimmerEnabled = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlossyButton(
                        text = "⌂ HOME",
                        onClick = onMainMenu,
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.PLATINUM,
                        pulseAnimation = false,
                        cornerRadius = 14.dp,
                        height = 52.dp,
                        shimmerEnabled = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(tween(600, delayMillis = 200))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Fruit.values().forEachIndexed { index, fruit ->
                        val float by floatingAnimationAsState(verticalRange = 5f + index * 2f, durationMillis = 2000 + index * 200)
                        Image(
                            painter = painterResource(id = fruit.drawableRes),
                            contentDescription = fruit.displayName,
                            modifier = Modifier
                                .size(36.dp)
                                .graphicsLayer {
                                    translationY = float
                                }
                        )
                    }
                }
            }
        }
    }
}