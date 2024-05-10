package dev.bernasss12.bebooks.util

import dev.bernasss12.bebooks.BetterEnchantedBooks
import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.config.ModConfig.applyTooltip
import dev.bernasss12.bebooks.config.SavedConfigManager
import dev.bernasss12.bebooks.gui.tooltip.IconTooltipComponent
import dev.bernasss12.bebooks.gui.tooltip.IconTooltipDataText
import dev.bernasss12.bebooks.mixin.OrderedTextTooltipComponentAccessor
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.function.Consumer

/**
 * Utility class containing various methods for tooltip manipulation and icon rendering.
 *
 * Note: This class is designed to be used in a client-side environment.
 */
@Environment(EnvType.CLIENT)
object Util {

    /*
        Append max enchantment level to tooltip line.
     */

    /**
     * Appends the maximum enchantment level to the given enchantment name if necessary.
     *
     * @param level The current enchantment level.
     * @param maxLevel The maximum enchantment level.
     * @param enchantmentName The mutable text object representing the enchantment name.
     */
    @JvmStatic
    fun appendMaxEnchantmentLevel(level: Int, maxLevel: Int, enchantmentName: MutableText) {
        if (maxLevel == 1 || level > maxLevel) return

        if (shouldRenderMaxEnchantmentLevel()) {
            enchantmentName.append("/").append(Text.translatable("enchantment.level.$maxLevel"))
        }
    }


    /**
     * Determines whether the maximum enchantment level should be rendered.
     * The method checks various conditions to determine if the maximum enchantment level
     * should be rendered based on the current configuration and item context.
     *
     * @return true if the maximum enchantment level should be rendered, false otherwise.
     */
    private fun shouldRenderMaxEnchantmentLevel(): Boolean {
        // Render on enchanted books if the current stack is enchanted book.
        if (
            ModConfig.showMaxEnchantmentLevel &&
            BetterEnchantedBooks.isCurrentItemStackEnchantedBookItem
        ) return true

        // Render on all items when current item is not empty and not enchanted book.
        if (
            ModConfig.showMaxEnchantmentLevelAllItems &&
            !BetterEnchantedBooks.isCurrentItemStackEmpty &&
            !BetterEnchantedBooks.isCurrentItemStackEnchantedBookItem
        ) return true

        // Render on enchantment table when the current itemstack is empty and current screen is enchantment table.
        if (
            ModConfig.showMaxEnchantmentLevelEnchantingTable &&
            BetterEnchantedBooks.isCurrentItemStackEmpty &&
            BetterEnchantedBooks.isCurrentScreenEnchantedScreen
        ) return true

        return false
    }

    /*
        Icon utility methods.
     */

    @JvmStatic
    fun addDummyText(tooltip: Consumer<Text>, enchantment: RegistryEntry<Enchantment>) {
        if (MinecraftClient.getInstance().currentScreen is HandledScreen<*> && BetterEnchantedBooks.isCurrentItemStackEnchantedBookItem) {
            applyTooltip {
                tooltip.accept(
                    IconTooltipDataText(
                        enchantment
                    )
                )
            }
        }
    }

    @JvmStatic
    fun convertTooltipComponents(components: List<TooltipComponent>): List<TooltipComponent> {
        if (!BetterEnchantedBooks.isCurrentItemStackEnchantedBookItem) return components
        applyTooltip {
            return components.map { component ->
                convertComponentIfPossible(component)
            }
        }
        return components
    }

    private fun convertComponentIfPossible(component: TooltipComponent): TooltipComponent {
        val orderedTextTooltipComponent = component as? OrderedTextTooltipComponent ?: return component
        val text = (orderedTextTooltipComponent as? OrderedTextTooltipComponentAccessor)?.text ?: return component
        val dataText = text as? IconTooltipDataText ?: return component
        return IconTooltipComponent(SavedConfigManager.getApplicableItems(dataText.enchantment.key.get().value))
    }

    /*
        Useful extension methods.
     */

    fun String.isInt(): Boolean = toIntOrNull() != null

    fun Int.encodeRGB(): String {
        val r = (this shr 16) and 0xFF
        val g = (this shr 8) and 0xFF
        val b = this and 0xFF
        return "rgb($r,$g,$b)"
    }

    fun Int.noAlpha() = this and 0xffffff

    fun String.decodeRGB(): Int {
        val values = this.removePrefix("rgb(").removeSuffix(")").split(",")
        val r = values[0].trim().toInt()
        val g = values[1].trim().toInt()
        val b = values[2].trim().toInt()

        return (r shl 16) + (g shl 8) + b
    }

    fun ItemStack.getStoredEnchantments(): ItemEnchantmentsComponent? {
        return get(DataComponentTypes.STORED_ENCHANTMENTS)
    }

}