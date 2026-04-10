package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

public abstract class SpellCastingCultistServant extends CultistServant {
    private static final EntityDataAccessor<Byte> SPELL;
    protected int spellTicks;
    private SpellType activeSpell;

    protected SpellCastingCultistServant(EntityType<? extends SpellCastingCultistServant> type, Level p_i48551_2_) {
        super(type, p_i48551_2_);
        this.activeSpell = SpellCastingCultistServant.SpellType.NONE;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL, (byte) 0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.spellTicks = compound.getInt("SpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellTicks", this.spellTicks);
    }

    public boolean isSpellcasting() {
        if (this.level().isClientSide) {
            return (Byte) this.entityData.get(SPELL) > 0;
        } else {
            return this.spellTicks > 0;
        }
    }

    public void setSpellType(SpellType spellType) {
        this.activeSpell = spellType;
        this.entityData.set(SPELL, (byte) spellType.id);
    }

    protected SpellType getSpellType() {
        return !this.level().isClientSide ? this.activeSpell
                : SpellCastingCultistServant.SpellType.getFromId((Byte) this.entityData.get(SPELL));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellTicks > 0) {
            --this.spellTicks;
        }

    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isSpellcasting() && this.isAlive()) {
            SpellType SpellCastingCultistServantEntity$spelltype = this.getSpellType();
            double d0 = SpellCastingCultistServantEntity$spelltype.particleSpeed[0];
            double d1 = SpellCastingCultistServantEntity$spelltype.particleSpeed[1];
            double d2 = SpellCastingCultistServantEntity$spelltype.particleSpeed[2];
            if (this instanceof ApostleServant) {
                float f = this.yBodyRot * 0.017453292F + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
                float f1 = Mth.cos(f);
                float f2 = Mth.sin(f);
                if (this.getMainArm() == HumanoidArm.RIGHT) {
                    this.level().addParticle((ParticleOptions) ModParticleTypes.CULT_SPELL.get(),
                            this.getX() + (double) f1 * 0.6, this.getY() + 1.8, this.getZ() + (double) f2 * 0.6, d0, d1,
                            d2);
                } else {
                    this.level().addParticle((ParticleOptions) ModParticleTypes.CULT_SPELL.get(),
                            this.getX() - (double) f1 * 0.6, this.getY() + 1.8, this.getZ() - (double) f2 * 0.6, d0, d1,
                            d2);
                }
            } else {
                for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
                    double d = this.level().random.nextGaussian() * 0.2;
                    this.level().addParticle((ParticleOptions) ModParticleTypes.CULT_SPELL.get(), this.getX(),
                            this.getEyeY(), this.getZ(), d0, d1, d2);
                }
            }
        }

    }

    protected int getSpellTicks() {
        return this.spellTicks;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    static {
        SPELL = SynchedEntityData.defineId(SpellCastingCultistServant.class, EntityDataSerializers.BYTE);
    }

    public static enum SpellType {
        NONE(0, 0.0, 0.0, 0.0),
        FIRE(1, 1.0, 0.6, 0.0),
        ZOMBIE(2, 0.1, 0.1, 0.8),
        ROAR(3, 0.8, 0.3, 0.8),
        TORNADO(4, 1.0, 0.1, 0.1),
        RANGED(5, 0.5, 0.5, 0.5),
        CLOUD(6, 0.3, 0.0, 0.0),
        SACRIFICE(7, 0.1, 0.1, 0.1);

        private final int id;
        private final double[] particleSpeed;

        private SpellType(int idIn, double xParticleSpeed, double yParticleSpeed, double zParticleSpeed) {
            this.id = idIn;
            this.particleSpeed = new double[] { xParticleSpeed, yParticleSpeed, zParticleSpeed };
        }

        public static SpellType getFromId(int idIn) {
            SpellType[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                SpellType SpellCastingCultistServantEntity$spelltype = var1[var3];
                if (idIn == SpellCastingCultistServantEntity$spelltype.id) {
                    return SpellCastingCultistServantEntity$spelltype;
                }
            }

            return NONE;
        }
    }

    public abstract class UseSpellGoal extends Goal {
        protected int spellWarmup;
        protected int spellCooldown;

        protected UseSpellGoal() {
        }

        public boolean canUse() {
            LivingEntity livingentity = SpellCastingCultistServant.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (SpellCastingCultistServant.this.isSpellcasting()) {
                    return false;
                } else {
                    return SpellCastingCultistServant.this.tickCount >= this.spellCooldown;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SpellCastingCultistServant.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.spellWarmup > 0;
        }

        public void start() {
            this.spellWarmup = this.getCastWarmupTime();
            SpellCastingCultistServant.this.spellTicks = this.getCastingTime();
            this.spellCooldown = SpellCastingCultistServant.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                SpellCastingCultistServant.this.playSound(soundevent, this.castingVolume(), 1.0F);
            }

            SpellCastingCultistServant.this.setSpellType(this.getSpellType());
        }

        public void tick() {
            --this.spellWarmup;
            if (this.spellWarmup == 0) {
                this.castSpell();
                SpellCastingCultistServant.this.setSpellType(SpellCastingCultistServant.SpellType.NONE);
                SpellCastingCultistServant.this.playSound(SpellCastingCultistServant.this.getCastingSoundEvent(), 1.0F,
                        1.0F);
            }

        }

        protected float castingVolume() {
            return 1.0F;
        }

        protected abstract void castSpell();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract SpellType getSpellType();
    }

    public class CastingASpellGoal extends Goal {
        public CastingASpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return SpellCastingCultistServant.this.getSpellTicks() > 0;
        }

        public void start() {
            super.start();
            SpellCastingCultistServant.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            SpellCastingCultistServant.this.setSpellType(SpellCastingCultistServant.SpellType.NONE);
        }

        public void tick() {
            if (SpellCastingCultistServant.this.getTarget() != null) {
                SpellCastingCultistServant.this.getLookControl().setLookAt(SpellCastingCultistServant.this.getTarget(),
                        (float) SpellCastingCultistServant.this.getMaxHeadYRot(),
                        (float) SpellCastingCultistServant.this.getMaxHeadXRot());
            }

        }
    }
}
