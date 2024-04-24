package dev.bernasss12.bebooks.manage

import dev.bernasss12.bebooks.BetterEnchantedBooks
import dev.bernasss12.bebooks.config.ModConfig
import net.minecraft.text.MutableText
import net.minecraft.text.Text

object MaxEnchantmentManager {
    private val shouldShowEnchantmentMaxLevel = ThreadLocal.withInitial { false }

    @JvmStatic
    fun setShowMaxLevel() = shouldShowEnchantmentMaxLevel.set(true)

    @JvmStatic
    fun appendMaxEnchantmentLevel(level: Int, maxLevel: Int, enchantmentName: MutableText) {
        if (
            ModConfig.showMaxEnchantmentLevel &&
            (level != 1 || maxLevel != 1) &&
            BetterEnchantedBooks.isCurrentItemStackEnchantedBookItem
        ) {
            enchantmentName.append("/").append(Text.translatable("enchantment.level.$maxLevel"))
            shouldShowEnchantmentMaxLevel.set(false)
        }
    }
}