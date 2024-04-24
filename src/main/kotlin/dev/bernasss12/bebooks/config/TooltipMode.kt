package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.config.DefaultConfigs.DEFAULT_TOOLTIP_MODE
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable

enum class TooltipMode : Translatable {
    ENABLED,
    ON_SHIFT,
    DISABLED;

    companion object {
        fun fromString(string: String): TooltipMode {
            for (value in entries) {
                if (value.toString() == string) {
                    return value
                }
            }
            return DEFAULT_TOOLTIP_MODE
        }
    }

    override fun getKey() = "enum.bebooks.tooltip_settings.${toString().lowercase()}"
}