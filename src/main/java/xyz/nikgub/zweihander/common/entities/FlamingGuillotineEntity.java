package xyz.nikgub.zweihander.common.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import xyz.nikgub.incandescent.util.EntityUtils;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.data.DamageTypeDatagen;

public class FlamingGuillotineEntity extends AttackEffectEntity {
    public AnimationState fallAnimationState = new AnimationState();
    public float damage = 5;

    public FlamingGuillotineEntity(EntityType<? extends FlamingGuillotineEntity> entityType, Level level) {
        super(entityType, level);
        this.lifetime = 10;
        this.fallAnimationState.start(0);
    }

    public static FlamingGuillotineEntity createWithDamage (EntityType<? extends FlamingGuillotineEntity> entityType, Level level, float damage)
    {
        FlamingGuillotineEntity entity = new FlamingGuillotineEntity(entityType, level);
        entity.damage = damage;
        return entity;
    }

    @Override
    public void tick()
    {
        if(this.tickCount > lifetime){
            this.remove(RemovalReason.DISCARDED);
            if(this.level() instanceof ServerLevel serverLevel)
            {
                serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 30, 0.5, 2, 0.5, 0.3);
                Entity owner = (this.getPlayerUuid() != null) ? this.level().getPlayerByUUID(this.getPlayerUuid()) : this;
                for(LivingEntity entity : EntityUtils.entityCollector(this.position(), 3 * Mth.sqrt(this.getSize()), this.level()))
                {
                    entity.hurt(Zweihander.Utils.makeDamageSource(DamageTypeDatagen.MUSKET_SHOT, serverLevel, owner, this),
                            damage * this.getSize());
                }
            }
        }
    }
}
