// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.game

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.havens.grace.R

enum class ModuleCategory(
    @DrawableRes val iconResId: Int?,
    @StringRes val labelResId: Int?
) {
    Config(
        iconResId = null,
        labelResId = null
    ),
    Combat(
        iconResId = R.drawable.ic_sword_black_24dp,
        labelResId = R.string.module_pvp
    ),
    Motion(
        iconResId = R.drawable.ic_ghost_black_24dp,
        labelResId = R.string.module_movement
    ),
    Visual(
        iconResId = R.drawable.ic_cube_scan_black_24dp,
        labelResId = R.string.module_render
    ),

    Misc(
        iconResId = R.drawable.ic_dice_3_outline_black_24dp,
        labelResId = R.string.module_utils
    )

}