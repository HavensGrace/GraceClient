// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

package io.havens.grace.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.havens.grace.notificationPopups.OverlayModuleList
import io.havens.grace.notificationPopups.OverlayNotification
import io.havens.grace.overlay.OverlayManager
import io.havens.grace.overlay.OverlayShortcutButton
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.put

abstract class Module(
    val name: String,
    val category: ModuleCategory,
    val iconResId: Int = 0,
    defaultEnabled: Boolean = false,
    val private: Boolean = false
) : InterruptiblePacketHandler,
    Configurable {

    open lateinit var session: ArticPacket

    private var _isEnabled by mutableStateOf(defaultEnabled)

    open var isEnabled: Boolean
        get() = _isEnabled
        set(value) {
            _isEnabled = value
            if (value) {
                onEnabled()
            } else {
                onDisabled()
            }
        }

    val isSessionCreated: Boolean
        get() = ::session.isInitialized

    var isExpanded by mutableStateOf(false)

    var isShortcutDisplayed by mutableStateOf(false)

    var shortcutX = 0

    var shortcutY = 100

    val overlayShortcutButton by lazy { OverlayShortcutButton(this) }

    override val values: MutableList<Value<*>> = ArrayList()

    open fun onEnabled() {
        sendToggleMessage(true)
    }

    open fun onDisabled() {
        sendToggleMessage(false)
    }

    open fun toJson() = buildJsonObject {
        put("state", isEnabled)
        put("values", buildJsonObject {
            values.forEach { value ->
                put(value.name, value.toJson())
            }
        })
        if (isShortcutDisplayed) {
            put("shortcut", buildJsonObject {
                put("x", shortcutX)
                put("y", shortcutY)
            })
        }
    }

    open fun fromJson(jsonElement: JsonElement) {
        if (jsonElement is JsonObject) {
            isEnabled = (jsonElement["state"] as? JsonPrimitive)?.boolean ?: isEnabled
            (jsonElement["values"] as? JsonObject)?.let {
                it.forEach { jsonObject ->
                    val value = getValue(jsonObject.key) ?: return@forEach
                    try {
                        value.fromJson(jsonObject.value)
                    } catch (e: Throwable) {
                        value.reset()
                    }
                }
            }
            (jsonElement["shortcut"] as? JsonObject)?.let {
                shortcutX = (it["x"] as? JsonPrimitive)?.int ?: shortcutX
                shortcutY = (it["y"] as? JsonPrimitive)?.int ?: shortcutY
                isShortcutDisplayed = true
            }
        }
    }

    private fun sendToggleMessage(enabled: Boolean) {
        if (!isSessionCreated) {
            return
        }

        // val stateText = if (enabled) "enabled".translatedSelf else "disabled".translatedSelf
        // val status = (if (enabled) "§a" else "§c") + stateText
        val moduleName = name


        // Update module list overlay
        if (enabled) {
            showModuleNotification()
            OverlayModuleList.showText(moduleName)
        } else {
            OverlayModuleList.removeText(moduleName)
        }
    }

    // Show notification for this module
    private fun showModuleNotification() {
        // Add notification for this module
        OverlayNotification.addNotification(name)

        // Check if notification overlay is already showing
        try {
            // This will attempt to create and show a new notification window
            // The window will automatically dismiss itself when all notifications are done
            OverlayManager.showOverlayWindow(OverlayNotification())
        } catch (e: Exception) {
            // If there's an error (like the window already exists), just ignore it
            // The notification was already added to the queue
        }
    }
}