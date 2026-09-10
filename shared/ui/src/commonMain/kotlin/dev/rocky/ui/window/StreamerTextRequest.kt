package dev.rocky.ui.window

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun StreamerTextRequest(enabled: Boolean, onSend: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = value,
            onValueChange = { value = it.take(MAX_TEXT_REQUEST_LENGTH) },
            modifier = Modifier.weight(1f).testTag("streamer-text-request"),
            label = { Text("Pergunte ao Rocky") },
            placeholder = { Text("Ex.: resuma as dúvidas sobre preço") },
            maxLines = 2,
        )
        Spacer(Modifier.width(8.dp))
        Button(
            modifier = Modifier.testTag("send-streamer-text-request"),
            onClick = {
                onSend(value.trim())
                value = ""
            },
            enabled = enabled && value.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = RockyColors.Accent,
                contentColor = Color.Black,
            ),
        ) { Text("Perguntar") }
    }
}

private const val MAX_TEXT_REQUEST_LENGTH = 500
