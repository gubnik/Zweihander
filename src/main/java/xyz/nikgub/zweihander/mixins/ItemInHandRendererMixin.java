package xyz.nikgub.zweihander.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nikgub.zweihander.items.ZweihanderItem;
import xyz.nikgub.zweihander.mob_effect.InfusionMobEffect;
import xyz.nikgub.zweihander.registries.EnchantmentRegistry;

@SuppressWarnings("unused")
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow
    public ItemRenderer itemRenderer;

    @Inject(method = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    public void renderItemHead(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, boolean b, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo callbackInfo)
    {
        if (itemStack.isEmpty()) return;
        ItemStack toRender = itemStack.copy();
        if (!toRender.isEmpty() && toRender.getItem() instanceof ZweihanderItem) {
            float mod = toRender.getEnchantmentLevel(EnchantmentRegistry.GIANT.get()) * 0.1f + 1f;
            poseStack.scale(mod, mod, mod);
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL"), cancellable = true)
    public void renderItemTail(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, boolean b, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo callbackInfo)
    {
        if (itemStack.isEmpty()) return;
        if (livingEntity.getTicksUsingItem() > 0 && livingEntity.getUseItem() == livingEntity.getMainHandItem()) return;
        ItemStack toRender = itemStack.copy();
        for (MobEffectInstance instance : livingEntity.getActiveEffects())
        {
            if (instance.getEffect() instanceof InfusionMobEffect infusionMobEffect && toRender.getMaxStackSize() == 1 && toRender.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE)) {
                toRender.getOrCreateTag().putBoolean(InfusionMobEffect.INFUSION_TAG, true);
                toRender.getOrCreateTag().putFloat(InfusionMobEffect.RED_TAG, infusionMobEffect.getItemColors().r());
                toRender.getOrCreateTag().putFloat(InfusionMobEffect.GREEN_TAG, infusionMobEffect.getItemColors().g());
                toRender.getOrCreateTag().putFloat(InfusionMobEffect.BLUE_TAG, infusionMobEffect.getItemColors().b());
                toRender.getOrCreateTag().putFloat(InfusionMobEffect.ALPHA_TAG, infusionMobEffect.getItemColors().a());
                this.itemRenderer.renderStatic(livingEntity, toRender, displayContext, b, poseStack, multiBufferSource, livingEntity.level(), i,
                        OverlayTexture.NO_OVERLAY, livingEntity.getId() + displayContext.ordinal());
            }
        }
    }
}
