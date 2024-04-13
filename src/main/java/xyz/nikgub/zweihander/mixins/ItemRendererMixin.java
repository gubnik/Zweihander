package xyz.nikgub.zweihander.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nikgub.zweihander.mob_effect.InfusionMobEffect;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow
    public ItemColors itemColors;

    @Inject(method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD"), cancellable = true)
    public void renderQuadListHead(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> bakedQuads, ItemStack itemStack, int p_115167_, int p_115168_, CallbackInfo callbackInfo){
        if(itemStack.getOrCreateTag().getBoolean(InfusionMobEffect.INFUSION_TAG)) {
            PoseStack.Pose posestack$pose = poseStack.last();
            for (BakedQuad bakedquad : bakedQuads) {
                vertexConsumer.putBulkData(posestack$pose, bakedquad,
                        itemStack.getOrCreateTag().getFloat(InfusionMobEffect.RED_TAG),
                        itemStack.getOrCreateTag().getFloat(InfusionMobEffect.GREEN_TAG),
                        itemStack.getOrCreateTag().getFloat(InfusionMobEffect.BLUE_TAG),
                        itemStack.getOrCreateTag().getFloat(InfusionMobEffect.ALPHA_TAG) / 2f
                        , 255, p_115168_, false);
            }
            callbackInfo.cancel();
        }
    }
}
