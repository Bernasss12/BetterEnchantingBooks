package dev.bernasss12.bebooks.util.text

import net.minecraft.item.ItemStack
import net.minecraft.text.*

data class IconTooltipDataText(val icons: Set<ItemStack>) : OrderedText, Text {

    override fun accept(visitor: CharacterVisitor?): Boolean = false

    override fun getStyle(): Style = Style.EMPTY

    override fun getContent(): TextContent = Text.literal("").content

    override fun getSiblings(): MutableList<Text> = mutableListOf()

    override fun asOrderedText(): OrderedText = this
}