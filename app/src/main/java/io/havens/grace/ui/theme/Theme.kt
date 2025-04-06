// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import io.havens.grace.R

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00B4EB),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0086B3),
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color(0xFFFFFFFF),

    tertiary = Color(0xFFFF4081),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF7B1FA2),
    onTertiaryContainer = Color(0xFFFFFFFF),

    background = Color(0xFF151517),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF151517),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF2A2C2E),
    onSurfaceVariant = Color(0xFFBDBDBD),

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFCF6679),
    onErrorContainer = Color(0xFFFFFFFF),

    outline = Color(0xFF888888),
    outlineVariant = Color(0xFF444444)
)


val CustomFontFamily = FontFamily(
    Font(R.font.balsamiqsans_regular, FontWeight.Normal),
    Font(R.font.balsamiqsans_bold, Bold)
)

val CustomTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = Bold,
        fontSize = 24.sp
    )
)

@Composable
fun GraceClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = CustomTypography,
        colorScheme = lightColorScheme(),
        content = content
    )
}
