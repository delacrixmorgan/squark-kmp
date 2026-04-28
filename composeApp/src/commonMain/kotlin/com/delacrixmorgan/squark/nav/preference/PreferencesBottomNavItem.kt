package com.delacrixmorgan.squark.nav.preference

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.delacrixmorgan.squark.nav.Routes

enum class PreferencesBottomNavItem(
    val title: String,
    val route: Routes,
    val icon: ImageVector,
) {
    Currency(
        "Currency",
        Routes.Currency(),
        Icons.Rounded.Flag
    ),
    Settings(
        "Settings",
        Routes.Settings,
        Icons.Rounded.Settings
    ),
}