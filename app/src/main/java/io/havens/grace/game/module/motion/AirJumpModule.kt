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

class AirJumpModule(iconResId: Int = R.drawable.ic_cloud_upload_black_24dp) : Module("Air Jump", ModuleCategory.Motion, iconResId) {

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            if (packet.inputData.contains(PlayerAuthInputData.JUMP_DOWN)) {
                // Allow air jump only if the player is falling
                if (session.humane.motionY < -0.2) {
                    val motionPacket = SetEntityMotionPacket().apply {
                        runtimeEntityId = session.humane.runtimeEntityId
                        motion = Vector3f.from(
                            session.humane.motionX,
                            0.42f,  // Standard jump boost
                            session.humane.motionZ
                        )
                    }
                    session.clientBound(motionPacket)
                }
            }
        }
    }
}
