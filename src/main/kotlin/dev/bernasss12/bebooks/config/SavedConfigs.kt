package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.model.enchantment.EnchantmentData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.io.File
import kotlin.jvm.optionals.getOrNull

/**
 * The SavedConfigs class represents the configuration data for enchantments and icons.
 * The data is saved in JSON format.
 *
 *    The "version 3" spec of the enchantment data will add the following:
 *     - Object will now include an "icons" set of item identifiers.
 *         - This set should only be serialized if it differs from the default set.
 *
 *    The "version 2" spec of the enchantment data config will be as follows:
 *     - Object will be composed of "version" value as well as "enchantments" object.
 *         - "version" is pretty self-explanatory, this will help any future changes support older ones until deprecated.
 *         - "enchantments" will be an array of objects that will have all the necessary data.
 *     - Only enchantments with a meaningful order or color value will be saved.
 *
 * @param version The version number of the enchantment data config.
 * @param enchantments The list of enchantment data objects.
 * @param icons The set of ItemStack icons.
 */
@Serializable
data class SavedConfigs(
    val version: Int = CURRENT_VERSION,
    val enchantments: List<EnchantmentData> = emptyList(),
    val icons: Set<@Serializable(with = ItemStackSerializer::class) ItemStack> = emptySet()
) {
    companion object {
        private const val CURRENT_VERSION = 3

        private val json = Json {
            prettyPrint = true
            encodeDefaults = false
        }

        fun readFromJson(jsonString: String): SavedConfigs {
            return json.decodeFromString(jsonString)
        }
    }

    fun writeToFile(file: File) {
        val jsonString = json.encodeToString(serializer(), this)
        file.writeText(jsonString)
    }

    object ItemStackSerializer : KSerializer<ItemStack> {
        override val descriptor = PrimitiveSerialDescriptor("ItemStack", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): ItemStack {
            val value = decoder.decodeString()
            val identifier = Identifier.tryParse(value) ?: error("Error parsing identifier: $value")
            val item = Registries.ITEM.getOrEmpty(identifier).getOrNull() ?: error("Error retrieving item: $identifier")
            return item.defaultStack
        }

        override fun serialize(encoder: Encoder, value: ItemStack) {
            encoder.encodeString(Registries.ITEM.getId(value.item).toString())
        }
    }
}