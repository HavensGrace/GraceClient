// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.overlay

import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.havens.grace.game.Module
import io.havens.grace.ui.theme.CustomFontFamily
import kotlin.math.cos
import kotlin.math.sin

class OverlayShortcutButton(
    private val module: Module
) : OverlayWindow() {

    private val _layoutParams by lazy {
        super.layoutParams.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            windowAnimations = android.R.style.Animation_Toast
            x = module.shortcutX
            y = module.shortcutY
        }
    }

    override val layoutParams: WindowManager.LayoutParams
        get() = _layoutParams


    @Composable
    override fun Content() {
        val context = LocalContext.current
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        val buttonSizePx = with(LocalDensity.current) { 56.dp.roundToPx() }

        val baseColors = listOf(Color(0xFF439CFB), Color(0xFF8EFAFA), Color(0xFFF85FB6))
        val shuffledColors = remember { baseColors.shuffled() }

        val infiniteTransition = rememberInfiniteTransition()
        val gradientOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        val angleRad = Math.toRadians(45.0)
        val x = cos(angleRad).toFloat() * 100f
        val y = sin(angleRad).toFloat() * 100f

        val gradientBrush = Brush.linearGradient(
            colors = shuffledColors,
            start = Offset(gradientOffset, gradientOffset),
            end = Offset(gradientOffset + x, gradientOffset + y)
        )

        val density = LocalDensity.current

        LaunchedEffect(isLandscape) {
            val buttonSizePx = with(density) { 56.dp.roundToPx() }
            _layoutParams.x = _layoutParams.x.coerceIn(0, width - buttonSizePx)
            _layoutParams.y = _layoutParams.y.coerceIn(0, height - buttonSizePx)
            windowManager.updateViewLayout(composeView, _layoutParams)
            updateShortcut()
        }


        Box(
            modifier = Modifier
                .padding(5.dp)
                .size(56.dp)
                .then(
                    if (module.isEnabled) Modifier.border(1.dp, gradientBrush, CircleShape)
                    else Modifier
                )
        ) {
            ElevatedCard(
                onClick = { module.isEnabled = !module.isEnabled },
                shape = CircleShape,
                colors = CardDefaults.elevatedCardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            _layoutParams.x = (_layoutParams.x + dragAmount.x.toInt()).coerceIn(0, width - buttonSizePx)
                            _layoutParams.y = (_layoutParams.y + dragAmount.y.toInt()).coerceIn(0, height - buttonSizePx)
                            windowManager.updateViewLayout(composeView, _layoutParams)
                            updateShortcut()
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (module.iconResId != 0) {
                        Image(
                            painter = painterResource(id = module.iconResId),
                            contentDescription = module.name,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = module.name.split(" ").joinToString("\n"),
                            style = MaterialTheme.typography.bodySmall.copy(brush = gradientBrush),
                            fontSize = 12.sp,
                            fontFamily = CustomFontFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }

    private fun updateShortcut() {
        module.shortcutX = _layoutParams.x
        module.shortcutY = _layoutParams.y
    }
}
