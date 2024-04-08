package dev.bernasss12.bebooks.manage

import dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER
import dev.bernasss12.bebooks.config.DefaultConfigs
import dev.bernasss12.bebooks.config.DefaultConfigs.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.config.SavedData
import dev.bernasss12.bebooks.model.enchantment.EnchantmentData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.minecraft.util.Identifier
import java.awt.Color
import java.io.File
import java.io.IOException

/*
    The "version 2" spec of the enchantment data config will be as follows:
    - Object will be composed of "version" value as well as "enchantments" object.
        - "version" is pretty self-explanatory, this will help any future changes support older ones until deprecated.
        - "enchantments" will be an array of objects that will have all the necessary data.
    - Only enchantments with a meaningful order or color value will be saved.
 */

object EnchantmentDataManager {

    private const val FILE_VERSION = 2
    private val file: File = DefaultConfigs.CONFIG_DIR.resolve("enchantment_data.json").toFile()
    private val cache = hashMapOf<Identifier, EnchantmentData>()

    fun getData(key: Identifier): EnchantmentData {
        return cache.getOrPut(key) {
            EnchantmentData(
                identifier = key,
            )
        }
    }

    fun getData(key: String): EnchantmentData {
        val identifier = Identifier.tryParse(key)
        return if (identifier != null) {
            getData(identifier)
        } else {
            throw IllegalArgumentException("Invalid identifier: $key")
        }
    }

    fun getDefaultColorForId(identifier: Identifier): Color {
        return DefaultConfigs.ENCHANTMENTS[identifier]?.color ?: DEFAULT_BOOK_STRIP_COLOR
    }

    fun save() {
        // Filter values, so it only saved non-default values.
        val filteredValues = cache.values.filter {
            it.priority != -1 || it.color != getDefaultColorForId(it.identifier)
        }
        SavedData(FILE_VERSION, filteredValues).writeToFile(file)
    }

    fun load() {
        try {
            val jsonString = file.readText()
            try {
                // Try reading the data as the new format:
                val data: SavedData = SavedData.readFromJson(jsonString)
                cache.clear()
                data.enchantments.forEach { enchantmentData ->
                    cache[enchantmentData.identifier] = enchantmentData
                }
            } catch (e: SerializationException) {
                LOGGER.warn("Failed to parse ${file.name}, going to try legacy parsing. This file will be overwritten when saved.")
                try {
                    val old: Map<String, Map<String, Int>> = Json.decodeFromString(jsonString)
                    old.forEach { (key, value) ->
                        val identifier = Identifier.tryParse(key) ?: return@forEach
                        cache.clear()
                        cache[identifier] = EnchantmentData(
                            identifier = identifier,
                            priority = value["orderIndex"] ?: -1,
                            color = value["color"]?.let { Color(it) } ?: getDefaultColorForId(identifier)
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
}