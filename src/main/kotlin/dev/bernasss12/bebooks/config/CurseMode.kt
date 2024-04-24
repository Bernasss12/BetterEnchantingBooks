package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.config.DefaultConfigs.DEFAULT_CURSE_MODE
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