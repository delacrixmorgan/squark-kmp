package com.delacrixmorgan.squark.ui.table

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dontsaybojio.rubbertextview.RubberDragBox
import com.dontsaybojio.rubbertextview.RubberDragDirection
import com.dontsaybojio.rubbertextview.RubberTextView
import com.dontsaybojio.rubbertextview.rememberRubberDragState

@Composable
fun CurrencyTable(
    state: TableUiState,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onRowClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val collapsedCellBackground = MaterialTheme.colorScheme.surface
    val collapsedCellForeground = MaterialTheme.colorScheme.onSurface
    val expandedCellBackground = MaterialTheme.colorScheme.inverseSurface
    val expandedCellForeground = MaterialTheme.colorScheme.inverseOnSurface
    val dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12F)

    // Drag resistance — keeps visual travel to ~screenWidth/6 of finger movement
    val resistance = 1f / 6f

    // A fixed 40 dp flick is enough to commit a swipe. At resistance = 1/6,
    // this becomes ~6.7 dp in offsetX units — well within bounds for any number.
    val dragThresholdDp = 40f
    val dragThreshold = dragThresholdDp * resistance

    val dampingRatio = if (state.reduceBounciness) {
        Spring.DampingRatioLowBouncy
    } else {
        Spring.DampingRatioMediumBouncy
    }

    val rubberState = rememberRubberDragState(
        dampingRatio = dampingRatio,
        stiffness = Spring.StiffnessMedium,
        resistance = resistance,
    )

    // Single progress value driving ALL height changes — guarantees perfect sync.
    val progress by animateFloatAsState(
        targetValue = if (state.isExpanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing,
        ),
    )

    // The accordion is "live" while expanded or while the collapse is still animating.
    // Once progress returns to 0 we stop composing the expanded block entirely, so the
    // retained zero-height sub-rows / dividers can't leave a residual hairline at rest.
    val isExpansionVisible = state.isExpanded || progress > 0f

    // Remember the last valid expandedRow index so the accordion collapse knows which row
    // was the anchor (state.expandedRow resets to -1 immediately on collapse).
    var rememberedExpandedRow by remember { mutableStateOf(state.expandedRow) }
    if (state.expandedRow >= 0) {
        rememberedExpandedRow = state.expandedRow
    }

    // Remember the last non-empty expandedRows so rows still render while collapsing.
    var rememberedExpandedRows by remember { mutableStateOf(state.expandedRows) }
    if (state.expandedRows.isNotEmpty()) {
        rememberedExpandedRows = state.expandedRows
    }

    // Remember the last non-null nextAnchorRow so the bottom anchor renders while collapsing.
    var rememberedNextAnchorRow by remember { mutableStateOf(state.nextAnchorRow) }
    if (state.nextAnchorRow != null) {
        rememberedNextAnchorRow = state.nextAnchorRow
    }

    RubberDragBox(
        modifier = modifier.fillMaxSize(),
        state = rubberState,
        enabled = !state.isExpanded,
        dragThreshold = dragThreshold,
        onDragCompleted = { direction ->
            when (direction) {
                RubberDragDirection.Left -> onSwipeLeft()
                RubberDragDirection.Right -> onSwipeRight()
                else -> Unit
            }
        }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // Heights lerp'd from the single progress value:
            val collapsedRowHeight: Dp = maxHeight / 10
            val expandedRowHeight: Dp = maxHeight / 11

            // Non-anchor rows: shrink to 0 as the selected row expands.
            val nonAnchorRowHeight: Dp = lerp(collapsedRowHeight, 0.dp, progress)

            // Sub-rows & bottom anchor: grow from 0 to expandedRowHeight.
            val subRowHeight: Dp = lerp(0.dp, expandedRowHeight, progress)

            Column(modifier = Modifier.fillMaxSize()) {
                state.rows.forEachIndexed { index, rowData ->
                    val isAnchorRow = isExpansionVisible && index == rememberedExpandedRow
                    key(index) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .then(
                                    if (isAnchorRow) Modifier.weight(1f)
                                    else Modifier.height(nonAnchorRowHeight)
                                )
                                .fillMaxWidth()
                                .clipToBounds()
                                .background(collapsedCellBackground)
                                .clickable { onRowClicked(index) },
                        ) {
                            // Quantifier column (left)
                            RubberTextView(
                                modifier = Modifier.weight(1f),
                                startLabel = rowData.beforeQuantifier,
                                label = rowData.quantifier,
                                endLabel = rowData.nextQuantifier,
                                state = rubberState,
                                style = MaterialTheme.typography.displaySmall.copy(color = collapsedCellForeground)
                            )
                            // Result column (right)
                            RubberTextView(
                                modifier = Modifier.weight(1f),
                                startLabel = rowData.beforeResult,
                                label = rowData.result,
                                endLabel = rowData.nextResult,
                                state = rubberState,
                                style = MaterialTheme.typography.displaySmall.copy(color = collapsedCellForeground)
                            )
                        }
                    } // end key(index)

                    // Divider between main rows. Thickness collapses to 0.dp with the
                    // rows as the accordion opens, so it reserves no leftover height.
                    if (index < state.rows.lastIndex) {
                        HorizontalDivider(
                            thickness = lerp(1.dp, 0.dp, progress),
                            color = dividerColor,
                        )
                    }

                    // Sub-rows inserted right after the anchor row (use rememberedExpandedRow
                    // so they still render and animate during collapse).
                    if (isAnchorRow) {
                        rememberedExpandedRows.forEachIndexed { subIndex, expandedRowData ->
                            key("sub_$subIndex") {
                                if (subIndex > 0) {
                                    HorizontalDivider(
                                        thickness = lerp(0.dp, 1.dp, progress),
                                        color = dividerColor,
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(subRowHeight)
                                        .fillMaxWidth()
                                        .clipToBounds()
                                        .background(expandedCellBackground)
                                        .clickable { onRowClicked(index) }
                                ) {
                                    Text(
                                        text = expandedRowData.quantifier,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.displaySmall.copy(color = expandedCellForeground),
                                    )
                                    Text(
                                        text = expandedRowData.result,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.displaySmall.copy(color = expandedCellForeground),
                                    )
                                }
                            }
                        }

                        // Bottom anchor: rendered right after sub-rows so it stays
                        // visually adjacent during collapse (no rows growing between them).
                        val anchorRow = rememberedNextAnchorRow
                        if (anchorRow != null) {
                            HorizontalDivider(
                                thickness = lerp(0.dp, 1.dp, progress),
                                color = dividerColor,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(subRowHeight)
                                    .fillMaxWidth()
                                    .clipToBounds()
                                    .background(collapsedCellBackground)
                                    .clickable { onRowClicked(rememberedExpandedRow) }
                            ) {
                                RubberTextView(
                                    modifier = Modifier.weight(1f),
                                    startLabel = anchorRow.beforeQuantifier,
                                    label = anchorRow.quantifier,
                                    endLabel = anchorRow.nextQuantifier,
                                    state = rubberState,
                                    style = MaterialTheme.typography.displaySmall.copy(color = collapsedCellForeground)
                                )
                                RubberTextView(
                                    modifier = Modifier.weight(1f),
                                    startLabel = anchorRow.beforeResult,
                                    label = anchorRow.result,
                                    endLabel = anchorRow.nextResult,
                                    state = rubberState,
                                    style = MaterialTheme.typography.displaySmall.copy(color = collapsedCellForeground)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class RowData(
    val quantifier: String,
    val result: String,
    val beforeQuantifier: String,
    val beforeResult: String,
    val nextQuantifier: String,
    val nextResult: String,
)

data class ExpandedRowData(
    val quantifier: String,
    val result: String,
)
