package xyz.nikgub.zweihander.registries;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.mob_effect.InfusionMobEffect;

public class MobEffectRegistry {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Zweihander.MOD_ID);

    public static final RegistryObject<InfusionMobEffect> FIRE_INFUSION = MOB_EFFECTS.register("fire_infusion",
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
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(0.4f, 0.4f, 2f, 1f),
                    ((event) -> {
                        LivingEntity living = event.getEntity();
                        if (living.getMobType() == MobType.UNDEAD)
                        {
                            event.setAmount(event.getAmount() * 1.2f);
                        }
                    })));

    public static final RegistryObject<InfusionMobEffect> CREEPER_INFUSION = MOB_EFFECTS.register("creeper_infusion",
            () -> new InfusionMobEffect(MobEffectCategory.BENEFICIAL, 0, new InfusionMobEffect.Colors(0.2f, 1f, 0.0f, 1f),
                    (event -> {
                        if (event.getAmount() > 6)
                        {
                            LivingEntity living = event.getEntity();
                            living.level().explode(event.getSource().getEntity(), living.getX(), living.getY() + living.getBbHeight() / 2, living.getZ(), event.getAmount()/5f, Level.ExplosionInteraction.NONE);
                        }
                    })));
}
