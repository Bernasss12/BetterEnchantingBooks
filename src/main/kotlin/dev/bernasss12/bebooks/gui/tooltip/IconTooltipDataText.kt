package dev.bernasss12.bebooks.gui.tooltip

import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.*

data class IconTooltipDataText(val enchantment: RegistryEntry<Enchantment>) : OrderedText, Text {

    override fun accept(visitor: CharacterVisitor?): Boolean = false

    override fun getStyle(): Style = Style.EMPTY

    override fun getContent(): TextContent = Text.literal("").content

    override fun getSiblings(): MutableList<Text> = mutableListOf()

    override fun asOrderedText(): OrderedText = this
}