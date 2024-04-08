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

@Serializable
data class SavedData(
    val version: Int,
    val enchantments: List<EnchantmentData> = listOf(),
    val icons: Set<@Serializable(with = ItemStackSerializer::class) ItemStack> = setOf()
) {
    companion object {
        private val json = Json {
            prettyPrint = true
            encodeDefaults = false
        }

        fun readFromJson(jsonString: String): SavedData {
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