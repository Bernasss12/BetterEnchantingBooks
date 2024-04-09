package dev.bernasss12.bebooks.util

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.config.ModConfig.applyTooltip
import dev.bernasss12.bebooks.gui.tooltip.IconTooltipComponent
import dev.bernasss12.bebooks.manage.ItemStackManager.getItemstack
import dev.bernasss12.bebooks.mixin.OrderedTextTooltipComponentAccessor
import dev.bernasss12.bebooks.util.text.IconTooltipDataText
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Items
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object Util {

    /*
        Icon utility methods.
     */

    @JvmStatic
    fun addTooltipIcons(tooltip: MutableList<Text>, enchantment: Enchantment) {
        if (MinecraftClient.getInstance().currentScreen is HandledScreen<*>) {
            if (getItemstack().item == Items.ENCHANTED_BOOK) {
                applyTooltip {
                    tooltip.add(
                        IconTooltipDataText(
                            ModConfig.getApplicableItemIcons(enchantment)
                        )
                    )
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
}