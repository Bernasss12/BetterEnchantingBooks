package dev.bernasss12.bebooks.manage

import dev.bernasss12.bebooks.config.DefaultConfigs.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.config.DefaultConfigs.getDefaultColor
import dev.bernasss12.bebooks.util.Util.getStoredEnchantments
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack
import java.awt.Color

object BookColorManager {
    private val cache = hashMapOf<ItemStack, Color>()

    val itemColorProvider = ItemColorProvider { stack: ItemStack?, tintIndex: Int ->
        if (tintIndex != 1) return@ItemColorProvider Color.WHITE.rgb

        return@ItemColorProvider stack?.getStoredEnchantments()?.let {
            when {
                it.size == 1 -> getDefaultColor(it.enchantments.first().key.get().value).rgb
                else -> null
            }
        } ?: DEFAULT_BOOK_STRIP_COLOR.rgb
    }

    fun clear() = cache.clear()
}