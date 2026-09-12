package dev.rocky.ui.window

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal actual fun ChatScrollbar(state: LazyListState, modifier: Modifier) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(state),
        modifier = modifier,
        style = ScrollbarStyle(
            minimalHeight = 24.dp,
            thickness = 5.dp,
            shape = RoundedCornerShape(3.dp),
            hoverDurationMillis = 200,
            unhoverColor = RockyColors.Border,
            hoverColor = RockyColors.TextMuted,
        ),
    )
}
