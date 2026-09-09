package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun LiveSessionControls(
    status: LiveSessionStatus,
    onStart: () -> Unit,
    onEnd: () -> Unit,
    onRestart: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RockyColors.SurfaceElevated)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "MODO DEMONSTRAÇÃO",
                color = RockyColors.Accent,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = status.description,
                modifier = Modifier.padding(top = 2.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = "Sem conexão com uma live real",
                modifier = Modifier.padding(top = 1.dp),
                color = RockyColors.TextMuted,
                fontSize = 10.sp,
            )
        }
        Spacer(Modifier.width(12.dp))
        when (status) {
            LiveSessionStatus.Stopped -> SessionButton("Iniciar", onStart, primary = true)
            LiveSessionStatus.Running -> SessionButton("Encerrar", onEnd)
            LiveSessionStatus.Ended -> SessionButton("Reiniciar", onRestart, primary = true)
        }
    }
}

@Composable
private fun SessionButton(
    label: String,
    onClick: () -> Unit,
    primary: Boolean = false,
) {
    val modifier = Modifier.height(36.dp)
    val shape = RoundedCornerShape(10.dp)
    if (primary) {
        Button(
            modifier = modifier,
            onClick = onClick,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = RockyColors.Accent,
                contentColor = Color.Black,
            ),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp),
        ) {
            Text(label, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            shape = shape,
            border = BorderStroke(1.dp, RockyColors.Border),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
        ) {
            Text(label, fontWeight = FontWeight.Normal)
        }
    }
}

private val LiveSessionStatus.description: String
    get() = when (this) {
        LiveSessionStatus.Stopped -> "Pronta para iniciar"
        LiveSessionStatus.Running -> "Sessão em andamento"
        LiveSessionStatus.Ended -> "Sessão encerrada"
    }
