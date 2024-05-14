package xyz.nikgub.zweihander.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.client.animations.FlamingGuillotineAnimations;
import xyz.nikgub.zweihander.common.entities.FlamingGuillotineEntity;

public class FlamingGuillotineModel<T extends FlamingGuillotineEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Zweihander.MOD_ID, "flaming_guillotine"), "main");
    private final ModelPart bone;

    public FlamingGuillotineModel(ModelPart root) {
        this.bone = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 12).addBox(-8.0F, 1.0F, -0.5F, 16.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 48, 24);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.fallAnimationState, FlamingGuillotineAnimations.FALL, ageInTicks);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.bone;
    }
}