// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarHostState =
    staticCompositionLocalOf<SnackbarHostState> { error("LocalSnackBarHostState is not presented.") }

@Composable
inline fun SnackbarHostStateScope(crossinline content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        content()
    }
}