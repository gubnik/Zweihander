package xyz.nikgub.zweihander.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nikgub.zweihander.common.mob_effect.InfusionMobEffect;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(
            method = {"renderQuadList"},
            at = {@At(value = "HEAD")}, cancellable = true
    )
    public void renderQuadListHead(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> bakedQuads, ItemStack itemStack, int p_115167_, int p_115168_, CallbackInfo callbackInfo){
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return;
        if(tag.getBoolean(InfusionMobEffect.INFUSION_TAG)) {
            PoseStack.Pose posestack$pose = poseStack.last();
            for (BakedQuad bakedquad : bakedQuads) {
                vertexConsumer.putBulkData(posestack$pose, bakedquad,
                        tag.getFloat(InfusionMobEffect.RED_TAG),
                        tag.getFloat(InfusionMobEffect.GREEN_TAG),
                        tag.getFloat(InfusionMobEffect.BLUE_TAG),
                        tag.getFloat(InfusionMobEffect.ALPHA_TAG) / 2f
                        , 255, p_115168_, true);
            }
            callbackInfo.cancel();
        }
    }
}
