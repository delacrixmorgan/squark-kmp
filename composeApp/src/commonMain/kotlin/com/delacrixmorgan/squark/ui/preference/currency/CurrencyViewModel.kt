package com.delacrixmorgan.squark.ui.preference.currency

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.delacrixmorgan.squark.domain.model.Currency
import com.delacrixmorgan.squark.domain.model.result.failure
import com.delacrixmorgan.squark.domain.model.result.success
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import com.delacrixmorgan.squark.domain.usecase.GetCurrenciesUseCase
import com.delacrixmorgan.squark.domain.usecase.GetRatesUpdatedAtUseCase
import com.delacrixmorgan.squark.domain.usecase.filterByQuery
import com.delacrixmorgan.squark.nav.CurrencyTarget
import com.delacrixmorgan.squark.nav.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.time.Instant

class CurrencyViewModel(
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val getRatesUpdatedAtUseCase: GetRatesUpdatedAtUseCase,
) : ViewModel(), KoinComponent {

    private val pickerTarget = savedStateHandle.toRoute<Routes.Currency>().pickerTarget

    private val _state = MutableStateFlow(CurrencyUiState())
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    init {
        loadCurrencies()
        observeRatesUpdatedAt()
    }

    private fun observeRatesUpdatedAt() {
        viewModelScope.launch {
            getRatesUpdatedAtUseCase().collect { updatedAt ->
                _state.update { it.copy(updatedAt = updatedAt) }
            }
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            val currentCode = when (pickerTarget) {
                CurrencyTarget.Base -> preferencesRepository.getBaseCurrency()
                CurrencyTarget.Quote -> preferencesRepository.getQuoteCurrency()
            }.first()
            _state.update {
                it.copy(
                    currencyTarget = pickerTarget,
                    selectedCurrencyCode = currentCode,
                )
            }
            getCurrenciesUseCase().collect { result ->
                result.success { currencies ->
                    val selectedCurrency = currencies.firstOrNull {
                        it.code == currentCode
                    }
                    val remainingCurrencies = currencies.filterNot {
                        it.code == currentCode
                    }.sortedBy { it.name }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            selectedCurrency = selectedCurrency,
                            currencies = remainingCurrencies,
                            filteredCurrencies = remainingCurrencies.filterByQuery(it.searchQuery),
                        )
                    }
                }
                result.failure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toString()) }
                }
            }
        }
    }

    fun onAction(navHostController: NavHostController, action: CurrencyAction) {
        when (action) {
            is CurrencyAction.OnSearchModeUpdated -> {
                _state.update { it.copy(searchMode = action.searchMode) }
            }
            is CurrencyAction.OnQueryUpdated -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        filteredCurrencies = it.currencies.filterByQuery(action.query),
                    )
                }
            }
            is CurrencyAction.CurrencySelected -> viewModelScope.launch {
                when (pickerTarget) {
                    CurrencyTarget.Base -> preferencesRepository.saveBaseCurrency(action.currency.code)
                    CurrencyTarget.Quote -> preferencesRepository.saveQuoteCurrency(action.currency.code)
                }
                navHostController.navigateUp()
            }
            is CurrencyAction.OnBackClicked -> {
                navHostController.navigateUp()
            }
        }
    }
}

data class CurrencyUiState(
    val isLoading: Boolean = true,
    val currencyTarget: CurrencyTarget = CurrencyTarget.Base,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val searching: Boolean = false,
    val searchMode: Boolean = false,
    val selectedCurrencyCode: String = "",
    val selectedCurrency: Currency? = null,
    val currencies: List<Currency> = emptyList(),
    val filteredCurrencies: List<Currency> = emptyList(),
    val updatedAt: Instant? = null,
)

sealed class CurrencyAction {
    data class OnSearchModeUpdated(val searchMode: Boolean) : CurrencyAction()
    data class OnQueryUpdated(val query: String) : CurrencyAction()
    data class CurrencySelected(val currency: Currency) : CurrencyAction()
    data object OnBackClicked : CurrencyAction()
}
