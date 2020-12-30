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
    name = "YAWPITCH",
    description = "Automatically sets your yaw/pitch.I.",
    category = ModuleCategory.COMBAT
)
class YawPitch: Module() {

    private val allowValue = BoolValue("Allow?", false)
    private val pitchValue = FloatValue("Pitchsir", 83.2F, 0.0F, 90F)
    private val yawValue = FloatValue("Yawsir", 0.0F, 0.0F, 90F)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(allowValue.get()) {
            mc.thePlayer!!.rotationPitch = pitchValue.get()
            mc.thePlayer!!.rotationYaw = yawValue.get()
        }
    }
}