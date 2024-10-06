package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.util.Util.getStoredEnchantments
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack
import java.awt.Color

object BookColorManager {
    private val cache = hashMapOf<ItemStack, Color>()

    val itemColorProvider = ItemColorProvider { stack: ItemStack?, tintIndex: Int ->
        if (tintIndex != 1) return@ItemColorProvider Color.WHITE.rgb

        if (stack == null || !ModConfig.colorBooks) return@ItemColorProvider DEFAULT_BOOK_STRIP_COLOR.rgb

        return@ItemColorProvider cache.computeIfAbsent(stack) {
            stack.getStoredEnchantments()?.let {
                when {
                    it.size == 1 -> SavedConfigManager.getEnchantmentColor(
                        it.enchantments.first().key.get().value
                    )

                    else -> null
                }
            } ?: DEFAULT_BOOK_STRIP_COLOR
        }.rgb
    }

    fun clear() = cache.clear()
}