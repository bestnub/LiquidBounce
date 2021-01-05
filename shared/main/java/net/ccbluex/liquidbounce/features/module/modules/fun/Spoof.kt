package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.FastPlace
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import kotlin.math.floor

@ModuleInfo(
    name = "Spoof",
    description = "Spoofs shit hotbar related",
    category = ModuleCategory.FUN
)

class Spoof: Module() {

    private var hotBar = IntegerValue("Slot", 9, 1, 9)
    private var onUse = BoolValue("OnUse", false)
    private var swing = BoolValue("Swing", true)
    private var randomSlot = BoolValue("RandomSlot", false)
    var delay = IntegerValue("Delay", 10, 0, 50)

    private var tick = 0
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        tick++
        if(((mc.gameSettings.keyBindUseItem.pressed && onUse.get()) || !onUse.get())) {
            if(swing.get() && mc.gameSettings.keyBindUseItem.pressed) {
                mc.thePlayer!!.swingItem()
            }
            if(!randomSlot.get()) {
                if(tick >= delay.get()) {
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketHeldItemChange(hotBar.get() - 1))
                    tick = 0
                }
            } else {
                if(tick >= delay.get()) {
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketHeldItemChange(randomIntFrom(0,8).toInt()))
                    tick = 0
                }
            }
        }
    }

    override fun onDisable() {
        mc.rightClickDelayTimer = 4
    }

    override fun onEnable() {
        mc.rightClickDelayTimer = FastPlace().speedValue.get()
    }
    private fun randomIntFrom(min: Int, max: Int): Double// Get a random integer from [min] to [max]
    {
        return floor(Math.random()*(max-min+1)+min)
    }
}