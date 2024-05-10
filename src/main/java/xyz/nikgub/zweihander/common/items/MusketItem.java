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
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nikgub.incandescent.Incandescent;
import xyz.nikgub.incandescent.util.EntityUtils;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.client.item_extensions.MusketClientExtension;
import xyz.nikgub.zweihander.common.registries.ItemRegistry;
import xyz.nikgub.zweihander.datagen.DamageTypeDatagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class MusketItem extends Item {

    public static final String AMMO_TAG = "___MUSKET_AMMO";

    public static final float DEFAULT_DAMAGE = 20f;

    public MusketItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    public static final Set<ToolAction> ACTIONS = Set.of(ToolActions.AXE_DIG);

    public static @Nullable MusketAmmunitionItem getAmmo (@NotNull ItemStack itemStack)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemStack.getOrCreateTag().getString(AMMO_TAG)));
        if (item instanceof MusketAmmunitionItem ammunitionItem) return ammunitionItem;
        return null;
    }

    public static void setAmmo (@NotNull ItemStack itemStack, @NotNull String s)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
        if (item instanceof MusketAmmunitionItem) itemStack.getOrCreateTag().putString(AMMO_TAG, s);
        setLoaded(itemStack, true);
    }

    public static void setAmmo (@NotNull ItemStack itemStack, @NotNull Item i)
    {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(i);
        if (i instanceof MusketAmmunitionItem) {
            assert location != null;
            itemStack.getOrCreateTag().putString(AMMO_TAG, location.toString());
        }
        setLoaded(itemStack, true);
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
    {
        if (hand != InteractionHand.MAIN_HAND || (MusketAmmunitionItem.fetchStack(player).isEmpty())) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        if (isLoaded(itemStack)) return UseAnim.CUSTOM;
        return UseAnim.CUSTOM;
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
            ItemStack ammo = MusketAmmunitionItem.fetchStack(entity);
            if (ticks == 0 && !ammo.isEmpty())
            {
                ResourceLocation ammoLocation = ForgeRegistries.ITEMS.getKey(ammo.getItem());
                if (ammoLocation == null) return;
                setAmmo(itemStack, ammoLocation.toString());
                if (!(entity instanceof Player player && player.isCreative())) ammo.shrink(1);
                Zweihander.Utils.playSound(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Zweihander.MOD_ID, "musket_load")), SoundSource.PLAYERS, 0.4f, 1);
            }
            return;
        }
        MusketAmmunitionItem ammunitionItem = getAmmo(itemStack);
        if (ammunitionItem == null) ammunitionItem = ItemRegistry.IRON_MUSKET_BALL.get();
        double inaccuracy = Mth.clamp(1 - ((72000 - ticks) / 20f), 0.1f, 1f);
        Vec3 spread;
        if (inaccuracy == 0) spread = new Vec3(0, 0, 0);
        else spread = new Vec3(
                ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 2,
                ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 4,
                ThreadLocalRandom.current().nextDouble(-inaccuracy, inaccuracy) / 2
        );
        Vec3 angles = entity.getLookAngle().add(spread);
        Vec3 lookPos;
        double i = 1.2;
        final double x = entity.getX();
        final double y = entity.getY();
        final double z = entity.getZ();
        Zweihander.Utils.playSound(entity.level(), x, y + entity.getEyeHeight(), z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Zweihander.MOD_ID, "musket_shot")), SoundSource.PLAYERS, 0.35f, 1);
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,  x + angles.x/1.5, y + entity.getEyeHeight() + angles.y/1.5, z + angles.z/1.5, 7, 0.05, 0.02, 0.05, 0.075);
            level.sendParticles(ParticleTypes.SMOKE,  x + angles.x/1.5, y + entity.getEyeHeight() + angles.y/1.5, z + angles.z/1.5, 7, 0.05, 0.02, 0.05, 0.075);
            ClipContext clip;
            while (EntityUtils.entityCollector(lookPos = new Vec3(x + angles.x * i, y + 1.5 + angles.y * i, z + angles.z * i), 0.1, entity.level()).isEmpty() &&
                    !level.getBlockState(new BlockPos(level.clip((clip = new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(i)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))).getBlockPos().getX(), level.clip(clip).getBlockPos().getY(), level.clip(clip).getBlockPos().getZ())
                    ).canOcclude() && i < 150)
            {
                level.sendParticles(ParticleTypes.CRIT, lookPos.x, lookPos.y, lookPos.z, 1, 0.01, 0, 0.01, 0);
                i += 0.2;
            }
            for (LivingEntity living : EntityUtils.entityCollector(lookPos, 0.2, level))
            {
                living.hurt(Zweihander.Utils.makeDamageSource(DamageTypeDatagen.MUSKET_SHOT, level, entity, null), DEFAULT_DAMAGE * ammunitionItem.getEffect().getModifier(living));
                living.knockback(0.75F, Mth.sin(entity.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(entity.getYRot() * ((float)Math.PI / 180F)));
            }
        }
        final long currTick = Incandescent.clientTick;
        Incandescent.runShakeFor(1, (player -> Incandescent.clientTick > currTick + 10));
        setLoaded(itemStack, false);
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
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.2D, AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @javax.annotation.Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (!isLoaded(itemStack)) return;
        String first = Component.translatable("tooltip.zweihander." + itemStack.getItem()).getString();
        MusketAmmunitionItem ammunitionItem = getAmmo(itemStack);
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
