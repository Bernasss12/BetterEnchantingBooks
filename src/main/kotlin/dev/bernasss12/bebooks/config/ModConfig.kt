package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER
import dev.bernasss12.bebooks.manage.BookColorManager
import dev.bernasss12.bebooks.manage.EnchantmentDataManager
import dev.bernasss12.bebooks.model.color.ColorSavingMode
import dev.bernasss12.bebooks.util.ModConstants.CONFIG_DIR
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_COLOR_BOOKS
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_COLOR_MODE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_COLOR_SAVING_MODE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_CURSE_COLOR_OVERRIDE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_GLINT_SETTING
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_KEEP_CURSES_BELOW
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SORTING_MODE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_TOOLTIP_MODE
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.io.path.createDirectories

object ModConfig {
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

    inline fun <T> applyTooltip(block: () -> T) {
        if (tooltipMode == TooltipMode.ENABLED || (tooltipMode == TooltipMode.ON_SHIFT && Screen.hasShiftDown())) {
            block.invoke()
        }
    }


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

    fun load() {
        if (file.exists()) {
            try {
                file.inputStream().use { stream ->
                    properties.load(stream)
                }
                save()
            } catch (e: IOException) {
                LOGGER.warn("Could not read ${file.name} properties file. Using defaults.")
            }
        }
    }

    fun save() {
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

    private fun <T : Any> Properties.getPropertyOrDefault(key: String, default: T, convert: (String) -> T): T {
        return convert.invoke(
            getOrPut(key) { default.toString() }.toString()
        )
    }

    private fun Properties.setProperty(key: String, any: Any) {
        setProperty(key, any.toString())
    }

    fun getConfigScreen(): Screen = ConfigBuilder.create().apply {
        defaultBackgroundTexture = Identifier("minecraft:textures/block/spruce_planks.png")
        setGlobalized(true)// Creating categories
        val entryBuilder = entryBuilder()

        getOrCreateCategory(Text.translatable("category.bebooks.sorting_settings")).apply {
            // Sorting settings page
            addEntry(
                entryBuilder.startEnumSelector(
                    Text.translatable("entry.bebooks.sorting_settings.sorting_mode"),
                    SortingMode::class.java,
                    sortingMode
                ).apply {
                    setDefaultValue(DEFAULT_SORTING_MODE)
                    setSaveConsumer { sortingMode = it }
                }.build()
            )
            addEntry(
                entryBuilder.startBooleanToggle(
                    Text.translatable("entry.bebooks.sorting_settings.keep_curses_at_bottom"),
                    keepCursesBelow
                ).apply {
                    setDefaultValue(DEFAULT_KEEP_CURSES_BELOW)
                    setSaveConsumer { keepCursesBelow = it }
                }.build()
            )
        }

        getOrCreateCategory(Text.translatable("category.bebooks.book_coloring_settings")).apply {
            // Coloring settings page
            addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_glint_settings.active"), enchantedBookGlint).apply {
                    setDefaultValue(DEFAULT_GLINT_SETTING)
                    setSaveConsumer { enchantedBookGlint = it }
                }.build()
            )
            addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_coloring_settings.active"), colorBooks).apply {
                    setDefaultValue(DEFAULT_COLOR_BOOKS)
                    setSaveConsumer { colorBooks = it }
                }.build()
            )
            addEntry(
                entryBuilder.startEnumSelector(
                    Text.translatable("entry.bebooks.book_coloring_settings.color_mode"), SortingMode::class.java,
                    colorMode
                ).apply {
                    setDefaultValue(DEFAULT_COLOR_MODE)
                    setSaveConsumer { colorMode = it }
                }.build()
            )
            addEntry(
                entryBuilder.startBooleanToggle(
                    Text.translatable("entry.bebooks.book_coloring_settings.curse_color_override_others"),
                    overrideCurseColor
                ).apply {
                    setDefaultValue(DEFAULT_CURSE_COLOR_OVERRIDE)
                    setSaveConsumer { overrideCurseColor = it }
                }.build()
            )

            val entries = ArrayList<AbstractConfigListEntry<*>>()
            val enchantments = Registries.ENCHANTMENT.keys.map { EnchantmentDataManager.getData(it.value) }
            for (enchantment in enchantments) {
                if (enchantment.enchantment == null) continue  // not registered
                entries.add(
                    entryBuilder.startColorField(Text.literal(enchantment.translated), enchantment.color.rgb).apply {
                        setDefaultValue(EnchantmentDataManager.getDefaultColorForId(enchantment.identifier).rgb)
                        setSaveConsumer { enchantment.color = Color(it) }
                    }.build()
                )
            }
            entries.sortWith(Comparator.comparing { entry: AbstractConfigListEntry<*> ->
                entry.fieldName.string
            })
            addEntry(
                entryBuilder.startSubCategory(Text.translatable("subcategory.bebooks.book_coloring_settings.enchantment_color"), entries).build()
            )

            addEntry(
                entryBuilder.startEnumSelector(
                    Text.translatable("entry.bebooks.book_coloring_settings.color_saving_mode"),
                    ColorSavingMode::class.java,
                    colorSavingMode,
                ).apply {
                    setDefaultValue(DEFAULT_COLOR_SAVING_MODE)
                    setSaveConsumer { colorSavingMode = it }
                }.build()
            )
        }

        getOrCreateCategory(Text.translatable("category.bebooks.tooltip_settings")).apply {
            // Tooltip settings page
            addEntry(
                entryBuilder.startBooleanToggle(
                    Text.translatable("entry.bebooks.tooltip_settings.show_enchantment_max_level"),
                    showMaxEnchantmentLevel
                ).apply {
                    setDefaultValue(DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL)
                    setSaveConsumer { showMaxEnchantmentLevel = it }
                }.build()
            )
            addEntry(
                entryBuilder.startEnumSelector(
                    Text.translatable("entry.bebooks.tooltip_settings.tooltip_mode"), TooltipMode::class.java,
                    tooltipMode
                ).apply {
                    setDefaultValue(DEFAULT_TOOLTIP_MODE)
                    setSaveConsumer { tooltipMode = it }
                }.build()
            )
        }

        setSavingRunnable {
            save()
            EnchantmentDataManager.save()

            BookColorManager.clear()
        }
    }.build()
}
