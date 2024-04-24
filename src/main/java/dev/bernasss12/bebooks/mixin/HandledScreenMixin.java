package dev.bernasss12.bebooks.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;

import com.llamalad7.mixinextras.sugar.Local;
import dev.bernasss12.bebooks.BetterEnchantedBooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public abstract class HandledScreenMixin {

    @Inject(
        method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getTooltipFromItem(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"
        )
    )
    private void setEnchantedItemStack(DrawContext context, int x, int y, CallbackInfo ci, @Local ItemStack itemStack) {
        BetterEnchantedBooks.INSTANCE.updateItemstack(itemStack);
    }

    //    @Inject( todo
    //            method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V",
    //            at = @At(value = "TAIL")
    //    )
    //    private void forgetEnchantedItemStack(DrawContext context, int x, int y, CallbackInfo ci) {
    //        ItemStackManager.setItemstack(ItemStack.EMPTY);
    //    }
}
