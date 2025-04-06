// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import android.view.inputmethod.InputMethodManager

@Composable
fun getDialogWindow(): Window? {
    return (LocalView.current.parent as? DialogWindowProvider)?.window
}

@Composable
fun getActivityWindow(): Window? {
    return LocalView.current.context.getActivityWindow()
}

fun showSoftKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun windowFullScreen(
    activityWindow: Window?,
    dialogWindow: Window?,
) {
    if (activityWindow != null && dialogWindow != null) {
        val attributes = WindowManager.LayoutParams()
        attributes.copyFrom(activityWindow.attributes)
        attributes.type = dialogWindow.attributes.type

        dialogWindow.attributes = attributes
        dialogWindow.setLayout(
            activityWindow.decorView.width,
            activityWindow.decorView.height
        )
    }
}

private tailrec fun Context.getActivityWindow(): Window? = when (this) {
    is Activity -> window
    is ContextWrapper -> baseContext.getActivityWindow()
    else -> null
}