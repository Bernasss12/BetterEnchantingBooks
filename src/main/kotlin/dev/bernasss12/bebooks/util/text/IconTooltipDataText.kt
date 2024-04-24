package dev.bernasss12.bebooks.util.text

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.text.*
import net.minecraft.util.Identifier

data class IconTooltipDataText(val enchantment: Identifier) : OrderedText, Text {

    override fun accept(visitor: CharacterVisitor?): Boolean = false

    override fun getStyle(): Style = Style.EMPTY

    override fun getContent(): TextContent = Text.literal("").content

    override fun getSiblings(): MutableList<Text> = mutableListOf()

    override fun asOrderedText(): OrderedText = this
}