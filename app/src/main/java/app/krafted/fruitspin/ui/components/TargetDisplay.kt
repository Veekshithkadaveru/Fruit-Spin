package app.krafted.fruitspin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.fruitspin.viewmodel.Fruit

@Composable
fun TargetDisplay(
    targetFruit: Fruit,
    correctTapsForCurrentTarget: Int,
    modifier: Modifier = Modifier
) {
    val tapsRemaining = 5 - correctTapsForCurrentTarget

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = targetFruit.drawableRes),
                contentDescription = targetFruit.displayName,
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = "Hit $tapsRemaining more to change target",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
