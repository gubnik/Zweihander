/*
        Zweihander, Minecraft modification
        Copyright (C) 20224  nikgub

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.nikgub.zweihander;

import com.mojang.logging.LogUtils;
import net.minecraft.DetectedVersion;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.nikgub.zweihander.client.models.FlamingGuillotineModel;
import xyz.nikgub.zweihander.client.renderers.FlamingGuillotineRenderer;
import xyz.nikgub.zweihander.common.items.InfusionItem;
import xyz.nikgub.zweihander.common.items.MusketItem;
import xyz.nikgub.zweihander.common.items.ZweihanderItem;
import xyz.nikgub.zweihander.common.mob_effect.InfusionMobEffect;
import xyz.nikgub.zweihander.common.mob_effect.OiledMobEffect;
import xyz.nikgub.zweihander.common.registries.*;
import xyz.nikgub.zweihander.data.RegistriesDataGeneration;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(Zweihander.MOD_ID)
public class Zweihander
{
    public static final String MOD_ID = "zweihander";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Zweihander()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SoundEventRegistry.SOUNDS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        EnchantmentRegistry.ENCHANTMENTS.register(modEventBus);
        MobEffectRegistry.MOB_EFFECTS.register(modEventBus);
        VillagerProfessionRegistry.POIS.register(modEventBus);
        VillagerProfessionRegistry.PROFESSIONS.register(modEventBus);
        ContractRegistry.CONTRACTS.register(modEventBus);
        EntityTypeRegistry.ENTITIES.register(modEventBus);

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerLayerDefinitions);
        modEventBus.addListener(this::creativeTabEvent);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ZweihanderConfig.COMMON);
    }

    private void commonSetup (final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            InfusionItem.makeRecipes();
        });

    }

    private void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        /**/
        RegistriesDataGeneration.addProviders(event.includeServer(), generator, output, lookupProvider, existingFileHelper);
        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Resources for Zweihander"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion)))));
    }

    private void creativeTabEvent(final BuildCreativeModeTabContentsEvent event)
    {
        for (Item item : ItemRegistry.ITEMS.getEntries().stream().map(RegistryObject::get).toList())
        {
            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                if (item instanceof InfusionItem) event.accept(item);
                if (item instanceof ZweihanderItem) event.accept(item);
                if (item instanceof MusketItem) event.accept(item);
            }
            else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            {
                if (item instanceof InfusionItem) event.accept(item);
            }
            else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            {
                event.accept(item);
            }
        }
    }

    private void clientSetup(final FMLCommonSetupEvent event) {
        EntityRenderers.register(EntityTypeRegistry.FLAMING_GUILLOTINE.get(), FlamingGuillotineRenderer::new);
    }

    private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FlamingGuillotineModel.LAYER_LOCATION, FlamingGuillotineModel::createBodyLayer);
    }

    @SubscribeEvent
    public void villagerTrades (VillagerTradesEvent event)
    {
        if (event.getType() == VillagerProfessionRegistry.DEMONOLOGIST.get())
        {
            event.getTrades().get(1).add(((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(ItemRegistry.ACCURSED_CONTRACT.get()),
                    5, 0, 0
            )));
            event.getTrades().get(1).add(((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(ItemRegistry.UNBOUND_BLOOD.get(), 1),
                    new ItemStack(ItemRegistry.BLESSED_SILVER_INGOT.get()),
                    16, 8, 0.02f
            )));
            event.getTrades().get(2).add(((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(ItemRegistry.UNBOUND_BLOOD.get(), 4),
                    new ItemStack(ItemRegistry.ACCURSED_CONTRACT.get()),
                    5, 16, 0.02f
            )));
            event.getTrades().get(2).add(((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(ItemRegistry.UNBOUND_BLOOD.get(), 3),
                    new ItemStack(Items.EXPERIENCE_BOTTLE, 2),
                    12, 16, 0.025f
            )));
            event.getTrades().get(3).add(((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(ItemRegistry.UNBOUND_BLOOD.get(), 16),
                    new ItemStack(ItemRegistry.MUSKET.get()),
                    16, 32, 0.02f
            )));
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void attackEntityEvent(final AttackEntityEvent event)
    {
        Player player = event.getEntity();
        if (!(player.getMainHandItem().getItem() instanceof ZweihanderItem)) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;
        player.getMainHandItem().getOrCreateTag().putBoolean("ProperSwing", (player.getAttackStrengthScale(0) == 1));
        if (player.getAttackStrengthScale(0) != 1) event.setCanceled(true);
    }

    @SubscribeEvent
    //@SuppressWarnings("unused")
    public void livingHurtEvent (final LivingHurtEvent event)
    {
        LivingEntity target = event.getEntity();
        DamageSource damageSource = event.getSource();
        OiledMobEffect.tryIgnition(event);
        if (!(damageSource.getDirectEntity() instanceof LivingEntity source)) return;
        ItemStack mainHandItem = source.getMainHandItem();
        EnchantmentRegistry.Utils.tryRepulsion(target, source, mainHandItem);
        if (Zweihander.Utils.isDirectDamage(damageSource))
        {
            if (mainHandItem.getMaxStackSize() == 1) {
                InfusionMobEffect.tryEffect(event);
            }
            if (mainHandItem.getEnchantmentLevel(EnchantmentRegistry.CURSE_OF_CHAOS.get()) != 0) {
                event.setAmount(event.getAmount() * EnchantmentRegistry.Utils.tryCurseOfChaos(target));
            }

        }
    }

    @SubscribeEvent
    public void onKillEffects (LivingDeathEvent event)
    {
        DamageSource damageSource = event.getSource();
        ItemStack hand;
        if (damageSource.getEntity() instanceof LivingEntity entity && (hand = entity.getMainHandItem()).getItem() instanceof MusketItem && !MusketItem.isLoaded(hand)
                && hand.getEnchantmentLevel(EnchantmentRegistry.TROOPER.get()) != 0)
        {
            MusketItem.reload(entity, hand);
        }
    }

    public static class Utils
    {
        public static void shortenEffect (final LivingEntity entity, final MobEffect effect, final int tick)
        {
            MobEffectInstance instance = entity.getEffect(effect);
            assert instance != null;
            MobEffectInstance newInstance = new MobEffectInstance(instance.getEffect(), Mth.clamp(instance.getDuration() - tick, 0, instance.getDuration()), instance.getAmplifier(), instance.isAmbient(), instance.isVisible(), instance.showIcon());
            entity.removeEffect(effect);
            entity.addEffect(newInstance);
        }

        public static void coverInParticles (final LivingEntity entity, final SimpleParticleType particleType, final double particleSpeed)
        {
            if (!(entity.level() instanceof ServerLevel level)) return;
            float height = entity.getBbHeight();
            float width = entity.getBbWidth();
            level.sendParticles(particleType, entity.getX(), entity.getY() + height / 2, entity.getZ(), (int) (10 * width * height * width), width / 2, height / 2, width / 2, particleSpeed);
        }

        public static boolean isDirectDamage (final DamageSource damageSource)
        {
            return !damageSource.is(DamageTypeTags.IS_EXPLOSION) && !damageSource.is(DamageTypeTags.IS_PROJECTILE);
        }

        public static void playSound (Level level, double x, double y, double z, SoundEvent soundEvent, SoundSource source, float volume, float pitch)
        {
            if (!level.isClientSide()) level.playSound(null, BlockPos.containing(x, y, z), soundEvent, source, volume, pitch);
            else level.playLocalSound(x, y, z, soundEvent, source, volume, pitch, false);
        }

        public static DamageSource makeDamageSource(ResourceKey<DamageType> damageType, @NotNull Level level, @Nullable Entity trueSource, @Nullable Entity proxy)
        {
            Optional<Registry<DamageType>> registry = level.registryAccess().registry(Registries.DAMAGE_TYPE);
            if (registry.isPresent())
                try {
                    return new DamageSource(registry.get().getHolderOrThrow(damageType), proxy, trueSource);
                }
                catch (IllegalStateException stateException)
                {
                    return new DamageSource(registry.get().getHolderOrThrow(DamageTypes.GENERIC), proxy, trueSource);
                }
            else
                throw new RuntimeException("Unable to locate damage type registry. How?");
        }

        //public static List<Vec3> launchRay (final Vec3 pos, final Vec3 rotations, int iterations, double step)
        //{
        //    List<Vec3> ret = new ArrayList<>();
        //    for (int i = 0; i < iterations; i++)
        //    {
        //        ret.add(new Vec3(pos.x + rotations.x * i * step, pos.y + rotations.y * i * step, pos.z + rotations.z * i * step));
        //    }
        //    return ret;
        //}
//
        //public static Vec3 traceUntil(LivingEntity livingEntity, BiConsumer<Vec3, Level> action, double limit){
        //    Vec3 lookPos;
        //    Vec3 initLook = livingEntity.getLookAngle();
        //    double i = 1.2;
        //    while(EntityUtils.entityCollector(
        //            lookPos = new Vec3( livingEntity.getX() + initLook.x * i, livingEntity.getY() + 1.5 + initLook.y * i, livingEntity.getZ() + initLook.z * i)
        //            , 0.1, livingEntity.level()).isEmpty() &&
        //            !livingEntity.level().getBlockState(new BlockPos(new Vec3i((int) lookPos.x, (int) lookPos.y, (int) lookPos.z))).canOcclude()
        //            && i < limit){
        //        action.accept(lookPos, livingEntity.level());
        //        i += 0.2;
        //    }
        //    return lookPos;
        //}
    }

}
