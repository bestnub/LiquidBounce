/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.CrossVersionUtils
import net.ccbluex.liquidbounce.utils.CrossVersionUtils.createOpenInventoryPacket
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.item.ArmorComparator
import net.ccbluex.liquidbounce.utils.item.ArmorPiece
import net.ccbluex.liquidbounce.utils.item.ItemUtils
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import java.util.stream.Collectors
import java.util.stream.IntStream


@ModuleInfo(name = "AutoArmor", description = "Automatically equips the best armor in your inventory.", category = ModuleCategory.COMBAT)
class AutoArmor : Module() {

    private val armorComparator = ArmorComparator()
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 100, 0, 400) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelayValue.get()

            if (maxDelay < newValue) set(maxDelay)
        }
    }
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 200, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelay = minDelayValue.get()

            if (minDelay > newValue) set(minDelay)
        }
    }
    private val invOpenValue = BoolValue("InvOpen", false)
    private val simulateInventory = BoolValue("SimulateInventory", true)
    private val noMoveValue = BoolValue("NoMove", false)
    private val itemDelayValue = IntegerValue("ItemDelay", 0, 0, 5000)
    private val hotbarValue = BoolValue("Hotbar", true)

    private var delay = 0L

    private var locked: Boolean = false

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!InventoryUtils.CLICK_TIMER.hasTimePassed(delay) || mc.thePlayer == null ||
            (mc.thePlayer!!.openContainer != null && mc.thePlayer!!.openContainer!!.windowId != 0)
        )
            return

        // Find best armor
        val armorPieces = IntStream.range(0, 36)
            .filter { i ->
                val itemStack = mc.thePlayer!!.inventory.getStackInSlot(i)

                return@filter (itemStack != null && classProvider.isItemArmor(itemStack.item)
                        && (i < 9 || System.currentTimeMillis() - itemStack.itemDelay >= itemDelayValue.get()))
            }
            .mapToObj { i -> ArmorPiece(mc.thePlayer!!.inventory.getStackInSlot(i), i) }
            .collect(Collectors.groupingBy(ArmorPiece::getArmorType))

        val bestArmor = arrayOfNulls<ArmorPiece>(4)

        for (armorEntry in armorPieces.entries) {
            bestArmor[armorEntry.key] = armorEntry.value.stream()
                .max(armorComparator).orElse(null)
        }

        // Swap armor
        for (i in 0..3) {
            val armorPiece: ArmorPiece? = bestArmor[i]

            if (armorPiece == null)
                continue

            val armorSlot = 3 - i

            val oldArmor = ArmorPiece(mc.thePlayer!!.inventory.armorItemInSlot(armorSlot), -1)

            if (ItemUtils.isStackEmpty(oldArmor.itemStack) || !classProvider.isItemArmor(oldArmor.itemStack.item) ||
                armorComparator.compare(oldArmor, armorPiece) < 0
            ) {
                if (!ItemUtils.isStackEmpty(oldArmor.itemStack) && move(8 - (3 - armorSlot), true)) {
                    locked = true
                    return
                }

                if (ItemUtils.isStackEmpty(mc.thePlayer!!.inventory.armorItemInSlot(armorSlot)) && move(
                        armorPiece.slot,
                        false
                    )
                ) {
                    locked = true
                    return
                }
            }
        }

        locked = false
    }

    fun isLocked(): Boolean {
        return !state || locked
    }

    /**
     * Shift+Left clicks the specified item
     *
     * @param item        Slot of the item to click
     * @param isArmorSlot
     * @return True if it is unable to move the item
     */
    private fun move(item: Int, isArmorSlot: Boolean): Boolean {
        if (!isArmorSlot && item < 9 && hotbarValue.get() && !classProvider.isGuiInventory(mc.currentScreen)) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(item))
            mc.netHandler.addToSendQueue(
                CrossVersionUtils.createUseItemPacket(
                    mc.thePlayer!!.inventoryContainer.getSlot(
                        item
                    ).stack, WEnumHand.MAIN_HAND
                )
            )
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

            return true
        } else if (!(noMoveValue.get() && MovementUtils.isMoving) && (!invOpenValue.get() || classProvider.isGuiInventory(
                mc.currentScreen
            )) && item != -1
        ) {
            val openInventory: Boolean = simulateInventory.get() && !classProvider.isGuiInventory(mc.currentScreen)

            if (openInventory)
                mc.netHandler.addToSendQueue(createOpenInventoryPacket())

            var full = isArmorSlot

            if (full) {
                for (iItemStack in mc.thePlayer!!.inventory.mainInventory) {
                    if (ItemUtils.isStackEmpty(iItemStack)) {
                        full = false
                        break
                    }
                }
            }

            if (full) {
                mc.playerController.windowClick(mc.thePlayer!!.inventoryContainer.windowId, item, 1, 4, mc.thePlayer!!)
            } else {
                mc.playerController.windowClick(
                    mc.thePlayer!!.inventoryContainer.windowId,
                    (if (isArmorSlot) item else (if (item < 9) item + 36 else item)),
                    0,
                    1,
                    mc.thePlayer!!
                )
            }

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            if (openInventory)
                mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())

            return true
        }
        return false
    }
}