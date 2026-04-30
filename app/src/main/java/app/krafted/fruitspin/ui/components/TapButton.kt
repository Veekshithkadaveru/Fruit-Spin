package app.krafted.fruitspin.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PointerIndicator(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(40.dp)) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val topY = 0f
        val bottomY = height * 0.6f
        val leftX = centerX - width * 0.25f
        val rightX = centerX + width * 0.25f

        drawLine(
            color = Color.White,
            start = Offset(centerX, topY),
            end = Offset(centerX, bottomY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.White,
            start = Offset(leftX, bottomY * 0.5f),
            end = Offset(centerX, bottomY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.White,
            start = Offset(rightX, bottomY * 0.5f),
            end = Offset(centerX, bottomY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TapButton(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PointerIndicator(modifier = Modifier.padding(bottom = 8.dp))

        Button(
            onClick = onTap,
            modifier = Modifier
                .size(120.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onTap() })
                },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "TAP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
