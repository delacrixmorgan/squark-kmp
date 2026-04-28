package com.delacrixmorgan.squark.ui.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.delacrixmorgan.squark.domain.model.Currency
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError
import com.delacrixmorgan.squark.domain.model.result.success
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import com.delacrixmorgan.squark.domain.usecase.GetConversionRateUseCase
import com.delacrixmorgan.squark.domain.usecase.GetCurrenciesUseCase
import com.delacrixmorgan.squark.nav.CurrencyTarget
import com.delacrixmorgan.squark.nav.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class TableViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val getConversionRateUseCase: GetConversionRateUseCase,
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(TableUiState())
    val state: StateFlow<TableUiState> = _state.asStateFlow()

    private var currencies: List<Currency> = emptyList()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getCurrenciesUseCase(),
                preferencesRepository.getBaseCurrency(),
                preferencesRepository.getQuoteCurrency(),
                preferencesRepository.getMultiplier(),
                preferencesRepository.getReduceBounciness(),
            ) { currenciesResult, base, quote, multiplier, reduceBounciness ->
                Preferences(currenciesResult, base, quote, multiplier, reduceBounciness)
            }.collect { (currenciesResult, base, quote, multiplier, reduceBounciness) ->
                currenciesResult.success { currencies = it }
                _state.update {
                    it.copy(
                        baseCurrency = base,
                        quoteCurrency = quote,
                        multiplier = multiplier,
                        reduceBounciness = reduceBounciness,
                    )
                }
                recalculateRows()
            }
        }
    }

    private data class Preferences(
        val currenciesResult: Result<List<Currency>, DataError>,
        val baseCurrency: String,
        val quoteCurrency: String,
        val multiplier: Double,
        val reduceBounciness: Boolean,
    )

    private fun recalculateRows() {
        val rate = getConversionRateUseCase(currencies, state.value.baseCurrency, state.value.quoteCurrency)
        if (rate != null) {
            _state.update { it.copy(conversionRate = rate) }
        }
        _state.update { current ->
            val rows = (0..9).map { index ->
                val quantifier = (index + 1) * current.multiplier
                val result = quantifier * current.conversionRate

                val beforeQuantifier = (index + 1) * (current.multiplier / 10.0)
                val beforeResult = beforeQuantifier * current.conversionRate

                val nextQuantifier = (index + 1) * (current.multiplier * 10.0)
                val nextResult = nextQuantifier * current.conversionRate

                RowData(
                    quantifier = quantifier.formatNumber(),
                    result = result.formatNumber(),
                    beforeQuantifier = beforeQuantifier.formatNumber(),
                    beforeResult = beforeResult.formatNumber(),
                    nextQuantifier = nextQuantifier.formatNumber(),
                    nextResult = nextResult.formatNumber(),
                )
            }
            val expandedRows = if (current.isExpanded) {
                (1..9).map { subIndex ->
                    val subQuantifier =
                        (current.expandedRow + 1) * current.multiplier + (current.multiplier / 10.0 * subIndex)
                    val subResult = subQuantifier * current.conversionRate
                    ExpandedRowData(
                        quantifier = subQuantifier.formatNumber(),
                        result = subResult.formatNumber(),
                    )
                }
            } else {
                emptyList()
            }
            val nextAnchorRow = if (current.isExpanded) {
                val nextIndex = current.expandedRow + 1
                val nextQuantifier = (nextIndex + 1) * current.multiplier
                val nextResult = nextQuantifier * current.conversionRate
                val beforeQuantifier = (nextIndex + 1) * (current.multiplier / 10.0)
                val beforeResult = beforeQuantifier * current.conversionRate
                val afterQuantifier = (nextIndex + 1) * (current.multiplier * 10.0)
                val afterResult = afterQuantifier * current.conversionRate
                RowData(
                    quantifier = nextQuantifier.formatNumber(),
                    result = nextResult.formatNumber(),
                    beforeQuantifier = beforeQuantifier.formatNumber(),
                    beforeResult = beforeResult.formatNumber(),
                    nextQuantifier = afterQuantifier.formatNumber(),
                    nextResult = afterResult.formatNumber(),
                )
            } else {
                null
            }
            current.copy(rows = rows, expandedRows = expandedRows, nextAnchorRow = nextAnchorRow)
        }
    }

    fun onAction(navHostController: NavHostController, action: TableAction) {
        when (action) {
            TableAction.OnBaseCurrencyClicked -> {
                navHostController.navigate(Routes.Preferences(CurrencyTarget.Base))
            }
            TableAction.OnQuoteCurrencyClicked -> {
                navHostController.navigate(Routes.Preferences(CurrencyTarget.Quote))
            }
            TableAction.OnSwapClicked -> {
                val baseCurrency = _state.value.baseCurrency
                val quoteCurrency = _state.value.quoteCurrency

                selectBaseCurrency(quoteCurrency)
                selectQuoteCurrency(baseCurrency)
            }
            TableAction.OnSwipeLeft -> {
                if (_state.value.isExpanded) return
                val current = _state.value
                val newMultiplier = if (current.multiplier > 0.1) {
                    current.multiplier / 10.0
                } else {
                    current.multiplier
                }
                _state.update { it.copy(multiplier = newMultiplier) }
                recalculateRows()
                viewModelScope.launch { preferencesRepository.saveMultiplier(newMultiplier) }
            }
            TableAction.OnSwipeRight -> {
                if (_state.value.isExpanded) return
                val current = _state.value
                val newMultiplier = if (current.multiplier < 10_000_000_000.0) {
                    current.multiplier * 10.0
                } else {
                    current.multiplier
                }
                _state.update { it.copy(multiplier = newMultiplier) }
                recalculateRows()
                viewModelScope.launch { preferencesRepository.saveMultiplier(newMultiplier) }
            }
            is TableAction.OnRowClicked -> {
                _state.update { current ->
                    if (current.isExpanded) {
                        current.copy(expandedRow = -1, expandedRows = emptyList(), nextAnchorRow = null)
                    } else {
                        val expandedRows = (1..9).map { subIndex ->
                            val subQuantifier =
                                (action.index + 1) * current.multiplier + (current.multiplier / 10.0 * subIndex)
                            val subResult = subQuantifier * current.conversionRate
                            ExpandedRowData(
                                quantifier = subQuantifier.formatNumber(),
                                result = subResult.formatNumber(),
                            )
                        }
                        val nextIndex = action.index + 1
                        val nextQuantifier = (nextIndex + 1) * current.multiplier
                        val nextResult = nextQuantifier * current.conversionRate
                        val beforeQuantifier = (nextIndex + 1) * (current.multiplier / 10.0)
                        val beforeResult = beforeQuantifier * current.conversionRate
                        val afterQuantifier = (nextIndex + 1) * (current.multiplier * 10.0)
                        val afterResult = afterQuantifier * current.conversionRate
                        val nextAnchorRow = RowData(
                            quantifier = nextQuantifier.formatNumber(),
                            result = nextResult.formatNumber(),
                            beforeQuantifier = beforeQuantifier.formatNumber(),
                            beforeResult = beforeResult.formatNumber(),
                            nextQuantifier = afterQuantifier.formatNumber(),
                            nextResult = afterResult.formatNumber(),
                        )
                        current.copy(expandedRow = action.index, expandedRows = expandedRows, nextAnchorRow = nextAnchorRow)
                    }
                }
            }
        }
    }

    private fun selectBaseCurrency(code: String) {
        _state.update { it.copy(baseCurrency = code) }
        viewModelScope.launch { preferencesRepository.saveBaseCurrency(code) }
    }

    private fun selectQuoteCurrency(code: String) {
        _state.update { it.copy(quoteCurrency = code) }
        viewModelScope.launch { preferencesRepository.saveQuoteCurrency(code) }
    }
}

data class TableUiState(
    val baseCurrency: String = "USD",
    val quoteCurrency: String = "USD",
    val multiplier: Double = 1.0,
    val conversionRate: Double = 1.0,
    val reduceBounciness: Boolean = false,
    val expandedRow: Int = -1,
    val rows: List<RowData> = emptyList(),
    val expandedRows: List<ExpandedRowData> = emptyList(),
    val nextAnchorRow: RowData? = null,
) {
    val isExpanded: Boolean get() = expandedRow >= 0
}

sealed interface TableAction {
    data object OnBaseCurrencyClicked : TableAction
    data object OnQuoteCurrencyClicked : TableAction
    data object OnSwapClicked : TableAction
    data object OnSwipeLeft : TableAction
    data object OnSwipeRight : TableAction
    data class OnRowClicked(val index: Int) : TableAction
}