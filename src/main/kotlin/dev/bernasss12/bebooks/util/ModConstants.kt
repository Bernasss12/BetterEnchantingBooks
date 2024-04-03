package dev.bernasss12.bebooks.util

import dev.bernasss12.bebooks.config.SortingMode
import dev.bernasss12.bebooks.config.TooltipMode
import dev.bernasss12.bebooks.model.color.ColorSavingMode
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments.*
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.*
import java.awt.Color
import java.nio.file.Path

object ModConstants {
    const val SETTINGS_VERSION: Int = 2
    val CONFIG_DIR: Path = FabricLoader.getInstance().configDir.resolve("bebooks")

    val DEFAULT_CHECKED_ITEMS_LIST: List<ItemStack> = listOf(
        DIAMOND_SWORD,
        DIAMOND_PICKAXE,
        DIAMOND_AXE,
        DIAMOND_SHOVEL,
        DIAMOND_HOE,
        BOW,
        CROSSBOW,
        FISHING_ROD,
        TRIDENT,
        DIAMOND_HELMET,
        DIAMOND_CHESTPLATE,
        DIAMOND_LEGGINGS,
        DIAMOND_BOOTS,
        ELYTRA,
        SHIELD,
        SHEARS,
        CARROT_ON_A_STICK,
        WARPED_FUNGUS_ON_A_STICK,
        FLINT_AND_STEEL
    ).map(::ItemStack)

    // Default enchantment colors as suggested by twusya on https://www.curseforge.com/minecraft/mc-mods/better-enchanted-books#c47
    val DEFAULT_ENCHANTMENT_COLORS: Map<Enchantment, Color> = hashMapOf(
        AQUA_AFFINITY to 0x6e7af7,
        BANE_OF_ARTHROPODS to 0x0f5160,
        BLAST_PROTECTION to 0x442e62,
        CHANNELING to 0xaef5ff,
        BINDING_CURSE to 0x274d1e,
        VANISHING_CURSE to 0x171220,
        DEPTH_STRIDER to 0x9cdbff,
        EFFICIENCY to 0xfff164,
        FEATHER_FALLING to 0xfff0d1,
        FIRE_ASPECT to 0xff7516,
        FIRE_PROTECTION to 0xc4aaa5,
        FLAME to 0xff7516,
        FORTUNE to 0xffb65b,
        FROST_WALKER to 0x90b4ff,
        IMPALING to 0xc5133a,
        INFINITY to 0x7b5be7,
        KNOCKBACK to 0x605b60,
        LOOTING to 0xffb65b,
        LOYALTY to 0x6ec4b1,
        LUCK_OF_THE_SEA to 0x4be850,
        LURE to 0xff0000,
        MENDING to 0xcaff61,
        MULTISHOT to 0xffb301,
        PIERCING to 0x337b50,
        POWER to 0xc5133a,
        PROJECTILE_PROTECTION to 0xcccdd5,
        PROTECTION to 0xa5afc4,
        PUNCH to 0x605b60,
        QUICK_CHARGE to 0xff0000,
        RESPIRATION to 0x7ad5ff,
        RIPTIDE to 0xaccff1,
        SHARPNESS to 0xc5133a,
        SILK_TOUCH to 0xffffff,
        SMITE to 0xbf5f2e,
        SOUL_SPEED to 0x41342c,
        SWEEPING to 0xffb301,
        THORNS to 0x560d0b,
        UNBREAKING to 0x5c3350
    ).mapValues { Color(it.value) }

    const val DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL: Boolean = false
    val DEFAULT_TOOLTIP_MODE: TooltipMode = TooltipMode.ON_SHIFT
    val DEFAULT_SORTING_MODE: SortingMode = SortingMode.ALPHABETICALLY
    const val DEFAULT_KEEP_CURSES_BELOW: Boolean = true
    const val DEFAULT_COLOR_BOOKS: Boolean = true
    const val DEFAULT_CURSE_COLOR_OVERRIDE: Boolean = true
    val DEFAULT_COLOR_MODE: SortingMode = SortingMode.ALPHABETICALLY
    val DEFAULT_COLOR_SAVING_MODE: ColorSavingMode = ColorSavingMode.HEXADECIMAL

    val DEFAULT_BOOK_STRIP_COLOR: Color = Color(0xc5133a)
    const val DEFAULT_GLINT_SETTING: Boolean = false
}