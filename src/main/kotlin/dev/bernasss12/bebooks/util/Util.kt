package dev.bernasss12.bebooks.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object Util {
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