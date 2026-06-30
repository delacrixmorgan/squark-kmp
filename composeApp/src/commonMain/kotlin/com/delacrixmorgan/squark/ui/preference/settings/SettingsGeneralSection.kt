package com.delacrixmorgan.squark.ui.preference.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Deblur
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.delacrixmorgan.squark.ui.component.ListItem
import com.delacrixmorgan.squark.ui.theme.SquarkModifiers

@Composable
internal fun Theme(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        label = "Theme",
        startContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.FormatPaint,
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
internal fun ReduceBounciness(checked: Boolean, onToggle: (Boolean) -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onToggle(!checked) },
        label = "Reduce Bounciness",
        startContent = {
            Icon(
                modifier = SquarkModifiers.iconModifier,
                imageVector = Icons.Rounded.Deblur,
                contentDescription = null
            )
        },
        endContent = {
            Switch(
                modifier = Modifier.padding(horizontal = 16.dp),
                checked = checked,
                onCheckedChange = onToggle
            )
        }
    )
}