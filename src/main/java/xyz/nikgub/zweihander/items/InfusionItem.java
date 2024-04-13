package xyz.nikgub.zweihander.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import xyz.nikgub.zweihander.mob_effect.InfusionMobEffect;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class InfusionItem extends Item {

    private final InfusionMobEffect effect;

    public InfusionItem(Properties properties, InfusionMobEffect effect) {
        super(properties);
        this.effect = effect;
    }

    public InfusionMobEffect getEffect() {
        return effect;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
    {
        if (hand != InteractionHand.OFF_HAND || player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack)
    {
        return 30;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().toList();
        for (MobEffectInstance instance : effectInstances)
        {
            if (instance.getEffect() instanceof InfusionMobEffect infusionMobEffect)
            {
                livingEntity.removeEffect(infusionMobEffect);
            }
        }
        livingEntity.addEffect(new MobEffectInstance(effect, 1200, 5));
        itemStack.shrink(1);
        return itemStack;
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new InfusionClientExtension());
    }

    public static class InfusionClientExtension implements IClientItemExtensions
    {
        public static HumanoidModel.ArmPose MAIN = HumanoidModel.ArmPose.create("infusion_item", true, (model, entity, arm) -> {
            int tick;
            if(entity instanceof Player player) tick = player.getUseItemRemainingTicks();
            else return;
            if(tick <= 0) return;
            ModelPart other = (arm.equals(HumanoidArm.RIGHT)) ? model.rightArm : model.leftArm;
            ModelPart main  = (arm.equals(HumanoidArm.RIGHT)) ? model.leftArm  : model.rightArm;
            main.yRot = -0.8F;
            main.zRot = 0.5F;
            main.xRot = -0.97079635F;
            other.xRot = main.xRot;
            other.zRot = 1F;
            float f = (float) CrossbowItem.getChargeDuration(player.getUseItem());
            float f1 = Mth.clamp((float)player.getTicksUsingItem(), 0.0F, f);
            float f2 = f1 / f;
            other.xRot = Mth.lerp(-f2, other.xRot, (-(float)Math.PI / 2F));
        });

        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            return MAIN;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
            if (player.getUseItemRemainingTicks() > 0) {
                this.applyItemArmTransform(poseStack, arm, swingProcess);
                return true;
            }
            return false;
        }

        private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float v) {
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            poseStack.translate((float)i * 0.56F, -0.52F + v * -0.6F, -0.72F);
        }
    }
}
