package app.krafted.fruitspin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.krafted.fruitspin.ui.animations.BouncySpring
import app.krafted.fruitspin.ui.animations.pulseScale
import app.krafted.fruitspin.ui.theme.*

enum class ButtonVariant {
    GOLD, RUBY, EMERALD, PLATINUM, COSMIC
}

@Composable
fun GlossyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.GOLD,
    pulseAnimation: Boolean = false,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
    height: Dp = 64.dp,
    shimmerEnabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleAnimation = animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = BouncySpring,
        label = "button_scale"
    )

    val colorScheme = when (variant) {
        ButtonVariant.GOLD -> ButtonColors(
            gradientStart = MetallicGoldDark,
            gradientMid = MetallicGold,
            gradientEnd = MetallicGoldLight,
            shineColor = MetallicGoldShine,
            shadowColor = Color(0x40D4AF37),
            textColor = Color.White,
            shimmerColor = MetallicGoldShine
        )
        ButtonVariant.RUBY -> ButtonColors(
            gradientStart = RubyRedDark,
            gradientMid = RubyRed,
            gradientEnd = RubyRedLight,
            shineColor = Color(0xFFFF6B9D),
            shadowColor = Color(0x40E0115F),
            textColor = Color.White,
            shimmerColor = Color(0x80FFFFFF)
        )
        ButtonVariant.EMERALD -> ButtonColors(
            gradientStart = EmeraldGreenDark,
            gradientMid = EmeraldGreen,
            gradientEnd = EmeraldGreenLight,
            shineColor = Color(0xFFB8FFB8),
            shadowColor = Color(0x4050C878),
            textColor = Color.White,
            shimmerColor = Color(0x80FFFFFF)
        )
        ButtonVariant.PLATINUM -> ButtonColors(
            gradientStart = PlatinumDark,
            gradientMid = Platinum,
            gradientEnd = PlatinumLight,
            shineColor = Color.White,
            shadowColor = Color(0x40A0A0A0),
            textColor = Color(0xFF121212),
            shimmerColor = Color(0xFFFFFFFF)
        )
        ButtonVariant.COSMIC -> ButtonColors(
            gradientStart = CosmicPurpleDark,
            gradientMid = CosmicPurple,
            gradientEnd = CosmicPurpleLight,
            shineColor = NeonCyan,
            shadowColor = Color(0x409D4EDD),
            textColor = Color.White,
            shimmerColor = NeonCyan
        )
    }

    val shimmerProgress = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate = shimmerProgress.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    Box(
        modifier = modifier
            .height(height)
            .shadow(
                elevation = if (isPressed) 4.dp else 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = colorScheme.shadowColor
            )
            .graphicsLayer {
                scaleX = scaleAnimation.value
                scaleY = scaleAnimation.value
            }
            .then(if (pulseAnimation && !isPressed) Modifier.pulseScale(0.97f, 1.03f, 1200) else Modifier)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.gradientStart,
                        colorScheme.gradientMid,
                        colorScheme.gradientEnd
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.shineColor.copy(alpha = 0.6f),
                                colorScheme.shineColor.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.5f
                        ),
                        topLeft = Offset(0f, 0f),
                        size = size.copy(height = size.height * 0.5f)
                    )
                }
        )

        if (shimmerEnabled && enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        val shimmerWidth = 100f
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    colorScheme.shimmerColor.copy(alpha = 0.3f),
                                    colorScheme.shimmerColor.copy(alpha = 0.5f),
                                    colorScheme.shimmerColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                start = Offset(shimmerTranslate.value - shimmerWidth, 0f),
                                end = Offset(shimmerTranslate.value + shimmerWidth, 0f)
                            ),
                            blendMode = BlendMode.Overlay
                        )
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(cornerRadius - 2.dp))
                .drawWithContent {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            this.color = colorScheme.shineColor.copy(alpha = 0.2f)
                            this.asFrameworkPaint().apply {
                                maskFilter = android.graphics.BlurMaskFilter(
                                    20f,
                                    android.graphics.BlurMaskFilter.Blur.INNER
                                )
                            }
                        }
                        canvas.drawRect(0f, 0f, size.width, size.height, paint)
                    }
                    drawContent()
                }
        )

        Text(
            text = text,
            style = ButtonTextStyle,
            color = if (enabled) colorScheme.textColor else colorScheme.textColor.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

private data class ButtonColors(
    val gradientStart: Color,
    val gradientMid: Color,
    val gradientEnd: Color,
    val shineColor: Color,
    val shadowColor: Color,
    val textColor: Color,
    val shimmerColor: Color
)

@Composable
fun GlossyButtonSmall(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PLATINUM,
    enabled: Boolean = true
) {
    GlossyButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        pulseAnimation = false,
        enabled = enabled,
        cornerRadius = 12.dp,
        height = 48.dp,
        shimmerEnabled = false
    )
}

@Composable
fun GlossyIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.GOLD,
    size: Dp = 56.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleAnimation = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = BouncySpring,
        label = "icon_button_scale"
    )

    val colorScheme = when (variant) {
        ButtonVariant.GOLD -> ButtonColors(
            gradientStart = MetallicGoldDark,
            gradientMid = MetallicGold,
            gradientEnd = MetallicGoldLight,
            shineColor = MetallicGoldShine,
            shadowColor = Color(0x40D4AF37),
            textColor = Color.White,
            shimmerColor = MetallicGoldShine
        )
        ButtonVariant.RUBY -> ButtonColors(
            gradientStart = RubyRedDark,
            gradientMid = RubyRed,
            gradientEnd = RubyRedLight,
            shineColor = Color(0xFFFF6B9D),
            shadowColor = Color(0x40E0115F),
            textColor = Color.White,
            shimmerColor = Color(0x80FFFFFF)
        )
        else -> ButtonColors(
            gradientStart = PlatinumDark,
            gradientMid = Platinum,
            gradientEnd = PlatinumLight,
            shineColor = Color.White,
            shadowColor = Color(0x40A0A0A0),
            textColor = Color(0xFF121212),
            shimmerColor = Color(0xFFFFFFFF)
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isPressed) 4.dp else 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = colorScheme.shadowColor
            )
            .graphicsLayer {
                scaleX = scaleAnimation.value
                scaleY = scaleAnimation.value
            }
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorScheme.gradientEnd,
                        colorScheme.gradientMid,
                        colorScheme.gradientStart
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.shineColor.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        icon()
    }
}