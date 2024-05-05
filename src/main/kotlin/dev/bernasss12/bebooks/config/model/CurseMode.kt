package dev.bernasss12.bebooks.config.model

import dev.bernasss12.bebooks.config.ModConfig.Defaults.DEFAULT_CURSE_MODE
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable

enum class CurseMode : Translatable {
    ABOVE,
    BELOW,
    IGNORE;

    companion object {
        fun fromString(string: String): CurseMode {
            for (value in CurseMode.entries) {
                if (value.toString() == string) {
                    return value
                }
            }
            return DEFAULT_CURSE_MODE
        }
    }

    override fun getKey() = "enum.bebooks.curse_mode.${toString().lowercase()}"
}