package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.live.LiveNote
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun NoteRow(
    note: LiveNote,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = note.timestamp,
            modifier = Modifier.width(42.dp).padding(top = 3.dp),
            color = RockyColors.TextMuted,
            style = MaterialTheme.typography.caption,
        )
        Column(modifier = Modifier.weight(1f)) {
            EvidenceButton(note.evidence)
            Text(
                text = note.text,
                color = RockyColors.TextPrimary,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = note.tag,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(RockyColors.SurfaceElevated, RoundedCornerShape(12.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp),
                color = RockyColors.TextSecondary,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
            )
        }
        Column {
            IconButton(onClick = onEdit) {
                Icon(Icons.Outlined.Edit, contentDescription = "Editar nota", tint = RockyColors.TextSecondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Excluir nota", tint = RockyColors.TextSecondary)
            }
        }
    }
}
