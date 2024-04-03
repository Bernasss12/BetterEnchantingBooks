package dev.bernasss12.bebooks.model.color

import dev.bernasss12.bebooks.util.Util.decodeRGB
import dev.bernasss12.bebooks.util.Util.encodeRGB
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable
import java.awt.Color

enum class ColorSavingMode(
    val serialize: (value: Color) -> String,
    val deserialize: (String) -> Color
) : Translatable {
    INTEGER(
        { it.rgb.toString() },
        { Color(it.toInt()) },
    ),
    HEXADECIMAL(
        { String.format("#%06X", it.rgb and 0xFFFFFF) },
        { Color(Integer.decode(it)) },
    ),
    RGB_VALUES(
        { it.rgb.encodeRGB() },
        { Color(it.decodeRGB()) },
    );

    companion object {
        fun fromString(value: String): ColorSavingMode {
            return when (value.uppercase()) {
                INTEGER.name -> INTEGER
                HEXADECIMAL.name -> HEXADECIMAL
                RGB_VALUES.name -> RGB_VALUES
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }

    override fun toString(): String = name.lowercase()

    override fun getKey(): String = "enum.bebooks.color_saving_settings.${name.lowercase()}"
}