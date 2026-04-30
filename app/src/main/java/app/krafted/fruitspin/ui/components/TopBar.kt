package app.krafted.fruitspin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    score: Int,
    bestScore: Int,
    lives: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreDisplay(score = score, bestScore = bestScore)
        LivesIndicator(lives = lives)
    }
}

@Composable
private fun ScoreDisplay(score: Int, bestScore: Int) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = score.toString(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = " / $bestScore",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun LivesIndicator(lives: Int) {
    Row {
        repeat(3) { index ->
            val isActive = index < lives
            Text(
                text = if (isActive) "❤️" else "🖤",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = if (index > 0) 4.dp else 0.dp)
            )
        }
    }
}
