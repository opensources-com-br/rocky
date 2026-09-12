package dev.rocky.ui.window

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun ChatScrollbar(state: LazyListState, modifier: Modifier = Modifier)
