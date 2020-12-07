package net.ccbluex.liquidbounce.features.module.modules.`fun`

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "GCD", description = "Module for testing.", category = ModuleCategory.FUN)
class GCD : Module() {
    val gcdDividerValue = FloatValue("gcdDivivder", 1F, 0.5F, 10F)
}