package dev.bernasss12.bebooks.mixin;

import java.util.function.Consumer;

import net.minecraft.client.item.TooltipType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.bernasss12.bebooks.config.ModConfig;
import dev.bernasss12.bebooks.config.SortingMode;
import dev.bernasss12.bebooks.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ItemEnchantmentsComponent.class)
public abstract class ItemEnchantmentsComponentMixin {
    @Inject(
            method = "getTooltipOrderList",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void replaceEnchantmentOrderList(
            @Nullable RegistryWrapper.WrapperLookup registryLookup,
            RegistryKey<Registry<Enchantment>> registryRef,
            TagKey<Enchantment> tooltipOrderTag,
            CallbackInfoReturnable<RegistryEntryList<Enchantment>> info
    ) {
        if (ModConfig.INSTANCE.getSortingMode() != SortingMode.DISABLED) {
            info.setReturnValue(
                    ModConfig.INSTANCE.getEnchantmentTooltipPriorityList()
            );
        }
    }

    @Inject(
            method = "appendTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void appendDummyIconTextData(
            Item.TooltipContext context,
            Consumer<Text> tooltip,
            TooltipType type,
            CallbackInfo ci,
            @Local RegistryEntry<Enchantment> enchantment
    ) {
        Util.addDummyText(tooltip, enchantment);
    }
}