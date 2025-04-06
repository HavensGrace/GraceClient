// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.game

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import io.havens.grace.activity.MainActivity
import io.havens.grace.application.AppContext
import io.havens.grace.game.module.PreserveLegacyModules.PacketLoggerModule
import io.havens.grace.game.module.combat.AdvancedCombatSystemModule
import io.havens.grace.game.module.combat.HitboxModule
import io.havens.grace.game.module.combat.KillauraModule
import io.havens.grace.game.module.combat.QuickAttackModule
import io.havens.grace.game.module.combat.TPAuraModule
import io.havens.grace.game.module.combat.VelocityModule
import io.havens.grace.game.module.misc.ArrayListModule
import io.havens.grace.game.module.misc.ChatCommandModule
import io.havens.grace.game.module.misc.DesyncModule
import io.havens.grace.game.module.misc.ESPModule
import io.havens.grace.game.module.misc.McIpModule
import io.havens.grace.game.module.misc.MiniMapModule
import io.havens.grace.game.module.misc.SessionInfoModule
import io.havens.grace.game.module.misc.SpeedometerModule
import io.havens.grace.game.module.misc.TracersModule
import io.havens.grace.game.module.misc.WatermarkModule
import io.havens.grace.game.module.motion.AirJumpModule
import io.havens.grace.game.module.motion.BhopModule
import io.havens.grace.game.module.motion.DamageBoostModule
import io.havens.grace.game.module.motion.FullStopModule
import io.havens.grace.game.module.motion.GlideModule
import io.havens.grace.game.module.motion.HighJumpModule
import io.havens.grace.game.module.motion.JetPackModule
import io.havens.grace.game.module.motion.JitterFlyModule
import io.havens.grace.game.module.motion.LongJumpModule
import io.havens.grace.game.module.motion.NoClipModule
import io.havens.grace.game.module.motion.OrbitStrafeModule
import io.havens.grace.game.module.motion.PhaseModule
import io.havens.grace.game.module.motion.SpeedModule
import io.havens.grace.game.module.motion.SpiderModule
import io.havens.grace.game.module.motion.StrafeModule
import io.havens.grace.game.module.motion.TargetStrafeModule
import io.havens.grace.game.module.motion.VanillaFlyModule
import io.havens.grace.game.module.visual.FovModule
import io.havens.grace.game.module.visual.NoHurtCameraModule
import io.havens.grace.game.module.visual.WeatherEventModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.InputStreamReader

object ModuleManager {

    private val _modules: MutableList<Module> = ArrayList()

    val modules: List<Module> = _modules

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        with(_modules) {

            //COMBAT
            add(KillauraModule())
            add(TPAuraModule())
            add(HitboxModule())
            add(VelocityModule())
            add(QuickAttackModule())
            add(AdvancedCombatSystemModule())


            //Movement
            add(AirJumpModule())
            add(BhopModule())
            add(VanillaFlyModule())
            add(HighJumpModule())
            add(JetPackModule())
            add(NoClipModule())
            add(TargetStrafeModule())
            add(OrbitStrafeModule())
            add(StrafeModule())
            add(SpeedModule())
            add(PhaseModule())
            add(SpiderModule())
            add(FullStopModule())
            add(DamageBoostModule())
            add(LongJumpModule())
            add(GlideModule())
            add(JitterFlyModule())

            //Visual
            add(FovModule())
            add(NoHurtCameraModule())
            add(WeatherEventModule())

            //Misc
            add(WatermarkModule())
            add(ArrayListModule())
            add(DesyncModule())
            add(SessionInfoModule())
            add(MiniMapModule())
            add(PacketLoggerModule())
            add(SpeedometerModule())
            add(TracersModule())
            add(McIpModule())
            add(ESPModule())
            add(ChatCommandModule())



        }
    }

    fun getModule(name: String): Module? {
        return _modules.find { it.name.equals(name, ignoreCase = true) }
    }

    fun saveConfig() {
        val configsDir = AppContext.instance.getExternalFilesDir(null)?.resolve("configs")
            ?: return

        configsDir.mkdirs()


        val config = configsDir.resolve("Grace.json")
        val jsonObject = buildJsonObject {
            put("modules", buildJsonObject {
                _modules.forEach {
                    if (it.private) {
                        return@forEach
                    }
                    put(it.name, it.toJson())
                }
            })
        }

        config.writeText(json.encodeToString(jsonObject))
    }

    fun loadConfig() {
        val configsDir = AppContext.instance.getExternalFilesDir(null)?.resolve("configs")
            ?: return

        configsDir.mkdirs()

        val config = configsDir.resolve("Grace.json")
        if (!config.exists()) {
            return
        }

        val jsonString = config.readText()
        if (jsonString.isEmpty()) {
            return
        }

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val modules = jsonObject["modules"]!!.jsonObject
        _modules.forEach { module ->
            (modules[module.name] as? JsonObject)?.let {
                module.fromJson(it)
            }
        }
    }

    /**
     * Loads a custom configuration from a user-selected file
     * Can be called from any Activity or Fragment
     *
     * @param activity The activity from which this function is called
     * @param onSuccess Callback for successful config load
     * @param onFailure Callback with error message on failure
     */
    fun loadCustomConfig(activity: MainActivity) {
        activity.launchConfigFilePicker()
    }


    /**
     * Alternative method for legacy activities that don't use ComponentActivity
     */
    fun startConfigFilePickerIntent(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain"))
        }
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Processes a selected config file URI from startActivityForResult
     * For use with the legacy activity method
     */
    fun handleConfigFileResult(
        uri: Uri,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        processConfigFile(uri, onSuccess, onFailure)
    }

    private fun launchFilePicker(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain"))
        }
        launcher.launch(intent)
    }

    private fun processConfigFile(uri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val contentResolver = AppContext.instance.contentResolver

        contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()

            if (jsonString.isEmpty()) {
                onFailure("Config file is empty")
                Toast.makeText(AppContext.instance, "Config file is empty", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            try {
                val jsonObject = json.parseToJsonElement(jsonString).jsonObject
                val modules = jsonObject["modules"]?.jsonObject

                if (modules == null) {
                    onFailure("Invalid config format: missing 'modules' section")
                    Toast.makeText(AppContext.instance, "Invalid config format", Toast.LENGTH_SHORT)
                        .show()
                    return
                }

                var appliedModules = 0
                _modules.forEach { module ->
                    if (!module.private) {
                        (modules[module.name] as? JsonObject)?.let {
                            module.fromJson(it)
                            appliedModules++
                        }
                    }
                }

                Toast.makeText(
                    AppContext.instance,
                    "Config loaded: $appliedModules modules configured",
                    Toast.LENGTH_SHORT
                ).show()
                onSuccess()

            } catch (e: Exception) {
                onFailure("Failed to parse config: ${e.message}")
                Toast.makeText(
                    AppContext.instance,
                    "Invalid config format: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } ?: run {
            onFailure("Could not open the selected file")
            Toast.makeText(
                AppContext.instance,
                "Could not open the selected file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun saveCustomConfig(activity: MainActivity) {
        activity.launchSaveConfigPicker()
    }


    fun saveConfigToUri(uri: Uri, onSuccess: (Uri) -> Unit, onFailure: (String) -> Unit) {
        val contentResolver = AppContext.instance.contentResolver

        contentResolver.openOutputStream(uri)?.use { outputStream ->
            // Create the JSON configuration
            val configJson = buildJsonObject {
                put("modules", buildJsonObject {
                    _modules.forEach {
                        if (!it.private) {
                            put(it.name, it.toJson())
                        }
                    }
                })
            }

            val jsonString = json.encodeToString(configJson)
            outputStream.write(jsonString.toByteArray())
            outputStream.flush()

            Toast.makeText(AppContext.instance, "Config saved successfully", Toast.LENGTH_SHORT)
                .show()
            onSuccess(uri)
        } ?: run {
            onFailure("Could not write to the selected file")
            Toast.makeText(
                AppContext.instance,
                "Could not write to the selected file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun bindSessionToModules(session: ArticPacket) {
        modules.forEach { module ->
            module.session = session
        }
    }

}