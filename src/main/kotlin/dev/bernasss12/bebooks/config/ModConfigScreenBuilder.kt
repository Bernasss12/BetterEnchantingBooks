package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_COLOR_BOOKS
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_COLOR_MODE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_COLOR_SAVING_MODE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_CURSE_COLOR_OVERRIDE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_GLINT_SETTING
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_KEEP_CURSES_BELOW
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL_ALL_ITEMS
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL_ENCHANTING_TABLE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SORTING_MODE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_TOOLTIP_MODE
import dev.bernasss12.bebooks.config.ModConfig.colorBooks
import dev.bernasss12.bebooks.config.ModConfig.colorMode
import dev.bernasss12.bebooks.config.ModConfig.colorSavingMode
import dev.bernasss12.bebooks.config.ModConfig.enchantedBookGlint
import dev.bernasss12.bebooks.config.ModConfig.keepCursesBelow
import dev.bernasss12.bebooks.config.ModConfig.overrideCurseColor
import dev.bernasss12.bebooks.config.ModConfig.showMaxEnchantmentLevel
import dev.bernasss12.bebooks.config.ModConfig.showMaxEnchantmentLevelAllItems
import dev.bernasss12.bebooks.config.ModConfig.showMaxEnchantmentLevelEnchantingTable
import dev.bernasss12.bebooks.config.ModConfig.sortingMode
import dev.bernasss12.bebooks.config.ModConfig.tooltipMode
import dev.bernasss12.bebooks.config.model.ColorSavingMode
import dev.bernasss12.bebooks.config.model.SortingMode
import dev.bernasss12.bebooks.config.model.TooltipMode
import dev.bernasss12.bebooks.util.Util.noAlpha
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color

object ModConfigScreenBuilder {
    fun getConfigScreen(): Screen = ConfigBuilder.create().apply {
        defaultBackgroundTexture = Identifier.of("minecraft:textures/block/spruce_planks.png")
        setGlobalized(true) // Creating categories
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
            val enchantments = MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.ENCHANTMENT)?.keys?.mapNotNull {
                SavedConfigManager.getEnchantmentData(
                    it.value
                )
            }?: emptyList()
            for (enchantment in enchantments) {
                if (enchantment.enchantment == null) continue  // not registered
                entries.add(
                    entryBuilder.startColorField(Text.literal(enchantment.translated), enchantment.color?.rgb?.noAlpha() ?: DEFAULT_BOOK_STRIP_COLOR.rgb)
                        .apply {
                            setDefaultValue(SavedConfigManager.getDefaultColor(enchantment.identifier).rgb.noAlpha())
                        setSaveConsumer { enchantment.color = Color(it) }
                        setAlphaMode(false)
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
                entryBuilder.startBooleanToggle(
                    Text.translatable("entry.bebooks.tooltip_settings.show_enchantment_max_level_enchanting_table"),
                    showMaxEnchantmentLevelEnchantingTable
                ).apply {
                    setDefaultValue(DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL_ENCHANTING_TABLE)
                    setSaveConsumer { showMaxEnchantmentLevelEnchantingTable = it }
                }.build()
            )
            addEntry(
                entryBuilder.startBooleanToggle(
                    Text.translatable("entry.bebooks.tooltip_settings.show_enchantment_max_level_all_items"),
                    showMaxEnchantmentLevelAllItems
                ).apply {
                    setDefaultValue(DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL_ALL_ITEMS)
                    setSaveConsumer { showMaxEnchantmentLevelAllItems = it }
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
            ModConfig.saveProperties()
            SavedConfigManager.saveToDisk()
            SavedConfigManager.reload()
            BookColorManager.clear()
        }

    }.build()
}