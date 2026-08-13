package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.random_something.masquerader_mod.sounds.SoundRegister;
import java.util.List;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;

//Based on https://github.com/TheDarkPeasant/The-Masquerade, Original by TheDarkPeasant
public class MasqueraderServantClone extends MasqueraderServant {
    private int lifespan = 300;
    private MasqueraderServant owner;

    public MasqueraderServantClone(EntityType<? extends AbstractIllagerServant> p_32105_, Level p_32106_) {
        super(p_32105_, p_32106_);
    }

    protected MasqueraderServantClone(EntityType<? extends AbstractIllagerServant> p_32105_, Level p_32106_,
            MasqueraderServant owner, int lifespan) {
        super(p_32105_, p_32106_);
        this.lifespan = lifespan;
        this.owner = owner;
        this.setTrueOwner(owner);
        this.goalSelector.addGoal(5, bowGoal);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 5.0D);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegister.CLONE_DEATH.get();
    }

    @Override
    public void die(DamageSource cause) {
        if (owner != null)
            owner.clones.remove(this);

        if (owner != null && (lifespan < 1 || owner.damageTaken > 5)) {
            this.resetAttackerTarget();
        } else if (owner != null && !owner.clones.isEmpty() && owner.damageTaken <= 5) {
            this.distractAttackers(owner.clones.get(this.random.nextInt(0, owner.clones.size())));
        }

        makePoofParticles(this, this.level());

        this.discard();
    }

    public void resetAttackerTarget() {
        List<Mob> list = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(100.0));

        for (Mob attacker : list) {
            if (attacker.getLastHurtByMob() == this) {
                attacker.setLastHurtByMob(owner);
            }

            if (attacker.getTarget() == this) {
                attacker.setTarget(owner);
            }
        }

    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("lifespan", lifespan);
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        lifespan = tag.getInt("lifespan");
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void tick() {
        if (this.getMask() != ILLUSIONER_MASK)
            this.setMask(ILLUSIONER_MASK);
        if (owner != null && this.getTarget() != owner.getTarget())
            this.setTarget(owner.getTarget());
        --lifespan;
        if (lifespan < 1) {
            this.kill();
        }
        super.tick();
    }

    public void makePoofParticles(Entity entity, Level level) {
        if (!level.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                ((ServerLevel) level).sendParticles(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(),
                        entity.getRandomZ(1.0D), 1, d0, d1, d2, 0);
            }
        }
    }
}