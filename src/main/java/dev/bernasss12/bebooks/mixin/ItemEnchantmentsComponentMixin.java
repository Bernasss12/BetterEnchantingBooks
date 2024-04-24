package dev.bernasss12.bebooks.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import dev.bernasss12.bebooks.config.ModConfig;
import dev.bernasss12.bebooks.config.SortingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}