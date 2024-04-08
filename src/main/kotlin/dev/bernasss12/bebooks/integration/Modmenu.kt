package dev.bernasss12.bebooks.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.bernasss12.bebooks.config.ModConfigScreenBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class Modmenu : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory {
        return@ConfigScreenFactory ModConfigScreenBuilder.getConfigScreen()
    }
}
