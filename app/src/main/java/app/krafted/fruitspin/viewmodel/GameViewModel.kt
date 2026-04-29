package app.krafted.fruitspin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fruitspin.data.db.AppDatabase
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

    fun resetGame() {
        _uiState.value = GameUiState()
    }
}
