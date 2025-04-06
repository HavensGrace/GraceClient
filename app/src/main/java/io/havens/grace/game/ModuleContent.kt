// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import io.havens.grace.R
import io.havens.grace.overlay.OverlayManager
import io.havens.grace.ui.component.ModuleCardX
import io.havens.grace.ui.theme.CustomFontFamily
import kotlin.math.roundToInt

private val moduleCache = HashMap<ModuleCategory, List<Module>>()

private fun fetchCachedModules(moduleCategory: ModuleCategory): List<Module> {
    val cachedModules = moduleCache[moduleCategory] ?: ModuleManager
        .modules
        .filter {
            !it.private && it.category === moduleCategory
        }
    moduleCache[moduleCategory] = cachedModules
    return cachedModules
}

@Composable
fun ModuleContent(moduleCategory: ModuleCategory, onOpenSettings: (Module) -> Unit) {
    var modules: List<Module>? by remember(moduleCategory) { mutableStateOf(moduleCache[moduleCategory]) }

    if (modules == null) {
        modules = fetchCachedModules(moduleCategory)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(modules.orEmpty().size) { index ->
            val module = modules!![index]
            ModuleCardX(
                module = module,
                onOpenSettings = { onOpenSettings(module) }
            )
        }
    }
}


@Composable
internal fun ChoiceValueContent(value: ListValue) {
    Column(
        Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
    ) {
        Text(
            value.name,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = CustomFontFamily,
            color = Color(0xFFCECECE)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(value.listItems.toList()) { item ->
                ElevatedFilterChip(
                    selected = value.value == item,
                    onClick = {
                        if (value.value != item) {
                            value.value = item
                        }
                    },
                    label = { Text(fontFamily = CustomFontFamily, text = item.name) },
                    modifier = Modifier.height(30.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFCECECE),
                        selectedContainerColor = Color(0xFF000000),
                        selectedLabelColor = Color(0xFFCECECE)
                    )
                )
            }
        }
    }
}


@Composable
internal fun ArrayChoiceValueContent(value: ArrayedListValue) {
    Column(
        Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
    ) {
        /* Text(
             value.name,
             style = MaterialTheme.typography.bodyMedium,
             color = Color(0xFFCECECE)
         ) */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            value.listItems.toList().forEach { item ->
                ElevatedFilterChip(
                    selected = value.value == item,
                    onClick = {
                        if (value.value != item) {
                            value.value = item
                        }
                    },
                    label = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                fontFamily = CustomFontFamily,
                                text = item.name,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF8B8B8B),
                        selectedContainerColor = Color(0xFF313131),
                        selectedLabelColor = Color(0xFF00D2B4)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    ColorfulSlider(
        value = value,
        onValueChange = { onValueChange(it) },
        valueRange = valueRange,
        trackHeight = 12.dp,
        thumbRadius = 6.dp,
        modifier = modifier,
        colors = MaterialSliderDefaults.materialColors(
            inactiveTrackColor = SliderBrushColor(color = Color.Transparent),
            activeTrackColor = SliderBrushColor(brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFFF85FB6), Color(0xFF8EFAFA), Color(0xFF439CFB))
            ))
        ),
        borderStroke = BorderStroke(2.dp, Color(0xFFadadad)),
        drawInactiveTrack = true
    )
}



@Composable
internal fun FloatValueContent(value: FloatValue) {
    Column(
        Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
    ) {
        Row {
            Text(
                value.name,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = CustomFontFamily,
                color = Color(0xFFCECECE)
            )
            Spacer(Modifier.weight(1f))
            Text(
                "%.1f".format(value.value),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = CustomFontFamily,
                color = Color(0xFFCECECE)
            )
        }

        CustomSlider(
            value = value.value,
            onValueChange = { newValue -> value.value = newValue },
            valueRange = value.range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IntValueContent(value: IntValue) {
    Column(
        Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
    ) {
        Row {
            Text(
                value.name,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = CustomFontFamily,
                color = Color(0xFFCECECE)
            )
            Spacer(Modifier.weight(1f))
            Text(
                value.value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = CustomFontFamily,
                color = Color(0xFFCECECE)
            )
        }
        CustomSlider(
            value = value.value.toFloat(),
            onValueChange = { newValue -> value.value = newValue.roundToInt() },
            valueRange = value.range.toFloatRange(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun BoolValueContent(value: BoolValue) {
    Row(
        Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
            .toggleable(
                value = value.value,
                interactionSource = null,
                indication = null,
                onValueChange = {
                    value.value = it
                }
            )
    ) {
        Text(
            value.name,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = CustomFontFamily,
            color = Color(0xFFCECECE)
        )
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = value.value,
            onCheckedChange = null,
            modifier = Modifier
                .padding(0.dp),
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color(0xFFCECECE),
                checkedColor = Color(0xFFCECECE),
                checkmarkColor = Color(0xFFCECECE)
            )
        )
    }
}

@Composable
internal fun ShortcutContent(module: Module) {
    Row(
        Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .toggleable(
                value = module.isShortcutDisplayed,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onValueChange = {
                    module.isShortcutDisplayed = it
                    if (it) {
                        OverlayManager.showOverlayWindow(module.overlayShortcutButton)
                    } else {
                        OverlayManager.dismissOverlayWindow(module.overlayShortcutButton)
                    }
                }
            )
    ) {
        Text(
            stringResource(R.string.tb_shortcut),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = CustomFontFamily,
            color = Color(0xFFCECECE)
        )
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = module.isShortcutDisplayed,
            onCheckedChange = null,
            modifier = Modifier
                .padding(0.dp),
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color(0xFFCECECE),
                checkedColor = Color(0xFFCECECE),
                checkmarkColor = Color(0xFFCECECE)
            )
        )
    }
}

private fun IntRange.toFloatRange() = first.toFloat()..last.toFloat()