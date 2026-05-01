package app.krafted.fruitspin.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fruitspin.data.db.AppDatabase
import app.krafted.fruitspin.utils.TapValidator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val bestScoreDao = db.bestScoreDao()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    private var slowdownJob: Job? = null

    init {
        loadBestScore()
    }

    private fun loadBestScore() {
        viewModelScope.launch {
            _bestScore.value = bestScoreDao.getBestScore() ?: 0
        }
    }

    fun saveBestScore(score: Int) {
        viewModelScope.launch {
            val current = bestScoreDao.getBestScore() ?: 0
            if (score > current) {
                bestScoreDao.insertOrUpdate(
                    app.krafted.fruitspin.data.db.BestScoreEntity(score = score)
                )
                _bestScore.value = score
            }
        }
    }

    fun updateRotation(angle: Float) {
        _uiState.update { it.copy(rotationAngle = angle) }
    }

    fun advanceRotation(deltaTimeSeconds: Float) {
        _uiState.update { GameRules.advanceRotation(it, deltaTimeSeconds) }
    }

    fun onTap() {
        val currentState = _uiState.value
        if (currentState.isGameOver) return

        val tappedFruit = TapValidator.validateTap(currentState.rotationAngle)
        val wasWrongTap = tappedFruit != currentState.targetFruit

        _uiState.update {
            GameRules.applyTap(it, tappedFruit) { current ->
                GameRules.randomTargetDifferentFrom(current)
            }
        }

        when {
            tappedFruit == null -> performHaptic(HapticType.MISS)
            wasWrongTap -> performHaptic(HapticType.WRONG)
            tappedFruit == Fruit.LUCKY_7 -> performHaptic(HapticType.JACKPOT)
            else -> performHaptic(HapticType.CORRECT)
        }

        if (wasWrongTap) {
            restoreSpeedAfterSlowdown()
        }
    }

    private fun performHaptic(type: HapticType) {
        val vibrator = getApplication<Application>().getSystemService(Vibrator::class.java)
        vibrator?.let {
            when (type) {
                HapticType.CORRECT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        it.vibrate(20)
                    }
                }
                HapticType.WRONG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1))
                    } else {
                        it.vibrate(longArrayOf(0, 100, 50, 100), -1)
                    }
                }
                HapticType.JACKPOT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 30, 50, 30, 100), -1))
                    } else {
                        it.vibrate(longArrayOf(0, 50, 30, 50, 30, 100), -1)
                    }
                }
                HapticType.MISS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(10, 30))
                    } else {
                        it.vibrate(10)
                    }
                }
            }
        }
    }

    private enum class HapticType {
        CORRECT, WRONG, JACKPOT, MISS
    }

    fun resetGame() {
        slowdownJob?.cancel()
        _uiState.value = GameUiState()
    }

    fun clearShake() {
        _uiState.update { it.copy(isShaking = false) }
    }

    fun clearTapFeedback() {
        _uiState.update { it.copy(tapFeedback = TapFeedback.NONE, speedBurst = false, targetIsFlipping = false) }
    }

    private fun restoreSpeedAfterSlowdown() {
        slowdownJob?.cancel()
        slowdownJob = viewModelScope.launch {
            delay(GameRules.WRONG_TAP_SLOWDOWN_MS)
            _uiState.update {
                if (it.isGameOver) it else it.copy(currentSpeedDps = GameRules.speedForTaps(it.totalCorrectTaps))
            }
        }
    }
}
