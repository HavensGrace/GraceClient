// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.overlay

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class OverlayLifecycleOwner : SavedStateRegistryOwner {

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle = LifecycleRegistry(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    fun setCurrentState(state: Lifecycle.State) {
        lifecycle.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycle.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        savedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        savedStateRegistryController.performSave(outBundle)
    }

}