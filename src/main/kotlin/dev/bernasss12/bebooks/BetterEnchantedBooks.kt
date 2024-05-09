package dev.bernasss12.bebooks

import dev.bernasss12.bebooks.config.BookColorManager.itemColorProvider
import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.config.SavedConfigManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Environment(EnvType.CLIENT)
object BetterEnchantedBooks {

    const val NAMESPACE = "bebooks"

    var isCurrentItemStackEnchantedBookItem: Boolean = false
        private set

    @Suppress("Unused")
    fun init() {
        ColorProviderRegistry.ITEM.register(itemColorProvider, Items.ENCHANTED_BOOK)
    }

    val LOGGER: Logger = LogManager.getLogger(BetterEnchantedBooks::class.java)

    /**
     * This method gets called once after every mod's entry points. By this point all enchantments should be registered.
     */
    @JvmStatic
    fun lateInit() {
        // TODO Make sure default configs and saved configs reload on resource pack reloading.
        ModConfig.loadProperties()
        SavedConfigManager.loadFromDisk()
    }

    fun updateItemstack(stack: ItemStack) {
        isCurrentItemStackEnchantedBookItem = ItemStack.areItemsEqual(stack, Items.ENCHANTED_BOOK.defaultStack)
    }
}