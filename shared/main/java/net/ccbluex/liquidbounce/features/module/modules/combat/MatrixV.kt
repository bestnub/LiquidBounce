package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(
    name = "MatrixV",
    description = "Automatically goes lower lower lower baby!",
    category = ModuleCategory.COMBAT
)
class MatrixV: Module() {

    private val allowValue = BoolValue("Allow?", false)
    private val lowerValue = BoolValue("Lower?", false)
    private val sameValue = BoolValue("SameY?", false)
    private val lowValue = FloatValue("LowValue!!", 1.0F, 0.0F, 10.0F)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(allowValue.get()) {
            if(lowerValue.get()) {
                if(sameValue.get()) {
                    mc.thePlayer!!.setPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + lowValue.get(), mc.thePlayer!!.posZ)
                    ClientUtils.displayChatMessage("posY NOW! &4${mc.thePlayer!!.posY}")
                } else {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, lowValue.get().toDouble(), mc.thePlayer!!.posZ, true))
                    ClientUtils.displayChatMessage("posY NOW! &7${mc.thePlayer!!.posY}")
                }
            }
        } else return
    }
}