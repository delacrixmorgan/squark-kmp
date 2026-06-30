@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.delacrixmorgan.squark.ui.preference.currency

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.delacrixmorgan.squark.data.utils.toRelativeTimeString
import com.delacrixmorgan.squark.domain.model.Currency
import com.delacrixmorgan.squark.nav.CurrencyTarget
import com.delacrixmorgan.squark.ui.component.IconButton
import com.delacrixmorgan.squark.ui.component.navigationicon.NavigationBackIcon
import com.delacrixmorgan.squark.ui.theme.DefaultColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    hostInnerPadding: PaddingValues,
    state: CurrencyUiState,
    onAction: (CurrencyAction) -> Unit,
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showLastUpdated: () -> Unit = {
        val message = state.updatedAt
            ?.let { "Last updated: ${it.toRelativeTimeString()}" }
            ?: "Rates haven't been updated yet"
        scope.launch { snackbarHostState.showSnackbar(message) }
    }
    var hasShownUpdate by rememberSaveable { mutableStateOf(false) }
    var shownUpdatedAt by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(state.isLoading, state.updatedAt) {
        if (state.isLoading) return@LaunchedEffect
        val timestamp = state.updatedAt?.toEpochMilliseconds()
        if (!hasShownUpdate || timestamp != shownUpdatedAt) {
            hasShownUpdate = true
            shownUpdatedAt = timestamp
            showLastUpdated()
        }
    }
    Scaffold(
        modifier = Modifier
            .padding(bottom = hostInnerPadding.calculateBottomPadding())
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (state.searchMode) {
                SearchAppBar(state, onAction)
            } else {
                AppBar(scrollBehavior, state, onAction)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val searching = state.searching
        if (searching) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            LoadedScreen(innerPadding, state, onAction, showLastUpdated)
        }
    }
}

@Composable
private fun LoadedScreen(
    innerPadding: PaddingValues,
    state: CurrencyUiState,
    onAction: (CurrencyAction) -> Unit,
    onShowLastUpdated: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val letters by remember(state.filteredCurrencies) {
        derivedStateOf {
            state.filteredCurrencies
                .mapNotNull { it.name.firstOrNull()?.uppercaseChar() }
                .distinct()
        }
    }
    var scrolledForCode by rememberSaveable { mutableStateOf(state.selectedCurrencyCode) }
    LaunchedEffect(state.selectedCurrencyCode) {
        if (state.selectedCurrencyCode != scrolledForCode) {
            scrolledForCode = state.selectedCurrencyCode
            listState.scrollToItem(0)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
        ) {
            if (state.selectedCurrency != null && !state.searchMode) {
                stickyHeader {
                    Text(
                        "Selected Currency",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
                item {
                    CurrencyItem(
                        currency = state.selectedCurrency,
                        onClick = { onAction(CurrencyAction.CurrencySelected(state.selectedCurrency)) },
                    )
                }
            }
            if (!state.searchMode) {
                stickyHeader {
                    Text(
                        "${state.currencies.size} Available Currencies",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable { onShowLastUpdated() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
            items(
                items = state.filteredCurrencies,
                key = { it.code },
            ) { currency ->
                CurrencyItem(
                    currency = currency,
                    onClick = { onAction(CurrencyAction.CurrencySelected(currency)) },
                )
                HorizontalDivider()
            }
        }
        AlphabetScrubber(
            letters = letters,
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                )
                .align(Alignment.CenterEnd),
            onLetterSelected = { letter ->
                val index = state.filteredCurrencies.indexOfFirst {
                    it.name.firstOrNull()?.uppercaseChar() == letter
                }
                if (index >= 0) scope.launch { listState.scrollToItem(index) }
            },
        )
    }
}

@Composable
private fun CurrencyItem(
    currency: Currency,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(
            modifier = Modifier.heightIn(max = 64.dp),
            painter = painterResource(currency.getFlagAsset()),
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1F),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = currency.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (currency.code != "USD") {
                Text(
                    text = currency.getConversionLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    state: CurrencyUiState,
    onAction: (CurrencyAction) -> Unit
) {
    MediumTopAppBar(
        colors = DefaultColors.appBarColors(),
        title = {
            Text(
                when (state.currencyTarget) {
                    CurrencyTarget.Base -> "Select your base currency"
                    CurrencyTarget.Quote -> "Select your quote currency"
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = { NavigationBackIcon { onAction(CurrencyAction.OnBackClicked) } },
        actions = {
            IconButton(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                onClicked = { onAction(CurrencyAction.OnSearchModeUpdated(searchMode = true)) }
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAppBar(
    state: CurrencyUiState,
    onAction: (CurrencyAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    TopAppBar(
        colors = DefaultColors.appBarColors(),
        title = {
            TextField(
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                value = state.searchQuery,
                onValueChange = { onAction(CurrencyAction.OnQueryUpdated(it)) },
                placeholder = { Text(text = "Search") },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable { onAction(CurrencyAction.OnQueryUpdated("")) },
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null
                        )
                    }
                },
            )
        },
        navigationIcon = {
            if (state.searchQuery.isNotBlank()) {
                NavigationBackIcon { onAction(CurrencyAction.OnBackClicked) }
            } else {
                IconButton(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    onClicked = { onAction(CurrencyAction.OnSearchModeUpdated(searchMode = false)) }
                )
            }
        },
    )
    LaunchedEffect(state.searchMode) {
        if (state.searchMode) focusRequester.requestFocus()
    }
}
