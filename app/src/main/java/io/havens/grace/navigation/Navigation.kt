// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.havens.grace.router.main.MainScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavHostController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Destinations.MainScreen.name
        ) {
            composable(Destinations.MainScreen.name) {
                MainScreen()
            }
        }
    }
}

val LocalNavHostController =
    staticCompositionLocalOf<NavHostController> { error("LocalNavHostController is not presented") }

enum class Destinations {
    MainScreen
}