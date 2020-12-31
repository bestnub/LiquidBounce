package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue


@ModuleInfo(
    name = "Speedboi",
    description = "Automatically fucks matrix!.!",
    category = ModuleCategory.FUN
)
class Speedmatrix: Module() {

    private val allowValue = BoolValue("Allow", false)
    private val fallValue = FloatValue("FallDist", 1F, 0.1F, 2F)
    private val speedAir = FloatValue("SpeedInAir", 0.02F, 0.00F, 1F)
    private val timerValue = FloatValue("Timer", 1F, 0.1F, 2F)
    private val speedAir2 = FloatValue("SpeedInAir2", 0.02F, 0.00F, 1F)
    private val timerValue2 = FloatValue("Timer2", 1F, 0.1F, 2F)
    private val motionY = FloatValue("MotionY", 0.42f, 0.00f, 1f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(allowValue.get()) {
            if(!mc.thePlayer!!.onGround) {
                if(mc.thePlayer!!.fallDistance > fallValue.get()) {
                    mc.thePlayer!!.speedInAir = speedAir.get()
                    mc.timer.timerSpeed = timerValue.get()
                }
            } else {
                if(MovementUtils.isMoving) {
                    mc.thePlayer!!.sprinting = true
                    mc.thePlayer!!.motionY = motionY.get().toDouble()
                    mc.thePlayer!!.jump()
                    mc.thePlayer!!.speedInAir = speedAir2.get()
                    mc.timer.timerSpeed = timerValue2.get()
                } else {
                    mc.timer.timerSpeed = 1f
                }
            }
        } else return
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer!!.speedInAir = 0.02f
        mc.thePlayer!!.motionY = 0.42
    }
}