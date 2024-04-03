package dev.bernasss12.bebooks.model.enchantment

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.manage.EnchantmentDataManager
import dev.bernasss12.bebooks.model.color.ColorSavingMode
import dev.bernasss12.bebooks.util.NBTUtil.getEnchantmentID
import dev.bernasss12.bebooks.util.Util.isInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.client.resource.language.I18n
import net.minecraft.enchantment.Enchantment
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.awt.Color

@Serializable
data class EnchantmentData(
    @Serializable(with = IdentifierSerializer::class)
    val identifier: Identifier,
    var priority: Int = -1,
    @Serializable(with = ColorSerializer::class)
    var color: Color = EnchantmentDataManager.getDefaultColorForId(identifier)
) {
    val enchantment: Enchantment? by lazy {
        Registries.ENCHANTMENT[identifier]
    }

    val translated: String by lazy {
        enchantment?.translationKey?.let { key ->
            I18n.translate(key)
        } ?: identifier.toString()
    }

    val curse: Boolean
        get() = enchantment?.isCursed ?: false

    // TODO add way to check if current value is the same as default, depending on identifier.
    // TODO extract needed data as interface and have real and fake enchantments

    companion object {
        @JvmStatic
        fun fromNBT(element: NbtElement): EnchantmentData {
            val id: String = element.getEnchantmentID()
            val identifier: Identifier = Identifier.tryParse(id) ?: error("Can't parse id")
            return EnchantmentDataManager.getData(identifier)
        }
    }

    object IdentifierSerializer : KSerializer<Identifier> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Identifier {
            val (namespace, path) = decoder.decodeString().split(":", limit = 2)
            return Identifier.of(namespace, path) ?: error("Cannot get identifier of: $namespace:$path")
        }

        override fun serialize(encoder: Encoder, value: Identifier) {
            encoder.encodeString(value.toString())
        }
    }

    object ColorSerializer : KSerializer<Color> {
        override val descriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): Color {
            val value = decoder.decodeString()
            return when {
                value.startsWith("rgb") -> ColorSavingMode.RGB_VALUES.deserialize(value)
                value.startsWith("#") -> ColorSavingMode.HEXADECIMAL.deserialize(value)
                value.isInt() -> ColorSavingMode.INTEGER.deserialize(value)
                else -> throw SerializationException("[$value] is not a valid color string.")
            }
        }

        override fun serialize(encoder: Encoder, value: Color) {
            encoder.encodeString(
                ModConfig.colorSavingMode.serialize(value)
            )
        }

    }
}
