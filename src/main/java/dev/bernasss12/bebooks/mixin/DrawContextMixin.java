package dev.bernasss12.bebooks.mixin;

import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

import dev.bernasss12.bebooks.manage.ItemStackManager;
import dev.bernasss12.bebooks.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
@Environment(EnvType.CLIENT)
public abstract class DrawContextMixin {
    // called for Merchant items, but not for inventory items
    @Inject(
        method = "drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
        at = @At("HEAD")
    )
    private void setEnchantedItemStack(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        ItemStackManager.setItemstack(stack);
    }

    @Inject(
            method = "drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
            at = @At(value = "TAIL")
    )
    private void forgetEnchantedItemStack(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        ItemStackManager.setItemstack(ItemStack.EMPTY);
    }

    @ModifyVariable(
        at = @At("HEAD"),
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
        argsOnly = true
    )
    private List<TooltipComponent> convertTooltipComponents(List<TooltipComponent> components) {
        return Util.convertTooltipComponents(components);
    }
}
