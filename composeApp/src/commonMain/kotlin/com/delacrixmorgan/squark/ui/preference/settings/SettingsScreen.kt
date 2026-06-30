package com.delacrixmorgan.squark.ui.preference.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.delacrixmorgan.squark.domain.model.ThemePreference
import com.delacrixmorgan.squark.nav.NavEffect
import com.delacrixmorgan.squark.nav.NavEffectHandler
import com.delacrixmorgan.squark.ui.component.ListView
import com.delacrixmorgan.squark.ui.component.RadioGroupBottomSheet
import com.delacrixmorgan.squark.ui.component.RadioRowData
import com.delacrixmorgan.squark.ui.theme.AppTypography
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import squark.composeapp.generated.resources.Res
import squark.composeapp.generated.resources.app_name
import squark.composeapp.generated.resources.ic_launcher_foreground

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    state: SettingsUiState,
    effects: Flow<NavEffect>,
    onAction: (SettingsAction) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Settings", style = AppTypography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            ListView(
                data = listOf(
                    { Theme { onAction(SettingsAction.ToggleThemeVisibility(show = true)) } },
                    { ReduceBounciness(checked = state.reduceBounciness) { onAction(SettingsAction.OnReduceBouncinessToggled(it)) } },
                ),
                divider = { HorizontalDivider() }
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            ListView(
                data = listOf(
                    { AppInfo { onAction(SettingsAction.OpenAppInfo) } },
                    { PrivacyPolicy { onAction(SettingsAction.OpenPrivacyPolicy) } },
                    { SendFeedback { onAction(SettingsAction.OpenSendFeedback) } },
                    { RateUs { onAction(SettingsAction.OpenRateUs) } },
                ),
                divider = { HorizontalDivider() }
            )
        }
        Spacer(Modifier.height(32.dp))

        Image(painter = painterResource(Res.drawable.ic_launcher_foreground), "Squark Logo")

        Spacer(Modifier.height(8.dp))

        Text(
            stringResource(Res.string.app_name) + " " + state.version,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(8.dp))
    }

    val themeBottomSheetState = rememberModalBottomSheetState(initialDetent = Hidden)
    RadioGroupBottomSheet(
        sheetState = themeBottomSheetState,
        title = "Theme",
        selectedIndex = state.theme.ordinal,
        items = ThemePreference.entries.map { RadioRowData(id = it.name, label = it.label) },
        onSelected = { selectedItem ->
            onAction(SettingsAction.OnThemeSelected(ThemePreference.entries.first { it.name == selectedItem.id }))
        },
        onDismissed = { onAction(SettingsAction.ToggleThemeVisibility(show = false)) }
    )

    LaunchedEffect(state.showTheme, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            themeBottomSheetState.targetDetent = if (state.showTheme) FullyExpanded else Hidden
        }
    }

    NavEffectHandler(effects)
}