package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER
import dev.bernasss12.bebooks.BetterEnchantedBooks.NAMESPACE
import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.config.model.EnchantmentData
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import java.awt.Color
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.jvm.optionals.getOrNull

object SavedConfigManager {

    val CONFIG_DIR: Path = FabricLoader.getInstance().configDir.resolve(NAMESPACE).also { it.createDirectories() }
    private val USER_CONFIG_FILE = CONFIG_DIR.resolve("enchantment_data.json").toFile()
    private const val DEFAULT_CONFIG_FILE = "default.json"

    private val resourceManager = MinecraftClient.getInstance().resourceManager

    // Currently user-set configurations
    private var current: SavedConfigs = SavedConfigs.DEFAULT

    // Default enchantment configurations shipped with mod
    private val original: SavedConfigs by lazy {
        resourceManager.getResource(Identifier.of(NAMESPACE, DEFAULT_CONFIG_FILE))
            .getOrNull()?.let { resource ->
                try {
                    LOGGER.info("Reading default enchantment configs.")
                    val jsonString = resource.reader.readText()
                    return@let SavedConfigs.readFromJson(jsonString).also {
                        LOGGER.info("Successfully read default configs.")
                    }
                } catch (e: IOException) {
                    LOGGER.error("Was not able to read own default enchantment configs. Default original color will be used.")
                    return@let null
                }
            } ?: let {
            LOGGER.error("Did not find the default mod enchantment configuration.")
            error("There is a huge bug in Better Enchanted Books. Please disable the mod and report it to the author.")
        }
    }

    // Any configuration loaded from other mods
    private val extras: MutableMap<Identifier, SavedConfigs> = loadExtraConfigs()

    /**
     * Loads the extra configurations from the resource manager.
     *
     * @return a mutable map of loaded extra configurations
     */
    private fun loadExtraConfigs() = resourceManager.findResources(NAMESPACE) { true }.mapNotNull {
        try { // TODO RERUN THIS AFTER RESOURCE RELOADING
            LOGGER.info("Found configs from: ${it.value.pack.id}. Loading.")
            val jsonString: String = it.value.reader.readText()
            it.key to SavedConfigs.readFromJson(jsonString)
        } catch (e: IOException) {
            LOGGER.error("Error while trying to load: ${it.value.pack.id}; ${it.key}")
            null
        }
    }.toMap().toMutableMap()

    private val unmanagedEnchantmentData = mutableListOf<EnchantmentData>()

    /* Enchantment colors */

    /**
     * Returns the default color for a given identifier.
     *
     * @param identifier the identifier used to retrieve the default color
     * @return the default color for the given identifier, or the default book strip color if no color is found
     */
    fun getDefaultColor(identifier: Identifier): Color {
        return extras.firstNotNullOfOrNull { config ->
            // Try to get the enchantment data color from any configs here.
            config.value.enchantments.firstOrNull { enchantment ->
                enchantment.identifier == identifier
            }
        }?.color ?: original.enchantments.firstOrNull { enchantment ->
            // Try to get the enchantment data from the original defaults.
            enchantment.identifier == identifier
        }?.color ?: DEFAULT_BOOK_STRIP_COLOR // Return original book color.
    }

    private val colorCache: MutableMap<Identifier, Color?> = mutableMapOf()

    /**
     * Gets the color of an enchantment based on its identifier.
     *
     * @param identifier the identifier of the enchantment
     * @return The color of the enchantment, or null if the identifier is not found
     */
    fun getEnchantmentColor(identifier: Identifier): Color? {
        return colorCache.computeIfAbsent(identifier) {
            current.enchantments.firstOrNull { it.identifier == identifier }?.color
                ?: getDefaultColor(identifier)
        }
    }


    /* Applicable items */

    private val applicableIconsCache: MutableMap<Identifier, Set<ItemStack>> = mutableMapOf()

    /**
     * Returns the default applicable items.
     *
     * This method concatenates the icons from the original item and all the icons from the extras,
     * and returns them as a set of `ItemStack`s.
     *
     * @return The set of ItemStacks containing the icons from the original item and extras.
     */
    private fun getDefaultApplicableItems(): Set<ItemStack> {
        return (original.icons + extras.flatMap { it.value.icons })
    }

    /**
     * Retrieves the applicable items for a given identifier.
     *
     * This method checks if the identifier exists in the enchantment registry. If it does, it retrieves the enchantment
     * and filters the items from the current icons or the default applicable items. The filtered items are returned as a
     * set of ItemStacks.
     *
     * @param identifier The identifier to retrieve the applicable items for.
     * @return a set of `ItemStacks` containing the applicable items for the given identifier, or an empty set if the
     * identifier does not exist in the enchantment registry.
     */
    fun getApplicableItems(identifier: Identifier): Set<ItemStack> {
        return applicableIconsCache.computeIfAbsent(identifier) {
            MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.ENCHANTMENT)?.get(identifier)?.let { enchantment ->
                (current.icons.ifEmpty { null } ?: getDefaultApplicableItems()).filter {
                    enchantment.isAcceptableItem(it)
                }.toSet()
            } ?: emptySet()
        }
    }

    /* Enchantment Data */

    /**
     * Retrieves a set of all `EnchantmentData` objects.
     *
     * @return The set of `EnchantmentData` objects.
     */
    fun getAllEnchantmentData(): Set<EnchantmentData> {
        return (current.enchantments + original.enchantments + extras.values.flatMap { it.enchantments }).distinctBy { it.identifier }.toSet()
    }

    /**
     * Retrieves the `EnchantmentData` object with the specified identifier.
     *
     * @param identifier The identifier of the enchantment.
     * @return The `EnchantmentData` object with the specified identifier, or null if not found.
     */
    fun getEnchantmentData(identifier: Identifier): EnchantmentData? {
        return getAllEnchantmentData().find { it.identifier == identifier }
    }

    /* Data persistence */

    fun loadFromDisk() {
        reload()

        try {
            val jsonString = USER_CONFIG_FILE.readText()
            current = SavedConfigs.readFromJson(jsonString)

            // Add all registered enchantments to the list even if there is no stored data.
            unmanagedEnchantmentData.clear() // TODO finish up unmanaged data!
            unmanagedEnchantmentData.addAll(
                let {
                    val managed = getAllEnchantmentData().map { it.identifier }
                    MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.ENCHANTMENT)?.keys?.map { it.value }?.filterNot { it in managed }?.map { identifier ->
                        EnchantmentData(identifier)
                    } ?: emptySet()
                }
            )

            if (unmanagedEnchantmentData.isNotEmpty()) {
                LOGGER.warn("The following enchantments were not found in any configuration file: ${unmanagedEnchantmentData.joinToString { it.translated }}")
            }
        } catch (e: IOException) {
            LOGGER.debug("No configuration file found. Creating new one.")
            current.writeToFile(USER_CONFIG_FILE)
        }

        extras.clear()
        extras.putAll(loadExtraConfigs())
    }

    fun saveToDisk() {
        LOGGER.info("Loaded settings.")
        current.writeToFile(USER_CONFIG_FILE)
    }

    fun reload() {
        colorCache.clear()
        applicableIconsCache.clear()

        ModConfig.reloadPriorityLists()
    }
}