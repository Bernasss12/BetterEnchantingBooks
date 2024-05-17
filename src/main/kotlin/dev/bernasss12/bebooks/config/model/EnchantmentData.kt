package dev.bernasss12.bebooks.config.model

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.util.Util.isInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.EnchantmentTags.CURSE
import net.minecraft.util.Identifier
import java.awt.Color

@Serializable
data class EnchantmentData(
    @Serializable(with = IdentifierSerializer::class)
    val identifier: Identifier,
    var priority: Int? = null,
    @Serializable(with = ColorSerializer::class)
    var color: Color? = null
) {
    val enchantment: Enchantment? by lazy {
        MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.ENCHANTMENT)?.get(identifier)
    }

    val translated: String by lazy {
        MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.ENCHANTMENT)?.getEntry(enchantment)?.value()?.description.toString()
    }

    val curse: Boolean
        get() = MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.ENCHANTMENT)?.getEntry(enchantment)?.isIn(CURSE) ?: false

//    companion object {
//        @JvmStatic
//        fun fromEnchantment(enchantment: Enchantment): EnchantmentData {
//            val identifier: Identifier = Registries.ENCHANTMENT.getId(enchantment) ?: error("Can't find id for $enchantment")
//            return SavedConfigManager.getEnchantmentData(identifier) ?: EnchantmentData(identifier)
//        }
//    }

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
