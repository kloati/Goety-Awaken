package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.ai.ModRangedAttackGoal;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.projectiles.CorruptedSoulBolt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;

import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class CorruptedSlime extends SlimeServant implements RangedAttackMob {

    public CorruptedSlime(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, SpawnGroupData pSpawnData,
            CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        int size = 2 + this.random.nextInt(3);
        this.setSize(size, true);
        return pSpawnData;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ModRangedAttackGoal<>(this, 1.0D, 20, 60, 15.0F) {
            @Override
            public boolean canUse() {
                LivingEntity livingentity = CorruptedSlime.this.getTarget();
                if (livingentity != null && livingentity.isAlive()) {
                    double distance = livingentity.distanceToSqr(CorruptedSlime.this);
                    if (distance > 16.0D) {
                        this.target = livingentity;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void tick() {
                super.tick();
                if (this.target != null && this.target.isAlive()) {
                    double d0 = this.target.getX() - CorruptedSlime.this.getX();
                    double d2 = this.target.getZ() - CorruptedSlime.this.getZ();
                    float f = (float) (Mth.atan2(d2, d0) * (180F / Math.PI)) - 90.0F;
                    CorruptedSlime.this.yBodyRot = f;
                    CorruptedSlime.this.yHeadRot = f;
                }
            }
        });
        this.slimeGoal();
    }

    @Override
    public void setSize(int size, boolean pResetHealth) {
        int clampedSize = Math.max(size, 2);
        super.setSize(clampedSize, pResetHealth);
    }

    @Override
    public boolean isTiny() {
        return this.getSize() <= 2;
    }

    @Override
    public boolean isMedium() {
        return this.getSize() == 3;
    }

    @Override
    public boolean isLarge() {
        return this.getSize() >= 4;
    }

    @Override
    public void remove(RemovalReason p_149847_) {
        int i = this.getSize();
        if (!this.level().isClientSide && i > 2 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) i / 4.0F;
            int j = Math.max(i / 2, 2);
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                CorruptedSlime slime = ModEntityType.CORRUPTED_SLIME.get().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }

                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(j, true);
                    if (this.getTrueOwner() != null) {
                        slime.setTrueOwner(this.getTrueOwner());
                    }
                    if (this.limitedLifeTicks > 0) {
                        slime.setLimitedLife(this.limitedLifeTicks);
                    }
                    slime.setHostile(this.isHostile());
                    slime.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2,
                            this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(slime);
                }
            }
        }
        this.setRemoved(p_149847_);
        this.invalidateCaps();
        this.brain.clearMemories();
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        double d0 = pTarget.getX() - this.getX();
        double d2 = pTarget.getZ() - this.getZ();
        float f = (float) (Mth.atan2(d2, d0) * (180F / Math.PI)) - 90.0F;
        this.setYRot(f);
        this.yBodyRot = f;
        this.yHeadRot = f;

        double range = pTarget.distanceTo(this);
        double d1 = pTarget.getY(0.5D) - this.getY(0.5D);

        float distance = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        float xRot = -(float) (Mth.atan2(d1, (double) distance) * (180F / Math.PI));

        double forwardOffset = this.getBbWidth() * 0.8F;
        double offsetX = -Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * forwardOffset;
        double offsetZ = Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * forwardOffset;
        double offsetY = this.getEyeY() - this.getY();
        CorruptedSoulBolt corruptedSoulBolt = new CorruptedSoulBolt(this,
                d0, d1, d2, this.level());

        corruptedSoulBolt.moveTo(
                this.getX() + offsetX,
                this.getY() + offsetY,
                this.getZ() + offsetZ,
                this.getYRot(),
                xRot);

        corruptedSoulBolt.shoot(d0, d1, d2, pVelocity, 1.0F);
        this.level().addFreshEntity(corruptedSoulBolt);
    }

}