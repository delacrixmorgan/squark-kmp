package com.delacrixmorgan.squark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.delacrixmorgan.squark.analytics.UserPropertyReporter
import com.delacrixmorgan.squark.nav.SquarkNavHost
import org.koin.compose.koinInject

@Composable
fun App(modifier: Modifier) {
    val userPropertyReporter = koinInject<UserPropertyReporter>()
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { userPropertyReporter.start(scope) }

    SquarkNavHost()
}
