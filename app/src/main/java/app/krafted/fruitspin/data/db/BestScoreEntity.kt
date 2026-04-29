package app.krafted.fruitspin.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "best_score")
data class BestScoreEntity(
    @PrimaryKey val id: Int = 1,
    val score: Int = 0
)
