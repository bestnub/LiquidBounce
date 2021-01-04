package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue


@ModuleInfo(
    name = "Speedboi",
    description = "Automatically fucks matrix!.!",
    category = ModuleCategory.FUN
)
class Speedmatrix: Module() {

    private val allowValue = BoolValue("Allow", false)
    //private val fallValue = FloatValue("FallDist", 1F, 0.1F, 2F)
    private val speedAir = FloatValue("SpeedInAir", 0.02F, 0.00F, 1F)
    private val timerValue = FloatValue("Timer", 1F, 0.1F, 2F)
    private val speedAir2 = FloatValue("SpeedInAir2", 0.02F, 0.00F, 1F)
    private val timerValue2 = FloatValue("Timer2", 1F, 0.1F, 2F)
    private val tickValue = IntegerValue("Ticks", 20, 1, 20)
    private var pass1: Boolean = false
    private var passTimer = TickTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer!!.isInWater || !mc.thePlayer!!.isInLava || !mc.thePlayer!!.isInWeb || !mc.thePlayer!!.isOnLadder) {
            if (allowValue.get()) {
                if(mc.thePlayer!!.onGround) {
                    if(MovementUtils.isMoving) {
                        pass1 = true
                        mc.thePlayer!!.sprinting = true
                        mc.thePlayer!!.jump()
                        mc.timer.timerSpeed = timerValue.get()
                        mc.thePlayer!!.speedInAir = speedAir.get()
                    } else {
                        mc.timer.timerSpeed = 1f
                    }
                } else {
                    if(pass1) {
                        passTimer.update()
                        if(passTimer.hasTimePassed(tickValue.get())) {
                            mc.timer.timerSpeed = timerValue2.get()
                            mc.thePlayer!!.speedInAir = speedAir2.get()
                            pass1 = false
                            passTimer.reset()
                        }
                    }
                }
            } else return
        } else {
            mc.timer.timerSpeed = 1f
            mc.thePlayer!!.speedInAir = 0.02f
            return
        }
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer!!.speedInAir = 0.02f
    }
}