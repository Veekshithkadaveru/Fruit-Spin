package app.krafted.fruitspin.viewmodel

import androidx.compose.ui.graphics.Color
import app.krafted.fruitspin.R

enum class Fruit(
    val displayName: String,
    val drawableRes: Int,
    val basePoints: Int,
    val glowColor: Color
) {
    GRAPES("Grapes", R.drawable.fruit_grapes, 10, Color(0xFF9C27B0)),
    STRAWBERRY("Strawberry", R.drawable.fruit_strawberry, 15, Color(0xFFF44336)),
    ORANGE("Orange", R.drawable.fruit_orange, 15, Color(0xFFFF9800)),
    BANANA("Banana", R.drawable.fruit_banana, 20, Color(0xFFFFEB3B)),
    WATERMELON("Watermelon", R.drawable.fruit_watermelon, 20, Color(0xFF4CAF50)),
    PLUM("Plum", R.drawable.fruit_plum, 25, Color(0xFF7B1FA2)),
    LUCKY_7("Lucky 7", R.drawable.fruit_lucky7, 50, Color(0xFFFFD700));
}

data class GameUiState(
    val rotationAngle: Float = 0f,
    val targetFruit: Fruit = Fruit.GRAPES,
    val lives: Int = 3,
    val score: Int = 0,
    val totalCorrectTaps: Int = 0,
    val correctStreak: Int = 0,
    val scoreMultiplier: Int = 1,
    val backgroundImage: Int = R.drawable.back_1,
    val currentSpeedDps: Float = 60f,
    val isGameOver: Boolean = false,
    val correctTapsForCurrentTarget: Int = 0,
    val lastTapWasJackpot: Boolean = false,
    val lastPointsEarned: Int = 0,
    val tapFeedback: TapFeedback = TapFeedback.NONE,
    val isShaking: Boolean = false,
    val targetIsFlipping: Boolean = false,
    val speedBurst: Boolean = false
)

enum class TapFeedback {
    NONE,
    CORRECT,
    WRONG,
    JACKPOT,
    MISS
}
