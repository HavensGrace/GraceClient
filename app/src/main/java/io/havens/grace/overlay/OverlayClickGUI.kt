// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.overlay

import AnimatedContentX
import ElevatedCardX
import io.havens.grace.ui.component.ModuleSettingsScreen
import NavigationRailItemX
import NavigationRailX
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarDefaults.windowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.havens.grace.R
import io.havens.grace.game.Module
import io.havens.grace.game.ModuleCategory
import io.havens.grace.game.ModuleContent


class OverlayClickGUI : OverlayWindow() {

    private val _layoutParams by lazy {
        super.layoutParams.apply {
            flags =
                flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            if (Build.VERSION.SDK_INT >= 31) {
                blurBehindRadius = 15
            }

            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

            dimAmount = 0.4f
            windowAnimations = android.R.style.Animation_Dialog
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    override val layoutParams: WindowManager.LayoutParams
        get() = _layoutParams

    private var selectedModuleCategory by mutableStateOf(ModuleCategory.Combat)
    private var selectedModule by mutableStateOf<Module?>(null) // ✅ Track selected module
    private var isSettingsOpen by mutableStateOf(false) // ✅ Track if settings are open

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Box(
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    OverlayManager.dismissOverlayWindow(this)
                },

            contentAlignment = Alignment.Center
        ) {
            // ✅ Main UI (ElevatedCardX)
            ElevatedCardX {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {


                    val gradientBrush = remember {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6B48FF), Color(0xFF00DDEB)),
                            start = Offset.Zero,
                            end = Offset(100f, 100f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(70.dp)
                            .windowInsetsPadding(windowInsets),
                        contentAlignment = Alignment.Center // Centers both horizontally & vertically
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            NavigationRailX(
                                windowInsets = WindowInsets(0, 0, 0, 0),
                                modifier = Modifier
                                    //.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "LOGO",
                                    tint = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .scale(0.8f, 0.8f)
                                )
                                ModuleCategory.entries.fastForEach { moduleCategory ->
                                    moduleCategory.iconResId?.let {
                                        moduleCategory.labelResId?.let { it1 ->
                                            NavigationRailItemX(
                                                selected = selectedModuleCategory == moduleCategory,
                                                onClick = {
                                                    if (selectedModuleCategory !== moduleCategory) {
                                                        selectedModuleCategory = moduleCategory
                                                    }
                                                },
                                                iconResId = it,
                                                labelResId = it1,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                    }
                                }

                            }
                        }

                    }
                    AnimatedContentX(
                        targetState = selectedModuleCategory,
                        modifier = Modifier
                            // .padding(20.dp) // Adds padding
                            .fillMaxSize() // Ensures it takes available space
                    ) { moduleCategory ->
                        ModuleContent(
                            moduleCategory,
                            onOpenSettings = { module ->
                                selectedModule = module
                                isSettingsOpen = true // ✅ Open settings
                            }
                        )
                    }

                }
            }


            // ✅ Show Settings Screen Above Everything When Open
            if (isSettingsOpen && selectedModule != null) {

                ModuleSettingsScreen(


                    module = selectedModule!!,
                    onDismiss = { isSettingsOpen = false }
                )
            }
        }
    }

    @Composable
    private fun ConfigCategoryContent() {
    }
}
