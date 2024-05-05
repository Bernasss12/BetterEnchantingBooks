package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_COLOR_BOOKS
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_COLOR_MODE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_COLOR_SAVING_MODE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_CURSE_COLOR_OVERRIDE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_GLINT_SETTING
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_KEEP_CURSES_BELOW
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SORTING_MODE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_TOOLTIP_MODE
import dev.bernasss12.bebooks.config.SavedConfigManager.CONFIG_DIR
import dev.bernasss12.bebooks.config.model.*
import net.minecraft.client.gui.screen.Screen
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
import java.awt.Color
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.io.path.createDirectories

object ModConfig {

    object Defaults {
        // Sorting settings
        val DEFAULT_SORTING_MODE: SortingMode = SortingMode.ALPHABETICALLY
        const val DEFAULT_KEEP_CURSES_BELOW: Boolean = true
        val DEFAULT_CURSE_MODE: CurseMode = CurseMode.BELOW

        // Tooltip settings
        const val DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL: Boolean = false
        val DEFAULT_TOOLTIP_MODE: TooltipMode = TooltipMode.ON_SHIFT

        // Coloring settings
        const val DEFAULT_COLOR_BOOKS: Boolean = true
        const val DEFAULT_CURSE_COLOR_OVERRIDE: Boolean = true
        val DEFAULT_COLOR_MODE: SortingMode = SortingMode.ALPHABETICALLY
        val DEFAULT_COLOR_SAVING_MODE: ColorSavingMode = ColorSavingMode.HEXADECIMAL

        // Remove enchantment glint
        const val DEFAULT_GLINT_SETTING: Boolean = false

        // Minecraft's enchantment book strip color
        val DEFAULT_BOOK_STRIP_COLOR: Color = Color(0xc5133a)
    }

    private val file: File = CONFIG_DIR.resolve("config.properties").toFile()
    private val properties: Properties = object : Properties() {
        override val values: MutableCollection<Any> = linkedSetOf()
    }

    // Sorting settings
    var sortingMode: SortingMode
        get() = properties.getPropertyOrDefault("sorting_mode", DEFAULT_SORTING_MODE, SortingMode::fromString)
        set(value) = properties.setProperty("sorting_mode", value)
    var keepCursesBelow: Boolean
        get() = properties.getPropertyOrDefault("keep_curses_below", DEFAULT_KEEP_CURSES_BELOW, String::toBoolean)
        set(value) = properties.setProperty("keep_curses_below", value)

    // Tooltip settings
    var showMaxEnchantmentLevel: Boolean
        get() = properties.getPropertyOrDefault("show_max_enchantment_level", DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL, String::toBoolean)
        set(value) = properties.setProperty("show_max_enchantment_level", value)
    var tooltipMode: TooltipMode
        get() = properties.getPropertyOrDefault("tooltip_mode", DEFAULT_TOOLTIP_MODE, TooltipMode::fromString)
        set(value) = properties.setProperty("tooltip_mode", value)

    // Coloring settings
    var colorBooks: Boolean
        get() = properties.getPropertyOrDefault("color_books", DEFAULT_COLOR_BOOKS, String::toBoolean)
        set(value) = properties.setProperty("color_books", value)
    var overrideCurseColor: Boolean
        get() = properties.getPropertyOrDefault("override_curse_color", DEFAULT_CURSE_COLOR_OVERRIDE, String::toBoolean)
        set(value) = properties.setProperty("override_curse_color", value)
    var colorMode: SortingMode
        get() = properties.getPropertyOrDefault("color_mode", DEFAULT_COLOR_MODE, SortingMode::fromString)
        set(value) = properties.setProperty("color_mode", value)
    var colorSavingMode: ColorSavingMode
        get() = properties.getPropertyOrDefault("color_saving_mode", DEFAULT_COLOR_SAVING_MODE, ColorSavingMode::fromString)
        set(value) = properties.setProperty("color_saving_mode", value)

    // Remove enchantment glint
    var enchantedBookGlint: Boolean
        get() = properties.getPropertyOrDefault("enchanted_book_glint", DEFAULT_GLINT_SETTING, String::toBoolean)
        set(value) = properties.setProperty("enchanted_book_glint", value)

    // Enchantment color priority list
    private var enchantmentColorPriorityList: Set<Enchantment> = emptySet()
        private set

    // Tooltip sorting priority list
    var enchantmentTooltipPriorityList: RegistryEntryList<Enchantment> = RegistryEntryList.empty()
        private set
        // TODO utilize backing property and dirty state and make it so when it's queried and dirty it will be recalculated.

    inline fun <T> applyTooltip(block: () -> T) {
        if (tooltipMode == TooltipMode.ENABLED || (tooltipMode == TooltipMode.ON_SHIFT && Screen.hasShiftDown())) {
            block.invoke()
        }
    }

    fun loadProperties() {
        if (file.exists()) {
            try {
                file.inputStream().use { stream ->
                    properties.load(stream)
                }
                saveProperties()
            } catch (e: IOException) {
                LOGGER.warn("Could not read ${file.name} properties file. Using defaults.")
            }
        }
    }

    fun saveProperties() {
        try {
            CONFIG_DIR.createDirectories()
            properties.apply {
                file.outputStream().use { stream ->
                    try {
                        // TODO add client command to reload settings
                        store(stream, "The settings will only be loaded at game start when changed here.")
                        LOGGER.debug("Saving configs to disk.")
                    } catch (e: IOException) {
                        LOGGER.warn("Could not write ${file.name} properties file. Changed settings may be lost.")
                    }
                }
            }
        } catch (e: IOException) {
            LOGGER.error("Couldn't create config directory.\nChanged settings could be lost!", e)
        }
    }

    private fun Collection<EnchantmentData>.sortToEnchantmentSet(sortingMode: SortingMode): Set<EnchantmentData> {
        return when (sortingMode) {
            SortingMode.ALPHABETICALLY -> sortedBy { it.translated }
            SortingMode.CUSTOM -> sortedBy { it.priority }
            SortingMode.DISABLED -> emptyList()

        }.toSet()
    }

    private fun Collection<EnchantmentData>.sortCurses(curseMode: CurseMode): Set<EnchantmentData> {
        return when (curseMode) {
            CurseMode.ABOVE -> sortedBy { !it.curse }
            CurseMode.BELOW -> sortedBy { it.curse }
            CurseMode.IGNORE -> this
        }.toSet()
    }

    private fun Collection<EnchantmentData>.toEnchantmentSet() : Set<Enchantment> = mapNotNull { it.enchantment }.toSet()

    private fun Collection<RegistryEntry<Enchantment>>.toRegistryList(): RegistryEntryList<Enchantment> = RegistryEntryList.of(this.toList())

    fun reloadPriorityLists() {
        enchantmentColorPriorityList = SavedConfigManager.getAllEnchantmentData()
            .sortToEnchantmentSet(colorMode)
            .sortCurses(if (overrideCurseColor) CurseMode.ABOVE else CurseMode.IGNORE)
            .toEnchantmentSet()

        enchantmentTooltipPriorityList = SavedConfigManager.getAllEnchantmentData()
            .sortToEnchantmentSet(sortingMode)
            .sortCurses(if (keepCursesBelow) CurseMode.BELOW else CurseMode.IGNORE)
            .toEnchantmentSet()
            .map { it.registryEntry }
            .toRegistryList()
    }

    /*
        Useful property methods.
     */

    private fun <T : Any> Properties.getPropertyOrDefault(key: String, default: T, convert: (String) -> T): T {
        return convert.invoke(
            getOrPut(key) { default.toString() }.toString()
        )
    }

    private fun Properties.setProperty(key: String, any: Any) {
        setProperty(key, any.toString())
    }
}
