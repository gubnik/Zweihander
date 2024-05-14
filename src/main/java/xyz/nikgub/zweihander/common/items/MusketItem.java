package xyz.nikgub.zweihander.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nikgub.incandescent.Incandescent;
import xyz.nikgub.incandescent.util.EntityUtils;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.client.item_extensions.MusketClientExtension;
import xyz.nikgub.zweihander.common.registries.EnchantmentRegistry;
import xyz.nikgub.zweihander.common.registries.ItemRegistry;
import xyz.nikgub.zweihander.data.DamageTypeDatagen;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class MusketItem extends Item {

    public static final UUID STEP_INCREASE_UUID = UUID.fromString("7a502089-d89e-4d41-bdbf-fd1c8c3f4180");
    public static final UUID MOVEMENT_INCREASE_UUID = UUID.fromString("be2690d8-4886-4f3c-8fbe-c6dfc97c3c5c");

    public static final String AMMO_TAG = "___MUSKET_AMMO"; // string tag
    public static final String SPRINT_TAG = "___MUSKET_SPRINT"; // boolean tag

    public static final float DEFAULT_DAMAGE = 16f;
    public static final int   DEFAULT_RANGE = 150;
    public static final float DAMAGE_CAP = 50f;

    public MusketItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    public static final Set<ToolAction> ACTIONS = Set.of(ToolActions.AXE_DIG);

    public static @Nullable MusketAmmunitionItem getAmmoOrNull(@NotNull ItemStack itemStack)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemStack.getOrCreateTag().getString(AMMO_TAG)));
        if (item instanceof MusketAmmunitionItem ammunitionItem) return ammunitionItem;
        return null;
    }

    public static @NotNull MusketAmmunitionItem getAmmoOrDefault (@NotNull ItemStack itemStack)
    {
        MusketAmmunitionItem item = getAmmoOrNull(itemStack);
        if (item == null) return ItemRegistry.IRON_MUSKET_BALL.get();
        return item;
    }

    public static void setAmmo (@NotNull ItemStack itemStack, @NotNull MusketAmmunitionItem i)
    {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(i);
        if (i instanceof MusketAmmunitionItem) {
            assert location != null;
            itemStack.getOrCreateTag().putString(AMMO_TAG, location.toString());
        }
        setLoaded(itemStack, true);
    }

    public static void reload (@NotNull LivingEntity entity, @NotNull ItemStack itemStack)
    {
        ItemStack ammoStack = MusketAmmunitionItem.fetchStack(entity);
        if (!ammoStack.isEmpty())
        {
            setAmmo(itemStack, (MusketAmmunitionItem) ammoStack.getItem());
            if (!(entity instanceof Player player && player.isCreative())) ammoStack.shrink(1);
            Zweihander.Utils.playSound(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Zweihander.MOD_ID, "musket_load")), SoundSource.PLAYERS, 0.4f, 1);
        }
        else if (entity instanceof Player player && player.isCreative())
        {
            setAmmo(itemStack, ItemRegistry.IRON_MUSKET_BALL.get());
            Zweihander.Utils.playSound(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Zweihander.MOD_ID, "musket_load")), SoundSource.PLAYERS, 0.4f, 1);
        }
    }

    public static boolean isLoaded (@NotNull ItemStack itemStack)
    {
        return itemStack.getOrCreateTag().getInt("CustomModelData") != 0;
    }

    private static void setLoaded (@NotNull ItemStack itemStack, final boolean state)
    {
        itemStack.getOrCreateTag().putInt("CustomModelData", (state) ? 1 : 0);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int index, boolean isVanillaIndex)
    {
        if (isLoaded(itemStack)) itemStack.getOrCreateTag().putBoolean(SPRINT_TAG, (entity instanceof LivingEntity living && living.isSprinting() && living.getMainHandItem()==itemStack));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
    {
        if ((hand != InteractionHand.MAIN_HAND || (MusketAmmunitionItem.fetchStack(player).isEmpty())) && !player.isCreative()) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        if (isLoaded(itemStack)) return UseAnim.CUSTOM;
        return UseAnim.CROSSBOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack)
    {
        if (isLoaded(itemStack)) return 72000;
        return 20;
    }

    @Override
    public final void onStopUsing(ItemStack itemStack, LivingEntity entity, int ticks)
    {
        if (!isLoaded(itemStack)) loadManager(itemStack, entity, ticks);
        else
        {
            final MusketAmmunitionItem.Effect effect = MusketItem.getAmmoOrDefault(itemStack).getEffect();
            if (!(entity.level() instanceof ServerLevel level)) return;
            Map<LivingEntity, Float> toDamage = fireManager(itemStack, entity, ticks);
            for (LivingEntity living : toDamage.keySet())
            {
                final float damageMultiplier = effect.getModifier(entity, living);
                final float finalDamage = Mth.clamp(toDamage.get(living), 0, DAMAGE_CAP);
                living.hurt(Zweihander.Utils.makeDamageSource(DamageTypeDatagen.MUSKET_SHOT, level, entity, entity), finalDamage * damageMultiplier);
                living.knockback(0.5F * damageMultiplier, Mth.sin(entity.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(entity.getYRot() * ((float) Math.PI / 180F)));
            }
            final long currTick = Incandescent.clientTick;
            Incandescent.runShakeFor(1, (player -> Incandescent.clientTick > currTick + 15));
            setLoaded(itemStack, false);
        }
    }

    private void loadManager (ItemStack itemStack, LivingEntity entity, int ticks)
    {
        if (ticks == 0) reload(entity, itemStack);
    }

    private Map<LivingEntity, Float> fireManager (ItemStack itemStack, LivingEntity entity, int ticks)
    {
        Map<LivingEntity, Float> ret = new HashMap<>();
        if (!(entity.level() instanceof ServerLevel level)) return ret;
        final int scattershotLevel = itemStack.getEnchantmentLevel(EnchantmentRegistry.SCATTERSHOT.get());
        final int riflingLevel = itemStack.getEnchantmentLevel(EnchantmentRegistry.RIFLING.get());
        final float initialDamage = (scattershotLevel != 0) ? 10F : (riflingLevel != 0) ? 12F : DEFAULT_DAMAGE;
        final int iterations = (scattershotLevel != 0) ? 100 : (riflingLevel != 0) ? 250 : DEFAULT_RANGE;
        final double x = entity.getX();
        final double y = entity.getY() + entity.getEyeHeight() + 0.1;
        final double z = entity.getZ();
        final Vec3 look = entity.getLookAngle();
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + look.x / 1.5, y + look.y / 1.5, z + look.z / 1.5, 7, 0.05, 0.02, 0.05, 0.075);
        level.sendParticles(ParticleTypes.SMOKE, x + look.x / 1.5, y + look.y / 1.5, z + look.z / 1.5, 7, 0.05, 0.02, 0.05, 0.075);
        Zweihander.Utils.playSound(entity.level(), x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Zweihander.MOD_ID, "musket_shot")), SoundSource.PLAYERS, 0.35f, 1);
        for (int shot = 0; shot < scattershotLevel + 1; shot++)
        {
            double i = 1.2;
            float calculatedDamage = initialDamage;
            final double inaccuracy = (scattershotLevel != 0) ? Mth.clamp(1 - ((72000 - ticks) / 20f), 0.25f, 1.2f)
                    : (riflingLevel != 0) ? Mth.clamp(1 - ((72000 - ticks) / 20f), 0.005f, 0.8f)
                    : Mth.clamp(1 - ((72000 - ticks) / 20f), 0.05f, 1f);
            final Vec3 spreadModifiers = new Vec3(
                    ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 2,
                    ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 4,
                    ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 2
            );
            final Vec3 angles = look.add(spreadModifiers);
            ClipContext clip; Vec3 lookPos;
            while (EntityUtils.entityCollector(lookPos = new Vec3(x + angles.x * i, y + angles.y * i, z + angles.z * i), 0.25, entity.level()).isEmpty() &&
                    !level.getBlockState(new BlockPos(level.clip((clip = new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(i)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))).getBlockPos().getX(), level.clip(clip).getBlockPos().getY(), level.clip(clip).getBlockPos().getZ())
                    ).canOcclude() && i < iterations)
            {
                level.sendParticles((itemStack.isEnchanted()) ? ParticleTypes.ENCHANTED_HIT : ParticleTypes.CRIT, lookPos.x, lookPos.y, lookPos.z, 2, 0.01, 0, 0.01, 0);
                i += 0.2;
                if (riflingLevel != 0) calculatedDamage *= (1 + itemStack.getEnchantmentLevel(EnchantmentRegistry.RIFLING.get()) * 0.0015f);
            }
            for (LivingEntity living : EntityUtils.entityCollector(lookPos, 0.4, level))
            {
                final float dealtDamage = calculatedDamage;
                if (ret.computeIfPresent(living, (livingEntity, f) -> f + dealtDamage) == null) ret.put(living, dealtDamage);
            }
        }
        return ret;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity)
    {
        if (livingEntity instanceof Player player) player.getCooldowns().addCooldown(this, 10);
        return itemStack;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ACTIONS.contains(toolAction);
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player) {
        return !player.isCreative();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack itemStack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        if (slot == EquipmentSlot.MAINHAND)
        {
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", isLoaded(itemStack) ? 4 : 9, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", isLoaded(itemStack) ? -2.8D : -3.2D, AttributeModifier.Operation.ADDITION));
            if (isLoaded(itemStack) && itemStack.getEnchantmentLevel(EnchantmentRegistry.ASSAULT.get()) != 0 && itemStack.getOrCreateTag().getBoolean(SPRINT_TAG))
            {
                builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(STEP_INCREASE_UUID, "Weapon modifier", 1, AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_INCREASE_UUID, "Weapon modifier", 0.1, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return builder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @javax.annotation.Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (!isLoaded(itemStack)) return;
        String first = Component.translatable("tooltip.zweihander." + itemStack.getItem()).getString();
        MusketAmmunitionItem ammunitionItem = getAmmoOrNull(itemStack);
        if (ammunitionItem == null) ammunitionItem = ItemRegistry.IRON_MUSKET_BALL.get();
        ResourceLocation ammoLocation = ForgeRegistries.ITEMS.getKey(ammunitionItem);
        if (ammoLocation == null) return;
        String second = Component.translatable("item."+ammoLocation.getNamespace()+"."+ammoLocation.getPath()).getString();
        list.add(Component.literal(first + second).withStyle(ChatFormatting.DARK_GRAY));
    }


    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new MusketClientExtension());
    }
}
