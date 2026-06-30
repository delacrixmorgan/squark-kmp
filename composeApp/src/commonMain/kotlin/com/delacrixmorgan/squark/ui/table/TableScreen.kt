package com.delacrixmorgan.squark.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TableScreen(
    state: TableUiState,
    onAction: (TableAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        CurrencyHeader(
            baseCurrency = state.baseCurrency,
            quoteCurrency = state.quoteCurrency,
            onBaseCurrencyClick = { onAction(TableAction.OnBaseCurrencyClicked) },
            onQuoteCurrencyClick = { onAction(TableAction.OnQuoteCurrencyClicked) },
            onSwapClick = { onAction(TableAction.OnSwapClicked) }
        )
        CurrencyTable(
            modifier = Modifier.weight(1F),
            state = state,
            onSwipeLeft = { onAction(TableAction.OnSwipeLeft) },
            onSwipeRight = { onAction(TableAction.OnSwipeRight) },
            onRowClicked = { onAction(TableAction.OnRowClicked(it)) },
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun CurrencyHeader(
    baseCurrency: String,
    quoteCurrency: String,
    onBaseCurrencyClick: () -> Unit,
    onQuoteCurrencyClick: () -> Unit,
    onSwapClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = onBaseCurrencyClick,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = baseCurrency,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Button(
                onClick = onQuoteCurrencyClick,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = quoteCurrency,
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
        // Swap button
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.inversePrimary)
                .clickable { onSwapClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.rotate(90F),
                imageVector = Icons.Rounded.SwapVert,
                contentDescription = "Swap",
            )
        }
    }
}
