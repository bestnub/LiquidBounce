package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(
    name = "MatrixV",
    description = "Automatically goes lower lower lower baby!",
    category = ModuleCategory.COMBAT
)
class MatrixV: Module() {

    private val allowValue = BoolValue("Allow?", false)
    private val lowerValue = BoolValue("Lower?", false)
    private val sameValue = BoolValue("SameY?", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(allowValue.get()) {
            if(lowerValue.get()) {
                if(sameValue.get()) {
                    mc.thePlayer!!.posY -= 0.1
                    ClientUtils.displayChatMessage("posY NOW! ${mc.thePlayer!!.posY}")
                } else {
                    mc.thePlayer!!.posY = 0.1
                    ClientUtils.displayChatMessage("posY NOW! ${mc.thePlayer!!.posY}")
                }
            }
        } else return
    }
}