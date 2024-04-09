package dev.bernasss12.bebooks.mixin;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import dev.bernasss12.bebooks.config.ModConfig;
import dev.bernasss12.bebooks.manage.MaxEnchantmentManager;
import dev.bernasss12.bebooks.util.NBTUtil;
import dev.bernasss12.bebooks.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStack.class, priority = 10)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin {

    @ModifyVariable(
            method = "appendEnchantments",
            argsOnly = true,
            at = @At("HEAD")
    )
    private static NbtList appendEnchantmentHead(NbtList tag, List<Text> tooltip, NbtList enchantments) {
        if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
            return NBTUtil.sorted(enchantments, ModConfig.INSTANCE.getSortingMode(), ModConfig.INSTANCE.getKeepCursesBelow());
        }
        return tag;
    }

    @Dynamic("ItemStack.appendEnchantments' lambda")
    @Inject(
            at = @At(value = "HEAD"),
            method = "method_17869",
            remap = false
    )
    private static void setShowEnchantmentMaxLevel(List<Text> tooltip, NbtCompound tag, Enchantment enchantment, CallbackInfo info) {
        if (ModConfig.INSTANCE.getShowMaxEnchantmentLevel()) {
            MaxEnchantmentManager.setShowMaxLevel();
        }
    }

    @Dynamic("ItemStack.appendEnchantments' lambda")
    @Inject(
            at = @At(value = "TAIL"),
            method = "method_17869",
            remap = false
    )
    private static void addTooltipIcons(List<Text> tooltip, NbtCompound tag, Enchantment enchantment, CallbackInfo info) {
        Util.addTooltipIcons(tooltip, enchantment);
    }
}
