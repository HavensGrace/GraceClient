// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.game.module.motion

import io.havens.grace.R
import io.havens.grace.game.InterceptablePacket
import io.havens.grace.game.Module
import io.havens.grace.game.ModuleCategory
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket
import kotlin.math.cos
import kotlin.math.sin

class JetPackModule(iconResId: Int = R.drawable.baseline_backpack_24) : Module("Jetpack", ModuleCategory.Motion, iconResId) {

    private val speed by floatValue("Speed", 2.5f, 1.0f..10.0f)

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            // Convert angles to radians
            val yaw = Math.toRadians(packet.rotation.y.toDouble())
            val pitch = Math.toRadians(packet.rotation.x.toDouble())

            // Calculate direction vector based on where player is looking
            val motionX = -sin(yaw) * cos(pitch) * speed
            val motionY = -sin(pitch) * speed
            val motionZ = cos(yaw) * cos(pitch) * speed

            val motionPacket = SetEntityMotionPacket().apply {
                runtimeEntityId = session.humane.runtimeEntityId
                motion = Vector3f.from(
                    motionX.toFloat(),
                    motionY.toFloat(),
                    motionZ.toFloat()
                )
            }
            session.clientBound(motionPacket)
        }
    }
}