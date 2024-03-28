package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.BetterEnchantedBooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientMixin {
    @Inject(at = @At("RETURN"), method = "<init>")
    private void lateInit(CallbackInfo info) {
        BetterEnchantedBooks.lateInit();
    }
}