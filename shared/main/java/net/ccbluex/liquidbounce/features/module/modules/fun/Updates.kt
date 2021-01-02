package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(
    name = "uPDATES",
    description = "Testingticktimerscuzidkshitaboutit",
    category = ModuleCategory.FUN
)

class Updates: Module() {

    private val allowValue = BoolValue("Allow", false)
    private val allowValue2 = BoolValue("Allow2", false)
    private val allowValue3 = BoolValue("Allow3", false)
    private val allowValue4 = BoolValue("Allow4", false)
    private val allowNumber = IntegerValue("Ticks", 20, 1, 20)
    private var test1 = TickTimer()
    private var test2 = TickTimer()
    private var test3 = TickTimer()
    private var test4 = TickTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(allowValue.get()) {
            test1.update()
            if(test1.hasTimePassed(allowNumber.get())) {
                ClientUtils.displayChatMessage("Test ${allowNumber.get()}")
                test1.reset()
            }
        } else return
        if(allowValue2.get()) {
            test2.update()
            if(test2.hasTimePassed(allowNumber.get())) {
                ClientUtils.displayChatMessage("Test2 ${allowNumber.get()}")
            }
        } else return
        if(allowValue3.get()) {
            if(test3.hasTimePassed(allowNumber.get())) {
                ClientUtils.displayChatMessage("Test3 ${allowNumber.get()}")
            }
        } else return
        if(allowValue4.get()) {
            if(test4.hasTimePassed(allowNumber.get())) {
                ClientUtils.displayChatMessage("Test4 ${allowNumber.get()}")
                test4.reset()
            }
        } else return
    }
}