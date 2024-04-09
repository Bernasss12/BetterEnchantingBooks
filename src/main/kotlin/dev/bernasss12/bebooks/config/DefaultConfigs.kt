package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER
import dev.bernasss12.bebooks.BetterEnchantedBooks.NAMESPACE
import dev.bernasss12.bebooks.model.color.ColorSavingMode
import dev.bernasss12.bebooks.model.enchantment.EnchantmentData
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.awt.Color
import java.io.IOException
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

object DefaultConfigs {
    val CONFIG_DIR: Path = FabricLoader.getInstance().configDir.resolve(NAMESPACE)

    // Enchantment colors and sorting priorities
    private var ENCHANTMENTS: Map<Identifier, EnchantmentData> = mapOf()
    // Icons to check and/or draw on the tooltip
    var ICONS: Set<ItemStack> = setOf()
        private set

    // Sorting settings
    val DEFAULT_SORTING_MODE: SortingMode = SortingMode.ALPHABETICALLY
    const val DEFAULT_KEEP_CURSES_BELOW: Boolean = true

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

    /**
     * Returns the default color for a given identifier.
     *
     * @param identifier the identifier used to retrieve the default color
     * @return the default color for the given identifier, or the default book strip color if no color is found
     */
    fun getDefaultColor(identifier: Identifier): Color {
        return ENCHANTMENTS[identifier]?.color ?: DEFAULT_BOOK_STRIP_COLOR
    }

    /**
     * Loads the default mod configuration.
     * This can technically be overwritten by a resource pack, for example, in a mod pack.
     * Located at bebooks:default.json
     *
     * @param resourceManager The resource manager to retrieve the default configuration from.
     * @param enchantments The map to store the loaded enchantment data.
     * @param icons The set to store the loaded item stack icons.
     */
    private fun loadDefaultModConfig(
        resourceManager: ResourceManager,
        enchantments: MutableMap<Identifier, EnchantmentData>,
        icons: MutableSet<ItemStack>
    ) {
        Identifier(NAMESPACE, "default.json")
            .let { defaultId -> resourceManager.getResource(defaultId) }
            .getOrNull()
            ?.let { default ->
                try {
                    LOGGER.info("Reading default enchantment configs.")
                    val jsonString = default.reader.readText()
                    val data = SavedConfigs.readFromJson(jsonString)
                    data.enchantments.forEach { current ->
                        enchantments[current.identifier] = current
                    }
                    data.icons.forEach { current ->
                        icons += current
                    }

                    // Check if any registered enchantment doesn't have a default value.
                    Registries.ENCHANTMENT.keys.map { key ->
                        key.value
                    }.filterNot { key ->
                        enchantments.contains(key)
                    }.let { notDefaulted ->
                        if (notDefaulted.isNotEmpty()) {
                            LOGGER.warn("The following enchantments do not have default settings [${notDefaulted.count()}]:")
                            LOGGER.warn(notDefaulted.joinToString(separator = ", ", prefix = "[", postfix = "]") { it.toString() })
                        }
                    }

                    LOGGER.info("Successfully read default configs.")
                } catch (e: IOException) {
                    LOGGER.error("Was not able to read own default enchantment configs. Default original color will be used.")
                }
            } ?: LOGGER.error("Did not find the default mod enchantment configuration.")
    }

    /**
     * Loads additive mod configurations from any resource pack or mod (in the respective resource pack).
     * Any name works, ideally something related with the resource pack or mod that adds it is recommended so there are no conflicts.
     * Located at: bebooks:bebooks/<anything>.json
     *
     * @param resourceManager The resource manager to retrieve the default configuration from.
     * @param enchantments The map to store the loaded enchantment data.
     * @param icons The set to store the loaded item stack icons.
     */
    private fun loadExternalDefaultModConfig(
        resourceManager: ResourceManager,
        enchantments: MutableMap<Identifier, EnchantmentData>,
        icons: MutableSet<ItemStack>
    ) {
        LOGGER.info("Checking for extra configs.")
        resourceManager.findResources(NAMESPACE) { true }
            .entries.filterNot { it.key.namespace == NAMESPACE }
            .takeIf { it.isNotEmpty() }
            ?.forEach {
                try {
                    LOGGER.info("Found configs from: ${it.value.pack.name}. Loading.")
                    val jsonString: String = it.value.reader.readText()
                    val data: SavedConfigs = SavedConfigs.readFromJson(jsonString)
                    data.enchantments.forEach { current: EnchantmentData ->
                        enchantments[current.identifier] = current
                    }
                    data.icons.forEach { current ->
                        icons += current
                    }
                } catch (e: IOException) {
                    LOGGER.error("Error while trying to load: ${it.value.pack.name}; ${it.key}")
                }
            } ?: LOGGER.info("No extra configs found.")
    }

    /**
     * Loads the default configurations for the mod.
     */
    fun loadDefaultConfigurations() {
        val resource = MinecraftClient.getInstance().resourceManager
        val enchantments = mutableMapOf<Identifier, EnchantmentData>()
        val icons = mutableSetOf<ItemStack>()

        loadDefaultModConfig(
            resource,
            enchantments,
            icons
        )

        loadExternalDefaultModConfig(
            resource,
            enchantments,
            icons
        )

        this.ENCHANTMENTS = enchantments.toMap()
        this.ICONS = icons.toSet()
    }
}