// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

package io.havens.grace.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.havens.grace.R
import io.havens.grace.ui.theme.CustomFontFamily
import io.havens.grace.ui.theme.GraceClientTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandlerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val message = intent?.getStringExtra("message") ?: return finish()

        setContent {
            GraceClientTheme {
                CrashLogScreen(
                    crashLog = message,
                    onDiscordClick = {
                        // Open Discord support link or app
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://discord.gg/GSW8wgdyNX")
                            )
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Couldn't open Discord", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onRestartClick = {
                        // Restart the main application
                        val intent = packageManager.getLaunchIntentForPackage(packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    },
                    onSaveLogcatClick = {
                        // Save logcat to Android/data directory
                        saveLogcatToFile(message)
                    }
                )

                BackHandler {
                    Toast.makeText(this, getString(R.string.tb_fallback), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveLogcatToFile(logContent: String) {
        try {
            // Get the external files directory in Android/data
            val directory = getExternalFilesDir(null)
            if (directory != null) {
                // Create a timestamped filename
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "crash_log_$timestamp.txt"
                val file = File(directory, filename)

                // Write the log content to the file
                FileOutputStream(file).use { out ->
                    out.write(logContent.toByteArray())
                }

                Toast.makeText(
                    this,
                    "Log saved to: ${file.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Couldn't access external storage", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save log: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun CrashLogScreen(
    crashLog: String,
    onDiscordClick: () -> Unit,
    onRestartClick: () -> Unit,
    onSaveLogcatClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // App name or title at the top
            Text(
                text = "Android Compact - 1",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = CustomFontFamily,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Crash Log Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F5FF)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Tab-like header for the "Crash Log" button
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp)
                    ) {
                        Button(
                            onClick = { /* Already on crash log screen */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6750A4)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Crash Log",
                                color = Color.White,
                                fontFamily = CustomFontFamily
                            )
                        }
                    }

                    // Crash log content
                    SelectionContainer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = crashLog,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = CustomFontFamily,
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }

            // Bottom row of action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = "Discord",
                    onClick = onDiscordClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                ActionButton(
                    text = "Restart",
                    onClick = onRestartClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                ActionButton(
                    text = "Save Logcat",
                    onClick = onSaveLogcatClick,
                    modifier = Modifier.weight(1f)
                )
            }

            // Time display in the corner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "9:30",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = CustomFontFamily,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEEE8F4)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Pencil-like icon placeholder
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
                    .background(Color(0xFF6750A4), RoundedCornerShape(2.dp))
            )

            Text(
                text = text,
                color = Color(0xFF6750A4),
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Medium
            )
        }
    }
}