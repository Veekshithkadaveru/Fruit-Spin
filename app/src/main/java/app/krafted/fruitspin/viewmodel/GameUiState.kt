package app.krafted.fruitspin.viewmodel

import app.krafted.fruitspin.R

enum class Fruit(val displayName: String, val drawableRes: Int, val basePoints: Int) {
    GRAPES("Grapes", R.drawable.fruit_grapes, 10),
    STRAWBERRY("Strawberry", R.drawable.fruit_strawberry, 10),
    ORANGE("Orange", R.drawable.fruit_orange, 10),
    BANANA("Banana", R.drawable.fruit_banana, 10),
    WATERMELON("Watermelon", R.drawable.fruit_watermelon, 10),
    PLUM("Plum", R.drawable.fruit_plum, 10),
    LUCKY_7("Lucky 7", R.drawable.fruit_lucky7, 50);
}

data class GameUiState(
    val rotationAngle: Float = 0f,
    val targetFruit: Fruit = Fruit.GRAPES,
    val lives: Int = 3,
    val score: Int = 0,
    val correctStreak: Int = 0,
    val scoreMultiplier: Int = 1,
    val backgroundImage: Int = R.drawable.back_1,
    val currentSpeedDps: Float = 60f,
    val isGameOver: Boolean = false,
    val correctTapsForCurrentTarget: Int = 0
)
