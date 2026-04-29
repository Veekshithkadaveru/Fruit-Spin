package app.krafted.fruitspin.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BestScoreDao {
    @Query("SELECT score FROM best_score WHERE id = 1")
    suspend fun getBestScore(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: BestScoreEntity)
}
