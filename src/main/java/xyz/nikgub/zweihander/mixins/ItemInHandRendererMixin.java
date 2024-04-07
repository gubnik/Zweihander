package xyz.nikgub.zweihander.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.ZweihanderItem;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow
    public ItemRenderer itemRenderer;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    public void renderItemHead(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, boolean b, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo callbackInfo) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ZweihanderItem) {
            float mod = itemStack.getEnchantmentLevel(Zweihander.GIANT.get()) * 0.1f + 1f;
            poseStack.scale(mod, mod, mod);
            this.itemRenderer.renderStatic(livingEntity, itemStack, displayContext, b, poseStack, multiBufferSource, livingEntity.level(), i, OverlayTexture.NO_OVERLAY, livingEntity.getId() + displayContext.ordinal());
        }
    }
}
