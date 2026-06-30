package com.delacrixmorgan.squark.ui.preference.currency

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AlphabetScrubber(
    letters: List<Char>,
    modifier: Modifier = Modifier,
    onLetterSelected: (Char) -> Unit,
) {
    var height by remember { mutableStateOf(0) }
    var activeLetter by remember { mutableStateOf<Char?>(null) }
    var touchY by remember { mutableStateOf(0f) }

    fun update(y: Float) {
        letters.letterAt(y, height)?.let {
            touchY = y
            activeLetter = it
            onLetterSelected(it)
        }
    }

    val bubbleSize = 48.dp
    val railPadding = 8.dp
    val density = LocalDensity.current

    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 4.dp)
                .onSizeChanged { height = it.height }
                .pointerInput(letters) {
                    detectVerticalDragGestures(
                        onDragStart = { offset -> update(offset.y) },
                        onVerticalDrag = { change, _ -> update(change.position.y) },
                        onDragEnd = { activeLetter = null },
                        onDragCancel = { activeLetter = null },
                    )
                }
                .pointerInput(letters) {
                    detectTapGestures(
                        onPress = { offset ->
                            update(offset.y)
                            tryAwaitRelease()
                            activeLetter = null
                        },
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            letters.forEach { letter ->
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        activeLetter?.let { letter ->
            val bubbleSizePx = with(density) { bubbleSize.toPx() }
            val railPaddingPx = with(density) { railPadding.toPx() }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        IntOffset(
                            x = (-bubbleSizePx - railPaddingPx).roundToInt(),
                            y = (touchY - bubbleSizePx / 2).roundToInt(),
                        )
                    }
                    .size(bubbleSize),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

private fun List<Char>.letterAt(y: Float, height: Int): Char? {
    if (isEmpty() || height <= 0) return null
    val idx = (y / height * size).toInt().coerceIn(0, size - 1)
    return this[idx]
}