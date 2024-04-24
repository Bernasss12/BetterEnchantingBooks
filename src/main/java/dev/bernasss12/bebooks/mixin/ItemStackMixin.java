package dev.bernasss12.bebooks.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import dev.bernasss12.bebooks.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 10)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin {

    //FIXME find new way to sort enchantments in tooltip.

    @Shadow
    public abstract Item getItem();

    @Inject(method = "hasGlint", at = @At(value = "HEAD"), cancellable = true)
    protected void changeEnchantedBooksGlintReturn(CallbackInfoReturnable<Boolean> info) {
        if (!ModConfig.INSTANCE.getEnchantedBookGlint() && this.getItem() == Items.ENCHANTED_BOOK) {
            info.setReturnValue(false);
        }
    }
}
