// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
package io.havens.grace.logging

import android.util.Log
import io.megumi.backend.relay.listener.MegumiPacketListener
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket
import org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEvent1Packet
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEvent2Packet
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket

@Suppress("MemberVisibilityCanBePrivate")
open class LoggingPacketHandler : MegumiPacketListener {

    var isEnabled = false

    override fun beforeClientBound(packet: BedrockPacket): Boolean {
        if (packet is PlayerAuthInputPacket) {
            return false
        }
        if (packet is InteractPacket) {
            return false
        }
        if (isEnabled) {
            Log.e("onReceivedFromClient", packet.toString())
        }
        return false
    }

    override fun beforeServerBound(packet: BedrockPacket): Boolean {
        if (packet is MoveEntityAbsolutePacket) {
            return false
        }
        if (packet is MovePlayerPacket) {
            return false
        }
        if (packet is LevelSoundEventPacket || packet is LevelSoundEvent1Packet || packet is LevelSoundEvent2Packet) {
            return false
        }
        if (packet is LevelEventPacket) {
            return false
        }
        if (packet is LevelChunkPacket) {
            return false
        }
        if (packet is AddPlayerPacket || packet is RemoveEntityPacket) {
            return false
        }
        if (isEnabled) {
            Log.e("onReceivedFromServer", packet.toString())
        }
        return false
    }

}