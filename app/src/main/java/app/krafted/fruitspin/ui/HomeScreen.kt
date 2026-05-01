package app.krafted.fruitspin.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val bestScore by viewModel.bestScore.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val titleBounce by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_bounce"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "title_shimmer")
    val shimmerTranslate by shimmerTransition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "title_shimmer"
    )

    val titleSlideIn = slideInFromBottom(0)
    val titleFadeIn = fadeInAnimation(100, 600)

    val wheelSlideIn = slideInFromBottom(200)
    val wheelFadeIn = fadeInAnimation(300, 500)

    val statsSlideIn = slideInFromBottom(400)
    val statsFadeIn = fadeInAnimation(500, 500)

    val buttonSlideIn = slideInFromBottom(600)
    val buttonFadeIn = fadeInAnimation(700, 500)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.back_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ParticleExplosion(
            modifier = Modifier.fillMaxSize(),
            particleCount = 20,
            colors = listOf(NeonPink, NeonOrange, NeonGold, MetallicGold)
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
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            center = center,
                            radius = size.width * 0.8f
                        )
                    )

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
                }
        )

        FloatingDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .graphicsLayer {
                        translationY = titleSlideIn.value + titleBounce
                        alpha = titleFadeIn.value
                        scaleX = 1f + (titleBounce / 200f)
                        scaleY = 1f - (titleBounce / 200f)
                    }
            ) {
                Box {
                    Text(
                        text = "FRUIT",
                        style = androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 10.sp,
                            shadow = Shadow(
                                color = MetallicGoldDark,
                                offset = Offset(4f, 4f),
                                blurRadius = 8f
                            )
                        ),
                        color = MetallicGold
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
                                            Color.White.copy(alpha = 0.6f),
                                            Color.Transparent
                                        ),
                                        start = Offset(shimmerTranslate - 100, 0f),
                                        end = Offset(shimmerTranslate + 100, 0f)
                                    ),
                                    blendMode = BlendMode.Overlay
                                )
                            }
                    )
                }

                Text(
                    text = "SPIN",
                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 16.sp,
                        shadow = Shadow(
                            color = NeonPink,
                            offset = Offset(0f, 0f),
                            blurRadius = 20f
                        )
                    ),
                    color = Color.White,
                    modifier = Modifier.offset(y = (-8).dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .graphicsLayer {
                        translationY = wheelSlideIn.value
                        alpha = wheelFadeIn.value
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.spinner_logo),
                    contentDescription = "Animated Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .graphicsLayer {
                        translationY = statsSlideIn.value
                        alpha = statsFadeIn.value
                    },
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeonCard(
                    modifier = Modifier.weight(1f),
                    neonColor = MetallicGold,
                    cornerRadius = 16.dp,
                    glowIntensity = 0.8f
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BEST SCORE",
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            color = NeonOrange,
                            letterSpacing = 2.sp
                        )
                        AnimatedCounter(
                            value = bestScore,
                            style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.7),
                            glowColor = MetallicGold
                        )
                    }
                }

                NeonCard(
                    modifier = Modifier.weight(1f),
                    neonColor = NeonOrange,
                    cornerRadius = 16.dp,
                    glowIntensity = 0.6f
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BEST STREAK",
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            color = NeonOrange,
                            letterSpacing = 2.sp
                        )
                        AnimatedCounter(
                            value = 0,
                            style = CounterTextStyle.copy(fontSize = CounterTextStyle.fontSize * 0.7),
                            glowColor = NeonOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = buttonSlideIn.value
                        alpha = buttonFadeIn.value
                    }
            ) {
                GlossyButton(
                    text = "▶ PLAY",
                    onClick = onPlayClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.RUBY,
                    pulseAnimation = true,
                    cornerRadius = 20.dp,
                    height = 72.dp,
                    shimmerEnabled = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val howToSlideIn = slideInFromBottom(800)
            val howToFadeIn = fadeInAnimation(900, 500)

            InfoPanel(
                title = "HOW TO PLAY",
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = howToSlideIn.value
                        alpha = howToFadeIn.value
                    },
                neonColor = NeonPink
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text("🎯", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "Watch the spinning wheel",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text("👆", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "Tap TAP! when the target fruit",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Text(
                        text = "reaches the ▼ pointer at the bottom",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = Platinum,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "❤️ 3 lives · Target changes every 5 hits",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = NeonGold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingDecorations() {
    val float1 by floatingAnimationAsState(8f, 3000)
    val float2 by floatingAnimationAsState(12f, 3500)
    val float3 by floatingAnimationAsState(6f, 2500)
    val float4 by floatingAnimationAsState(10f, 4000)

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "🍎",
            fontSize = 24.sp,
            modifier = Modifier
                .offset(x = 32.dp, y = 120.dp)
                .graphicsLayer {
                    translationY = float1
                    alpha = 0.3f
                }
        )

        Text(
            text = "🍇",
            fontSize = 28.sp,
            modifier = Modifier
                .offset(x = 320.dp, y = 200.dp)
                .graphicsLayer {
                    translationY = float2
                    alpha = 0.25f
                }
        )

        Text(
            text = "🍋",
            fontSize = 22.sp,
            modifier = Modifier
                .offset(x = 40.dp, y = 480.dp)
                .graphicsLayer {
                    translationY = float3
                    alpha = 0.2f
                }
        )

        Text(
            text = "🍊",
            fontSize = 26.sp,
            modifier = Modifier
                .offset(x = 300.dp, y = 540.dp)
                .graphicsLayer {
                    translationY = float4
                    alpha = 0.25f
                }
        )

        val breathing by breathingAnimation(0.2f, 0.5f, 2000)
        Text(
            text = "✨",
            fontSize = 20.sp,
            modifier = Modifier
                .offset(x = 280.dp, y = 100.dp)
                .graphicsLayer {
                    alpha = breathing
                }
        )
    }
}

@Composable
private fun CanvasPointer() {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(28.dp, 24.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2, size.height)
            lineTo(0f, 0f)
            lineTo(size.width, 0f)
            close()
        }

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                this.color = NeonGold.copy(alpha = 0.4f)
                this.asFrameworkPaint().apply {
                    maskFilter = android.graphics.BlurMaskFilter(
                        15f,
                        android.graphics.BlurMaskFilter.Blur.OUTER
                    )
                }
            }
            canvas.drawPath(path, paint)
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(MetallicGold, MetallicGoldLight, MetallicGold)
            )
        )

        drawPath(
            path = path,
            color = MetallicGoldDark,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}