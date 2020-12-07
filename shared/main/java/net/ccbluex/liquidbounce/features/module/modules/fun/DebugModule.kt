package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.script.api.global.Chat
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "DebugModule", description = "DebugModule for testing.", category = ModuleCategory.FUN)
class DebugModule : Module() {
    private var packetYaw = 0.0F
    private var packetPitch = 0.0F

    private val readRotationPacketsValue: BoolValue = object : BoolValue("ReadRotationPackets", false) {}

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        if (!readRotationPacketsValue.get()) return
        val packet = event.packet
        if (classProvider.isCPacketPlayerPosLook(packet) || classProvider.isCPacketPlayerLook(packet)) {
            val packetPlayer = packet.asCPacketPlayer()
            val newPacketYaw = packetPlayer.yaw
            val newPacketPitch = packetPlayer.pitch
            if (newPacketYaw != packetYaw || newPacketPitch != packetPitch) {
                Chat.print("Pitch: $newPacketPitch Yaw: $newPacketYaw")
                packetPitch = newPacketPitch
                packetYaw = newPacketYaw
            }
        }
    }

}