package dev.bernasss12.bebooks.manage

import dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER
import dev.bernasss12.bebooks.config.DefaultConfigs
import dev.bernasss12.bebooks.config.SavedConfigs
import dev.bernasss12.bebooks.model.enchantment.EnchantmentData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemStack.areItemsEqual
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.awt.Color
import java.io.File
import java.io.IOException

class SavedConfigsManager {

    private val file: File = DefaultConfigs.CONFIG_DIR.resolve("enchantment_data.json").toFile()
    private val enchantmentConfigurations = hashMapOf<Identifier, EnchantmentData>()
    val applicableItemIcons = mutableSetOf<ItemStack>()

    fun getData(key: Identifier): EnchantmentData {
        return enchantmentConfigurations.getOrPut(key) {
            EnchantmentData(
                identifier = key,
            )
        }
    }

    fun getData(enchantment: Enchantment): EnchantmentData? {
        return Registries.ENCHANTMENT.getId(enchantment)?.let {
            getData(
                it
            )
        }
    }

    fun save() {
        // Filter values, so it only saved non-default values.
        val nonDefaultEnchantmentData = enchantmentConfigurations.values.filter {
            it.priority != -1 || it.color != DefaultConfigs.getDefaultColor(it.identifier)
        }

        // Compare the current set with the default set and if there are no differences don't bother saving it.
        val applicableItemsDifference = if (
            applicableItemIcons.count() != DefaultConfigs.ICONS.count() ||
            applicableItemIcons.zip(DefaultConfigs.ICONS).any { (current, default) ->
                areItemsEqual(current, default)
            }
        ) applicableItemIcons else emptySet()

        SavedConfigs(
            enchantments = nonDefaultEnchantmentData,
            icons = applicableItemsDifference
        ).writeToFile(file)
    }

    fun load() {
        clear()
        try {
            val jsonString = file.readText()
            try {
                // Try reading the data as the new format:
                val data: SavedConfigs = SavedConfigs.readFromJson(jsonString)
                enchantmentConfigurations.clear()
                data.enchantments.forEach { enchantmentData ->
                    enchantmentConfigurations[enchantmentData.identifier] = enchantmentData
                }
                if (data.version >= 3) {
                    if (data.icons.isEmpty()) {
                        applicableItemIcons.addAll(DefaultConfigs.ICONS)
                    } else {
                        applicableItemIcons.addAll(data.icons)
                    }
                }
            } catch (e: SerializationException) {
                LOGGER.warn("Failed to parse ${file.name}, going to try legacy parsing. This file will be overwritten when saved.")
                try {
                    val old: Map<String, Map<String, Int>> = Json.decodeFromString(jsonString)
                    old.forEach { (key, value) ->
                        val identifier = Identifier.tryParse(key) ?: return@forEach
                        enchantmentConfigurations.clear()
                        enchantmentConfigurations[identifier] = EnchantmentData(
                            identifier = identifier,
                            priority = value["orderIndex"] ?: -1,
                            color = value["color"]?.let { Color(it) } ?: DefaultConfigs.getDefaultColor(identifier)
                        )
                    }
                    LOGGER.error("Legacy format read. Old index random values pointless. Suggest deleting settings file if no color values changed. File: ${file.absolutePath}")
                } catch (e: SerializationException) {
                    LOGGER.warn("Failed to read from legacy format. Using default values.")
                }
            }
        } catch (e: IOException) {
            LOGGER.debug("No configuration file found. Creating new one.")
        }
        save()
    }

    private fun clear() {
        enchantmentConfigurations.clear()
        applicableItemIcons.clear()
    }
}