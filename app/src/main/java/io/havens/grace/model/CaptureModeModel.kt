// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.model

import android.content.SharedPreferences
import androidx.core.content.edit

data class CaptureModeModel(
    val serverHostName: String,
    val serverPort: Int
) {

    companion object {

        fun from(sharedPreferences: SharedPreferences): CaptureModeModel {
            val serverHostName = sharedPreferences.getString(
                "capture_mode_model_server_host_name",
                "play.lbsg.net"
            )!!
            val serverPort = sharedPreferences.getInt(
                "capture_mode_model_server_port",
                19132
            )
            return CaptureModeModel(serverHostName, serverPort)
        }

    }

    fun to(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            putString(
                "capture_mode_model_server_host_name",
                serverHostName
            )
            putInt(
                "capture_mode_model_server_port",
                serverPort
            )
        }
    }
}