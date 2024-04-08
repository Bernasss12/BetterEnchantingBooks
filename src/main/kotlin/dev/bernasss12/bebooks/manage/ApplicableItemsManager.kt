package dev.bernasss12.bebooks.manage

import dev.bernasss12.bebooks.config.DefaultConfigs
import dev.bernasss12.bebooks.config.ModConfig.applyTooltip
import dev.bernasss12.bebooks.gui.tooltip.IconTooltipComponent
import dev.bernasss12.bebooks.manage.ItemStackManager.getItemstack
import dev.bernasss12.bebooks.mixin.OrderedTextTooltipComponentAccessor
import dev.bernasss12.bebooks.util.text.IconTooltipDataText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

object ApplicableItemsManager {

    /*
        TODO also have an item list that is not the default!!
        TODO merge this cache to EnchantmentData.
        Be advised: Everytime settings change all enchantment data should be re-created so this can be re-calculated.
     */

    private val cachedApplicableEnchantments: MutableMap<Enchantment, List<ItemStack>> = hashMapOf()

    private fun Enchantment.computeApplicableItems(): List<ItemStack> =
        DefaultConfigs.ICONS.filter { currentItem -> isAcceptableItem(currentItem) }

    private fun getApplicableItems(enchantment: Enchantment): List<ItemStack> =
        cachedApplicableEnchantments.getOrPut(enchantment) { enchantment.computeApplicableItems() }

    @JvmStatic
    fun addTooltipIcons(tooltip: MutableList<Text>, enchantment: Enchantment) {
        if (MinecraftClient.getInstance().currentScreen is HandledScreen<*>) {
            if (getItemstack().item == Items.ENCHANTED_BOOK) {
                applyTooltip {
                    tooltip.add(IconTooltipDataText(getApplicableItems(enchantment)))
                }
            }
        }
    }

    @JvmStatic
    fun convertTooltipComponents(components: List<TooltipComponent>): List<TooltipComponent> {
        if (getItemstack().item != Items.ENCHANTED_BOOK) return components
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
        return IconTooltipComponent(dataText.icons)
    }

}