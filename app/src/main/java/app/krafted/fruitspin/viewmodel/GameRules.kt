package app.krafted.fruitspin.viewmodel

import kotlin.random.Random

fun getSpeedDps(totalTaps: Int): Float = GameRules.speedForTaps(totalTaps)

internal object GameRules {
    const val WRONG_TAP_SLOWDOWN_MS = 450L

    fun applyTap(
        state: GameUiState,
        tappedFruit: Fruit?,
        nextTarget: (Fruit) -> Fruit
    ): GameUiState {
        if (state.isGameOver) return state

        return if (tappedFruit == state.targetFruit) {
            applyCorrectTap(state, nextTarget)
        } else {
            applyWrongTap(state)
        }
    }

    fun advanceRotation(state: GameUiState, deltaTimeSeconds: Float): GameUiState {
        if (state.isGameOver) return state

        val rotation = (state.rotationAngle + (state.currentSpeedDps * deltaTimeSeconds)) % 360f
        return state.copy(rotationAngle = rotation)
    }

    fun speedForTaps(taps: Int): Float = when {
        taps < 5 -> 60f
        taps < 15 -> 90f
        taps < 30 -> 130f
        taps < 50 -> 170f
        else -> 210f
    }

    fun backgroundForScore(score: Int): Int = when {
        score < 150 -> app.krafted.fruitspin.R.drawable.back_1
        score < 300 -> app.krafted.fruitspin.R.drawable.back_2
        else -> app.krafted.fruitspin.R.drawable.back_3
    }

    fun randomTargetDifferentFrom(current: Fruit, random: Random = Random.Default): Fruit {
        val options = Fruit.values().filter { it != current }
        return options[random.nextInt(options.size)]
    }

    private fun applyCorrectTap(
        state: GameUiState,
        nextTarget: (Fruit) -> Fruit
    ): GameUiState {
        val streak = state.correctStreak + 1
        val totalTaps = state.totalCorrectTaps + 1
        val multiplier = if (streak >= 10) 2 else 1
        val isJackpot = state.targetFruit == Fruit.LUCKY_7
        val fruitMultiplier = if (isJackpot) 3 else 1
        val points = state.targetFruit.basePoints * multiplier * fruitMultiplier
        val score = state.score + points
        val targetFruit = nextTarget(state.targetFruit).takeIf { it != state.targetFruit }
            ?: randomTargetDifferentFrom(state.targetFruit)

        return state.copy(
            targetFruit = targetFruit,
            score = score,
            totalCorrectTaps = totalTaps,
            correctStreak = streak,
            scoreMultiplier = multiplier,
            currentSpeedDps = speedForTaps(totalTaps),
            correctTapsForCurrentTarget = 0,
            lastTapWasJackpot = isJackpot,
            lastPointsEarned = points,
            tapFeedback = if (isJackpot) TapFeedback.JACKPOT else TapFeedback.CORRECT,
            targetIsFlipping = true,
            speedBurst = speedForTaps(totalTaps) > state.currentSpeedDps
        )
    }

    private fun applyWrongTap(state: GameUiState): GameUiState {
        val lives = (state.lives - 1).coerceAtLeast(0)

        return state.copy(
            lives = lives,
            correctStreak = 0,
            scoreMultiplier = 1,
            currentSpeedDps = speedForTaps(state.totalCorrectTaps) / 2f,
            isGameOver = lives == 0,
            tapFeedback = TapFeedback.WRONG,
            isShaking = true
        )
    }
}
