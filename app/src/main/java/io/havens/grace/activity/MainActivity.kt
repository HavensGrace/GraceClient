// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

package io.havens.grace.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import io.havens.grace.game.ModuleManager
import io.havens.grace.navigation.Navigation
import io.havens.grace.ui.theme.GraceClientTheme
import io.havens.grace.util.Zetalogger
import io.havens.grace.util.PacketJumper

class MainActivity : ComponentActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var saveFileLauncher: ActivityResultLauncher<Intent>

    @OptIn(ExperimentalFoundationApi::class)
    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val antiLogcat = Zetalogger()
        antiLogcat.startLogging()


        // Initialize the launchers
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        ModuleManager.handleConfigFileResult(uri)
                    }
                }
            }

        saveFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        ModuleManager.saveCustomConfig(
                            this
                        )

                    }
                }
            }
//kill Check Disabled for dev builds
        /*val airavv = PacketJumper()
        airavv.v2H8M(this)*/
        enableEdgeToEdge()
        setContent {
            GraceClientTheme {
                Navigation()
            }
        }
    }

    // Public methods to launch file pickers
    fun launchConfigFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain"))
        }
        filePickerLauncher.launch(intent)
    }

    fun launchSaveConfigPicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "grace_config.json")
        }
        saveFileLauncher.launch(intent)
    }
}
