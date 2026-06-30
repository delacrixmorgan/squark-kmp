package com.delacrixmorgan.squark.ui.preference.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.delacrixmorgan.squark.ui.component.ListItem
import com.delacrixmorgan.squark.ui.theme.SquarkModifiers

@Composable
internal fun AppInfo(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        label = "App Info",
        startContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.Info,
                contentDescription = null
            )
        },
        endContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    )
}

@Composable
internal fun PrivacyPolicy(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        label = "Privacy Policy",
        startContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.Policy,
                contentDescription = null
            )
        },
        endContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    )
}

@Composable
internal fun SendFeedback(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        label = "Send Feedback",
        startContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.Feedback,
                contentDescription = null
            )
        },
        endContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    )
}

@Composable
internal fun RateUs(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        label = "Rate Us",
        startContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.ThumbUp,
                contentDescription = null
            )
        },
        endContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    )
}