package dev.bernasss12.bebooks.manage

import dev.bernasss12.bebooks.config.DefaultConfigs.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.util.NBTUtil.hasStoredEnchantments
import dev.bernasss12.bebooks.util.Util.noAlpha
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack
import java.awt.Color

object BookColorManager {
    private val cache = hashMapOf<ItemStack, Color>()

    val itemColorProvider = ItemColorProvider { stack: ItemStack?, tintIndex: Int ->
        if (!ModConfig.colorBooks || stack == null) return@ItemColorProvider DEFAULT_BOOK_STRIP_COLOR.rgb
        if (tintIndex != 1) return@ItemColorProvider 0xffffffff.toInt()

        // Check if stack has any enchantments e.g. in advancement screen.
        if (!stack.hasStoredEnchantments()) {
            return@ItemColorProvider DEFAULT_BOOK_STRIP_COLOR.rgb
        }

        cache.getOrPut(stack) {
            Color.RED
//            val data = EnchantedBookItem
//                .getEnchantmentNbt(stack)
//                .getPriorityEnchantmentData(
//                    sortingMode = ModConfig.colorMode,
//                    keepCursesBelow = ModConfig.keepCursesBelow,
//                    curseColorOverride = ModConfig.overrideCurseColor
//                )
//            data.color
        }.rgb.noAlpha()
    }

    fun clear() = cache.clear()
}