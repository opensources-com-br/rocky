package dev.rocky.ui.window

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun RockyHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Rocky", fontWeight = FontWeight.Bold)
            Text(
                text = "the voice of chat, in harmony",
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.body2,
            )
        }
        Spacer(Modifier.width(12.dp))
        Surface(
            modifier = Modifier.border(1.dp, RockyColors.Accent, RoundedCornerShape(16.dp)),
            color = RockyColors.Surface,
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("IDLE", color = RockyColors.Accent, modifier = Modifier.padding(12.dp, 6.dp))
        }
    }
}
