// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.game.module.motion

import io.havens.grace.R
import io.havens.grace.game.InterceptablePacket
import io.havens.grace.game.Module
import io.havens.grace.game.ModuleCategory
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket

class HighJumpModule(iconResId: Int = R.drawable.ic_chevron_double_up_black_24dp) : Module("High Jump", ModuleCategory.Motion, iconResId) {

    private val jumpHeight by floatValue("Height", 1.0f, 1.0f..8.0f)

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            if (packet.inputData.contains(PlayerAuthInputData.JUMP_DOWN) || packet.inputData.contains(
                    PlayerAuthInputData.VERTICAL_COLLISION
                )
            ) {
                val motionPacket = SetEntityMotionPacket().apply {
                    runtimeEntityId = session.humane.runtimeEntityId
                    motion = Vector3f.from(
                        session.humane.motionX,
                        jumpHeight,
                        session.humane.motionZ
                    )
                }
                session.clientBound(motionPacket)
            }
        }
    }
}