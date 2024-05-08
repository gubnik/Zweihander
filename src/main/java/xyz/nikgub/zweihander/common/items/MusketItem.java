package xyz.nikgub.zweihander.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import xyz.nikgub.incandescent.util.EntityUtils;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.client.item_extensions.MusketClientExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class MusketItem extends Item {

    public static final float DEFAULT_DAMAGE = 14f;

    public MusketItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    public static final Set<ToolAction> ACTIONS = Set.of(ToolActions.AXE_DIG);

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ACTIONS.contains(toolAction);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack itemStack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        if (slot == EquipmentSlot.MAINHAND)
        {
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", DEFAULT_DAMAGE / (2 * (isLoaded(itemStack) ? 2 : 1)) + 1, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.2D, AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }

    public static boolean isLoaded (ItemStack itemStack)
    {
        return itemStack.getOrCreateTag().getInt("CustomModelData") != 0;
    }

    public static void setLoaded (ItemStack itemStack, boolean state)
    {
        itemStack.getOrCreateTag().putInt("CustomModelData", (state) ? 1 : 0);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
    {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        if (isLoaded(itemStack)) return UseAnim.CUSTOM;
        return UseAnim.CROSSBOW;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack itemStack, int ticks)
    {
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack)
    {
        if (isLoaded(itemStack)) return 72000;
        return 20;
    }

    @Override
    public void onStopUsing(ItemStack itemStack, LivingEntity entity, int ticks)
    {
        if (!isLoaded(itemStack))
        {
            if (ticks == 0) setLoaded(itemStack, true);
            return;
        }
        double inaccuracy = Mth.clamp(1 - ((72000 - ticks) / 20f), 0f, 1f);
        Vec3 spread;
        if (inaccuracy == 0) spread = new Vec3(0, 0, 0);
        else spread = new Vec3(
                ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 4,
                ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 10,
                ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 4
        );
        Vec3 angles = entity.getLookAngle().add(spread);
        List<Vec3> ray = Zweihander.Utils.launchRay(entity.getEyePosition(), angles, 100, 0.2);
        float damageModifier = 1f;
        if (entity.level() instanceof ServerLevel level)
        {
            for (Vec3 pos : ray)
            {
                // TODO : doesnt work, needs fixing
                if (entity.level().getBlockState(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)).canOcclude()) break;
                level.sendParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 1, 0.01, 0, 0.01, 0);
                for (LivingEntity target : EntityUtils.entityCollector(pos, 0.2, level))
                {
                    target.hurt(new DamageSource(level.registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(DamageTypes.GENERIC), entity), DEFAULT_DAMAGE * damageModifier);
                    target.knockback(0.5 + damageModifier, Math.sin(entity.getYRot() * ((float)Math.PI / 180F)), -Math.cos(entity.getYRot() * ((float)Math.PI / 180F)));
                }
                damageModifier *= 0.98;
            }
        }
        setLoaded(itemStack, false);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity)
    {
        if (livingEntity instanceof Player player) player.getCooldowns().addCooldown(this, 10);
        return itemStack;
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new MusketClientExtension());
    }
}
