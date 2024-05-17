package dev.bernasss12.bebooks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import com.llamalad7.mixinextras.sugar.Local;
import dev.bernasss12.bebooks.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
@Environment(EnvType.CLIENT)
public abstract class EnchantmentMixin {
    @Shadow
    public abstract int getMaxLevel();

//    @Inject(
//            at = @At(value = "TAIL"),
//            method = "getName(I)Lnet/minecraft/text/Text;"
//    )
//    private void appendMaxEnchantmentLevel(int level, CallbackInfoReturnable<Text> info, @Local MutableText enchantmentName) {
//        Util.appendMaxEnchantmentLevel(level, this.getMaxLevel(), enchantmentName);
//    }
}
