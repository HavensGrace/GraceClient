// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.game.module.motion

import io.havens.grace.R
import io.havens.grace.game.InterceptablePacket
import io.havens.grace.game.ListItem
import io.havens.grace.game.Module
import io.havens.grace.game.ModuleCategory
import io.havens.grace.game.module.PreserveLegacyModules.MotionVarModule
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket

class SpeedModule(iconResId: Int = R.drawable.ic_run_black_24dp) : Module("Speed", ModuleCategory.Motion, iconResId) {

    // Define the speed modes
    private object SpeedModes {

        val MOTION = object : ListItem {
            override val name: String = "Motion"
        }
        val VANILLA = object : ListItem {
            override val name: String = "Vanilla"
        }

        val ALL = setOf(VANILLA, MOTION)
    }

    // Add a list value for speed modes with Vanilla as default
    private var speedMode by listValue("Mode", SpeedModes.MOTION, SpeedModes.ALL)
    private var speedValue by floatValue("Speed", 1.3f, 0.1f..5f)

    override fun onDisabled() {
        val abilities = MotionVarModule.LastUpdateAbilitiesPacket.value?.clone() ?: return

        abilities.abilityLayers[0].walkSpeed = 0.1f
        session.clientBound(abilities)
        super.onDisabled()
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }
        val packet = interceptablePacket.packet

        if (packet is PlayerAuthInputPacket) {
            when (speedMode) {
                SpeedModes.MOTION -> {
                    // Motion mode uses motion packet - useful for certain scenarios but may cause "slipping" feel
                    // Check if motion is greater than 0.0 and inputData contains VERTICAL_COLLISION
                    if (packet.motion.length() > 0.0 && packet.inputData.contains(
                            PlayerAuthInputData.VERTICAL_COLLISION
                        )
                    ) {
                        // Create and send the SetEntityMotionPacket with current player's motion
                        val motionPacket = SetEntityMotionPacket().apply {
                            runtimeEntityId = session.humane.runtimeEntityId
                            motion = Vector3f.from(
                                session.humane.motionX.toDouble() * speedValue,
                                session.humane.motionY.toDouble(),
                                session.humane.motionZ.toDouble() * speedValue
                            )
                        }

                        // Send the motion update packet
                        session.clientBound(motionPacket)
                    }
                }

                SpeedModes.VANILLA -> {
                    // Vanilla mode uses walk speed modification - more natural feel
                    val abilities =
                        MotionVarModule.LastUpdateAbilitiesPacket.value?.clone() ?: return
                    if (speedValue == abilities.abilityLayers[0].walkSpeed) return

                    abilities.abilityLayers[0].walkSpeed = speedValue
                    session.clientBound(abilities)
                }
            }
        }
    }
}