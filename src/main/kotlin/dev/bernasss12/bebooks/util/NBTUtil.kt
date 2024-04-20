package dev.bernasss12.bebooks.util

import dev.bernasss12.bebooks.config.SortingMode
import dev.bernasss12.bebooks.model.enchantment.EnchantmentData
import net.minecraft.item.EnchantedBookItem.STORED_ENCHANTMENTS_KEY
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

object NBTUtil {
    @JvmStatic
    fun NbtList.sorted(sortingMode: SortingMode, keepCursesBelow: Boolean): NbtList = sortedList(sortingMode, keepCursesBelow).toNbtList()

    private fun NbtList.sortedList(
        sortingMode: SortingMode,
        keepCursesBelow: Boolean,
        curseColorOverride: Boolean = false
    ): List<NbtElement> {
        if (sortingMode == SortingMode.DISABLED && !keepCursesBelow && !curseColorOverride) return this
        return this.copy().let { list ->
            val sorted = when (sortingMode) {
                SortingMode.DISABLED -> list
                SortingMode.ALPHABETICALLY -> list.sortedBy { EnchantmentData.fromNBT(it).translated }
                SortingMode.CUSTOM -> list.sortedBy { EnchantmentData.fromNBT(it).priority }
            }

            if (keepCursesBelow || curseColorOverride) {
                val (curses, nonCurses) = sorted.partition { EnchantmentData.fromNBT(it).curse }
                return@let if (curseColorOverride) {
                    (curses + nonCurses)
                } else {
                    (nonCurses + curses)
                }
            }

            return@let sorted
        }
    }

    fun NbtList.getPriorityEnchantmentData(
        sortingMode: SortingMode,
        keepCursesBelow: Boolean,
        curseColorOverride: Boolean,
    ): EnchantmentData {
        val sorted = sortedList(sortingMode, keepCursesBelow, curseColorOverride)
        val first = sorted.first()
        return first.let { EnchantmentData.fromNBT(it) }
    }

    fun ItemStack.hasStoredEnchantments(): Boolean {
        return this.nbt?.contains(STORED_ENCHANTMENTS_KEY) ?: false
    }

    private fun List<NbtElement>.toNbtList(): NbtList = NbtList().apply { addAll(this@toNbtList) }

    fun NbtElement.getEnchantmentID(): String {
        return (this as? NbtCompound)?.getString("id") ?: error("Could not retrieve id from $this")
    }
}