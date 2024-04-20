package xyz.nikgub.zweihander.mob_effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.registries.MobEffectRegistry;

public class OiledMobEffect extends MobEffect {
    public OiledMobEffect() {
        super(MobEffectCategory.HARMFUL, -16777152);
    }

    public static void tryIgnition (LivingHurtEvent event)
    {
        LivingEntity target = event.getEntity();
        DamageSource damageSource = event.getSource();
        MobEffectInstance oiledInstance = target.getEffect(MobEffectRegistry.OILED.get());
        if (oiledInstance != null && (target.isOnFire() || damageSource.is(DamageTypeTags.IS_FIRE)) && !damageSource.is(DamageTypes.ON_FIRE) && !damageSource.is(DamageTypes.IN_FIRE)) {
            event.setAmount(event.getAmount() * (1 + target.getRemainingFireTicks() / 200f));
            target.setSecondsOnFire(target.getRemainingFireTicks() / 10 + 5 * (oiledInstance.getAmplifier() + 1));
            target.removeEffect(MobEffectRegistry.OILED.get());
            target.playSound(SoundEvents.BLAZE_SHOOT);
            Zweihander.Utils.coverInParticles(target, ParticleTypes.FLAME, 0.25D);
        }
    }
}
