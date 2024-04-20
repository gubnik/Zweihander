package xyz.nikgub.zweihander.registries;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.mob_effect.InfusionMobEffect;
import xyz.nikgub.zweihander.mob_effect.OiledMobEffect;

public class MobEffectRegistry {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Zweihander.MOD_ID);

    public static final RegistryObject<OiledMobEffect> OILED = MOB_EFFECTS.register("oiled",
            OiledMobEffect::new);

    public static final RegistryObject<InfusionMobEffect> FIERY_INFUSION = MOB_EFFECTS.register("fiery_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(1f, 0.3f, 0f, 1f),
                    ((event) -> {
                        event.getEntity().setSecondsOnFire(3);
                    })));

    public static final RegistryObject<InfusionMobEffect> ICE_INFUSION = MOB_EFFECTS.register("ice_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(0f, 0.4f, 1f, 1f),
                    ((event) -> {
                        LivingEntity living = event.getEntity();
                        if (living.canFreeze())
                        {
                            living.setTicksFrozen(
                                    living.getTicksFrozen() + 300
                            );
                        }
                    })));

    public static final RegistryObject<InfusionMobEffect> SILVER_INFUSION = MOB_EFFECTS.register("silver_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(0.7f, 0.7f, 1f, 1f),
                    ((event) -> {
                        LivingEntity living = event.getEntity();
                        if (living.getMobType() == MobType.UNDEAD)
                        {
                            event.setAmount(event.getAmount() * 1.2f);
                            Zweihander.Utils.coverInParticles(living, ParticleTypes.CRIT, 0.2D);
                        }
                    })));

    public static final RegistryObject<InfusionMobEffect> CREEPER_INFUSION = MOB_EFFECTS.register("creeper_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(0.25f, 1f, 0.0f, 1f),
                    (MobEffectRegistry::creeperInfusion)));

    public static final RegistryObject<InfusionMobEffect> MIDAS_INFUSION = MOB_EFFECTS.register("midas_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(1f, 1f, 0f, 1f),
                    (MobEffectRegistry::midasInfusion)));

    public static final RegistryObject<InfusionMobEffect> OIL_INFUSION = MOB_EFFECTS.register("oil_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(0f, 0.05f, 0.2f, 1.2f),
                    (MobEffectRegistry::oilInfusion)));

    private static void creeperInfusion (LivingHurtEvent event)
    {
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity sourceEntity)) return;
        LivingEntity targetEntity = event.getEntity();
        Zweihander.Utils.shortenEffect(sourceEntity, CREEPER_INFUSION.get(), 200);
        targetEntity.level().explode(event.getSource().getEntity(), targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() / 2, targetEntity.getZ(), 0.1f * event.getAmount(), Level.ExplosionInteraction.NONE);
    }

    private static void midasInfusion (LivingHurtEvent event)
    {
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity sourceEntity)) return;
        LivingEntity entity = event.getEntity();
        Zweihander.Utils.shortenEffect(sourceEntity, MIDAS_INFUSION.get(), 200);
        sourceEntity.level().addFreshEntity(
                new ExperienceOrb(sourceEntity.level(), entity.getX(), entity.getY(), entity.getZ(), (int) event.getAmount() * 2)
        );
    }

    private static void oilInfusion(LivingHurtEvent event)
    {
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity)) return;
        LivingEntity entity = event.getEntity();
        entity.removeEffect(OILED.get());
        entity.addEffect(new MobEffectInstance(OILED.get(), 100, 0, false, true));
    }
}
