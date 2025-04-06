// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.overlay

import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.havens.grace.R
import kotlin.math.min

@Suppress("KotlinConstantConditions")
class OverlayButton : OverlayWindow() {

    private val _layoutParams by lazy {
        super.layoutParams.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

            windowAnimations = android.R.style.Animation_Toast
            x = 0
            y = 100
        }
    }

    override val layoutParams: WindowManager.LayoutParams
        get() = _layoutParams

    private val overlayClickGUI by lazy { OverlayClickGUI() }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val configuration = LocalConfiguration.current
        val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        LaunchedEffect(isLandScape) {
            _layoutParams.x = min(width, _layoutParams.x)
            _layoutParams.y = min(height, _layoutParams.y)
            windowManager.updateViewLayout(composeView, _layoutParams)
        }

        val infiniteTransition = rememberInfiniteTransition()

        val offsetY by infiniteTransition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )

        ElevatedCard(
            onClick = {
                OverlayManager.showOverlayWindow(overlayClickGUI)
            },
            shape = RoundedCornerShape(36.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFa7b8d0)
            ),
            modifier = Modifier
                .padding(5.dp)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        _layoutParams.x += (dragAmount.x).toInt()
                        _layoutParams.y += (dragAmount.y).toInt()
                        windowManager.updateViewLayout(composeView, _layoutParams)
                    }
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.floating_logo),
                contentDescription = null,
                tint = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(42.dp)
                    .graphicsLayer(
                        translationY = offsetY,
                        rotationZ = rotation,
                        scaleX = 1.05f,
                        scaleY = 1.05f
                    )
                    .shadow(0.dp, RoundedCornerShape(18.dp), clip = false)
            )
        }

    }
}
