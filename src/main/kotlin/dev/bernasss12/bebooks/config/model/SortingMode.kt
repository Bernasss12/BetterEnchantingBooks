package dev.bernasss12.bebooks.config.model

import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_SORTING_MODE
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable

enum class SortingMode : Translatable {
    ALPHABETICALLY,
    CUSTOM,
    DISABLED;

    companion object {
        fun fromString(string: String): SortingMode {
            for (value in SortingMode.entries) {
                if (value.toString() == string) {
                    return value
                }
            }
            return DEFAULT_SORTING_MODE
        }
    }

    override fun getKey() = "enum.bebooks.sorting_settings.${toString().lowercase()}"
}