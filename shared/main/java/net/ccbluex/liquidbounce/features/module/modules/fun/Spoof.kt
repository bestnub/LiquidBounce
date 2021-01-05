package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(
    name = "Spoof",
    description = "Spoofs shit hotbar related",
    category = ModuleCategory.FUN
)

class Spoof: Module() {

    private val allowValue = BoolValue("Allow", false)
    private val allowValue2 = BoolValue("Allow2", false)
    private val allowValue3 = BoolValue("Allow3", false)
    private val allowValue4 = BoolValue("Allow4", false)
    private val timerValue = IntegerValue("Ticks", 0, 0, 20)
    private val debugValue = BoolValue("Debug", false)
    private val debugTicks = IntegerValue("Debug-Ticks", 0, 0, 20)

    private var allowTick = TickTimer()
    private var debugTick= TickTimer()
    private val blockSlot = InventoryUtils.findAutoBlockBlock()
    private var slot = 0

    override fun onEnable() {
        slot = mc.thePlayer!!.inventory.currentItem
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (blockSlot == -1)
            return

        if(debugValue.get()) {
            debugTick.update()
            if(debugTick.hasTimePassed(debugTicks.get())) {
                ClientUtils.displayChatMessage("Block slot: $blockSlot")
                debugTick.reset()
            }
        } else return
        if(allowValue.get()) {
            if(mc.thePlayer!!.inventory.currentItem != slot) {
                mc.thePlayer!!.inventory.currentItem = slot
            } else return
        } else return
        if(allowValue2.get()) {
            if(blockSlot - 36 != slot) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(blockSlot - 36))
            } else return
        } else return
        if(allowValue3.get()) {
            if(blockSlot >= 0) {
                mc.thePlayer!!.inventory.currentItem = blockSlot - 36
                mc.playerController.updateController()
            } else return
        } else return
        if(allowValue4.get()) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(blockSlot - 36))
        } else return

        if(mc.gameSettings.keyBindUseItem.pressed) {
            if(allowValue4.get()) {
                allowTick.update()
                if(allowTick.hasTimePassed(timerValue.get())) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(slot))
                    allowTick.reset()
                }
            } else return
        }
    }
}