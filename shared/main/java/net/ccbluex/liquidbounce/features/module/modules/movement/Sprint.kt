
/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "Sprint", description = "Automatically sprints all the time.", category = ModuleCategory.MOVEMENT)
class Sprint : Module() {

    private val allDirectionsMode = ListValue("AllDirectionsMode", arrayOf("Normal", "Matrix"), "Normal")
    @JvmField
    val allDirectionsValue = BoolValue("AllDirections", true)
    @JvmField
    val blindnessValue = BoolValue("Blindness", true)
    @JvmField
    val foodValue = BoolValue("Food", true)
    @JvmField
    val checkServerSide = BoolValue("CheckServerSide", false)
    @JvmField
    val checkServerSideGround = BoolValue("CheckServerSideOnlyGround", false)

    private var stopSprint: Boolean = false
    var cancel: Boolean = true

    override fun onEnable() {
        stopSprint = false
        cancel = true
    }
    @EventTarget
    fun onJump(event: JumpEvent) {
        if (cancel && mc.thePlayer!!.sprinting) {
            event.cancelEvent()
            cancel = false
            val yaw = mc.thePlayer!!.rotationYaw
            mc.thePlayer!!.rotationYaw = getMoveYaw()
            mc.thePlayer!!.jump()
            mc.thePlayer!!.rotationYaw = yaw
            cancel = true
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!MovementUtils.isMoving || mc.thePlayer!!.sneaking || (blindnessValue.get() && mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.BLINDNESS))) || (foodValue.get() && !(mc.thePlayer!!.foodStats.foodLevel > 6.0F || mc.thePlayer!!.capabilities.allowFlying)) || (checkServerSide.get() && (mc.thePlayer!!.onGround || !checkServerSideGround.get()) && !allDirectionsValue.get() && RotationUtils.targetRotation != null && RotationUtils.getRotationDifference(Rotation(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)) > 30)) {
            mc.thePlayer!!.sprinting = false
            return
        }
        if (allDirectionsValue.get() && allDirectionsMode.get().equals("Normal", true) || mc.thePlayer!!.movementInput.moveForward >= 0.8F) {
            mc.thePlayer!!.sprinting = true
        }
        if(allDirectionsValue.get() && allDirectionsMode.get().equals("Matrix", true)) {
            if (MovementUtils.isMoving) {
                if (!stopSprint && mc.thePlayer!!.moveForward <= 0) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    stopSprint = true
                } else if (stopSprint) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    stopSprint = false
                }
            } else {
                stopSprint = false
            }
        }
    }

    private fun getMoveYaw():Float {
        var moveYaw = mc.thePlayer!!.rotationYaw
        if (mc.thePlayer!!.moveForward != 0F && mc.thePlayer!!.moveStrafing == 0F) {
            moveYaw += if(mc.thePlayer!!.moveForward > 0) 0 else 180
        } else if (mc.thePlayer!!.moveForward != 0F && mc.thePlayer!!.moveStrafing != 0F) {
            if (mc.thePlayer!!.moveForward > 0F)
                moveYaw += if(mc.thePlayer!!.moveStrafing > 0) -45 else 45
            else
                moveYaw -= if(mc.thePlayer!!.moveStrafing > 0) -45 else 45

            moveYaw += if(mc.thePlayer!!.moveForward > 0) 0 else 180
        } else if (mc.thePlayer!!.moveStrafing != 0F && mc.thePlayer!!.moveForward == 0F) {
            moveYaw += if(mc.thePlayer!!.moveStrafing > 0) -90 else 90
        }
        return moveYaw
    }
}