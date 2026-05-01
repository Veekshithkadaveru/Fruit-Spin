package app.krafted.fruitspin.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRulesTest {

    @Test
    fun correctNormalFruitAddsPointsAndStreak() {
        val state = GameRules.applyTap(
            state = GameUiState(targetFruit = Fruit.GRAPES),
            tappedFruit = Fruit.GRAPES,
            nextTarget = { Fruit.ORANGE }
        )

        assertEquals(10, state.score)
        assertEquals(1, state.correctStreak)
        assertEquals(1, state.scoreMultiplier)
    }

    @Test
    fun lucky7AddsJackpotPoints() {
        val state = GameRules.applyTap(
            state = GameUiState(targetFruit = Fruit.LUCKY_7),
            tappedFruit = Fruit.LUCKY_7,
            nextTarget = { Fruit.ORANGE }
        )

        assertEquals(150, state.score)
    }

    @Test
    fun tenthConsecutiveCorrectTapScoresWithMultiplier() {
        val state = GameRules.applyTap(
            state = GameUiState(
                targetFruit = Fruit.GRAPES,
                score = 90,
                correctStreak = 9
            ),
            tappedFruit = Fruit.GRAPES,
            nextTarget = { Fruit.ORANGE }
        )

        assertEquals(110, state.score)
        assertEquals(10, state.correctStreak)
        assertEquals(2, state.scoreMultiplier)
    }

    @Test
    fun fiveCorrectTapsChangesTargetAndResetsTargetProgress() {
        val state = GameRules.applyTap(
            state = GameUiState(
                targetFruit = Fruit.GRAPES,
                correctTapsForCurrentTarget = 4
            ),
            tappedFruit = Fruit.GRAPES,
            nextTarget = { Fruit.ORANGE }
        )

        assertEquals(Fruit.ORANGE, state.targetFruit)
        assertEquals(0, state.correctTapsForCurrentTarget)
    }

    @Test
    fun wrongTapDecrementsLivesAndResetsStreakWithoutResettingTargetProgress() {
        val state = GameRules.applyTap(
            state = GameUiState(
                targetFruit = Fruit.GRAPES,
                lives = 3,
                score = 200,
                correctStreak = 12,
                scoreMultiplier = 2,
                currentSpeedDps = 90f,
                totalCorrectTaps = 9,
                correctTapsForCurrentTarget = 3
            ),
            tappedFruit = Fruit.ORANGE,
            nextTarget = { Fruit.STRAWBERRY }
        )

        assertEquals(2, state.lives)
        assertEquals(0, state.correctStreak)
        assertEquals(1, state.scoreMultiplier)
        assertEquals(3, state.correctTapsForCurrentTarget)
        assertEquals(45f, state.currentSpeedDps)
        assertFalse(state.isGameOver)
    }

    @Test
    fun thirdWrongTapSetsGameOver() {
        val state = GameRules.applyTap(
            state = GameUiState(targetFruit = Fruit.GRAPES, lives = 1),
            tappedFruit = Fruit.ORANGE,
            nextTarget = { Fruit.STRAWBERRY }
        )

        assertEquals(0, state.lives)
        assertTrue(state.isGameOver)
    }

    @Test
    fun speedThresholdsAndSlowdownUseCurrentSpeedState() {
        assertEquals(60f,  GameRules.speedForTaps(4))
        assertEquals(90f,  GameRules.speedForTaps(9))
        assertEquals(130f, GameRules.speedForTaps(15))
        assertEquals(170f, GameRules.speedForTaps(30))

        val slowed = GameRules.applyTap(
            state = GameUiState(targetFruit = Fruit.GRAPES, score = 150, currentSpeedDps = 90f, totalCorrectTaps = 9),
            tappedFruit = Fruit.ORANGE,
            nextTarget = { Fruit.STRAWBERRY }
        )
        val advanced = GameRules.advanceRotation(
            state = slowed.copy(rotationAngle = 10f),
            deltaTimeSeconds = 1f
        )

        assertEquals(45f, slowed.currentSpeedDps)
        assertEquals(55f, advanced.rotationAngle)
    }
}
