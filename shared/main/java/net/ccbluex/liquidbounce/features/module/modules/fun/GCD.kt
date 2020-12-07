package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "GCD", description = "Module for testing.", category = ModuleCategory.FUN)
class GCD : Module() {
    val gcdDividerValue: IntegerValue = object : IntegerValue("gcdDivivder", 1, 1, 10) {
        override fun onChanged(oldValue: Int, newValue: Int) {
        }
    }
    val sensitivityDividerValue: IntegerValue = object : IntegerValue("sensitivityDivider", 1, 1, 10) {
        override fun onChanged(oldValue: Int, newValue: Int) {
        }
    }

    val enableRoundingValue: BoolValue = object : BoolValue("Rounding", false) {}
    val roundingToValue: IntegerValue = object : IntegerValue("RoundingTo", 5, 0, 10) {
        override fun onChanged(oldValue: Int, newValue: Int) {
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
    }
}