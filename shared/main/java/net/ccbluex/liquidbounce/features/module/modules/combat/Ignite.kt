/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.ItemType
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityPlayerSP
import net.ccbluex.liquidbounce.api.minecraft.client.multiplayer.IWorldClient
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WMathHelper
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import kotlin.math.atan2
import kotlin.math.sqrt

@ModuleInfo(name = "Ignite", description = "Automatically sets targets around you on fire.", category = ModuleCategory.COMBAT)
class Ignite: Module() {
    private val lighterValue = BoolValue("Lighter", true)
    private val lavaBucketValue = BoolValue("Lava", true)
    private val modeValue = ListValue("Mode", arrayOf("Normal", "Switch"), "Normal")
    private val msTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!msTimer.hasTimePassed(500L))
            return

        val thePlayer: IEntityPlayerSP? = mc.thePlayer
        val theWorld: IWorldClient? = mc.theWorld

        if (thePlayer == null || theWorld == null)
            return

        val lighterInHotbar =
            if(lighterValue.get()) InventoryUtils.findItem(36, 45, classProvider.getItemEnum(ItemType.FLINT_AND_STEEL)) else -1
        val lavaInHotbar =
            if(lavaBucketValue.get()) InventoryUtils.findItem(36, 45, classProvider.getItemEnum(ItemType.LAVA_BUCKET)) else -1

        if (lighterInHotbar == -1 && lavaInHotbar == -1)
            return

        val fireInHotbar = if(lighterInHotbar != -1) lighterInHotbar else lavaInHotbar

        for (entity: IEntity in theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, true) && !entity.burning) {
                val blockPos: WBlockPos = entity.position

                if (mc.thePlayer!!.getDistanceSq(blockPos) >= 22.3 ||
                    !BlockUtils.isReplaceable(blockPos) ||
                    !classProvider.isBlockAir(BlockUtils.getBlock(blockPos)))
                    continue

                RotationUtils.keepCurrentRotation = true

                if(modeValue.get().equals("Normal", ignoreCase = true)) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(fireInHotbar - 36))
                }

                if(modeValue.get().equals("Switch", ignoreCase = true)) {
                    thePlayer.inventory.currentItem = fireInHotbar - 36
                    mc.playerController.updateController()
                }

                val itemStack: IItemStack? = mc.thePlayer!!.inventory.getStackInSlot(fireInHotbar)

                if (classProvider.isItemBucket(itemStack!!.item)) {
                    val diffX: Double = blockPos.x + 0.5 - mc.thePlayer!!.posX
                    val diffY: Double = blockPos.y + 0.5 - (thePlayer.entityBoundingBox.minY + thePlayer.eyeHeight)
                    val diffZ: Double = blockPos.z + 0.5 - thePlayer.posZ
                    val sqrt: Double = sqrt(diffX * diffX + diffZ * diffZ)
                    val yaw: Float = ((atan2(diffZ, diffX) * 180.0 / Math.PI) - 90F).toFloat()
                    val pitch: Float = (-(atan2(diffY, sqrt) * 180.0 / Math.PI)).toFloat()

                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerLook(
                        thePlayer.rotationYaw +
                                WMathHelper.wrapAngleTo180_float((yaw - thePlayer.rotationYaw)),
                        thePlayer.rotationPitch +
                                WMathHelper.wrapAngleTo180_float((pitch - thePlayer.rotationPitch)),
                        thePlayer.onGround))

                    mc.playerController.sendUseItem(thePlayer, theWorld, itemStack)
                } else {
                    for (enumFacingType: EnumFacingType in EnumFacingType.values()) {
                        val side: IEnumFacing = classProvider.getEnumFacing(enumFacingType)

                        val neighbor: WBlockPos = blockPos.offset(side)

                        if (!BlockUtils.canBeClicked(neighbor)) continue

                        val diffX: Double = neighbor.x + 0.5 - thePlayer.posX
                        val diffY: Double = neighbor.y + 0.5 -
                                (thePlayer.entityBoundingBox.minY +
                                        thePlayer.eyeHeight)
                        val diffZ: Double = neighbor.z + 0.5 - thePlayer.posZ
                        val sqrt: Double = sqrt(diffX * diffX + diffZ * diffZ)
                        val yaw: Float = ((atan2(diffZ, diffX) * 180.0 / Math.PI) - 90F).toFloat()
                        val pitch: Float = (-(atan2(diffY, sqrt) * 180.0 / Math.PI)).toFloat()

                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerLook(
                            thePlayer.rotationYaw +
                                    WMathHelper.wrapAngleTo180_float(yaw - thePlayer.rotationYaw),
                            thePlayer.rotationPitch +
                                    WMathHelper.wrapAngleTo180_float(pitch - thePlayer.rotationPitch),
                            thePlayer.onGround))

                        if (mc.playerController.onPlayerRightClick(thePlayer, theWorld, itemStack, neighbor,
                                side.opposite, WVec3(side.directionVec))) {
                            thePlayer.swingItem()
                            break
                        }
                    }
                }

                mc.netHandler
                    .addToSendQueue(classProvider.createCPacketHeldItemChange(thePlayer.inventory.currentItem))
                RotationUtils.keepCurrentRotation = false
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerLook(thePlayer.rotationYaw, thePlayer.rotationPitch, thePlayer.onGround)
                )

                msTimer.reset()
                break
            }
        }
    }
}
