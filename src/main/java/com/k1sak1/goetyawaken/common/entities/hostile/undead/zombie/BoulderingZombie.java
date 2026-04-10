package com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie;

import com.Polarice3.Goety.common.entities.ai.path.ModClimberNavigation;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class BoulderingZombie extends Zombie {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(BoulderingZombie.class,
            EntityDataSerializers.BYTE);

    public BoulderingZombie(EntityType<? extends Zombie> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new net.minecraft.world.entity.ai.control.MoveControl(this);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new ModClimberNavigation(this, level);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.climb();
        }
    }

    public void climb() {
        if (this.horizontalCollision && this.getTarget() != null) {
            double horizontalDistance = Math.sqrt(
                    Math.pow(this.getX() - this.getTarget().getX(), 2) +
                            Math.pow(this.getZ() - this.getTarget().getZ(), 2));

            if (horizontalDistance <= 5.0D && this.getTarget().getY() > this.getY()) {
                BlockPos climbPos = this.getClimbPos();
                if (climbPos != null) {
                    this.getMoveControl().setWantedPosition(
                            climbPos.getX() + 0.5D,
                            climbPos.getY(),
                            climbPos.getZ() + 0.5D,
                            1.0D);
                    this.setClimbing(true);
                    if (this.tickCount % 20 == 0 && this.random.nextInt(3) == 0) {
                        this.playClimbSound();
                    }
                }
            }
        } else {
            this.setClimbing(false);
        }
    }

    private int getClimbHeight() {
        int height = 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int y = 1; y <= 8; y++) {
            mutablePos.set(this.blockPosition().getX(), this.blockPosition().getY() + y, this.blockPosition().getZ());
            BlockState state = this.level().getBlockState(mutablePos);
            if (!state.blocksMotion() && !this.isStuckAtCeiling()) {
                height = y;
            } else {
                break;
            }
        }
        return height;
    }

    private boolean isClimbable(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        return state.blocksMotion() && !state.is(Blocks.AIR);
    }

    private BlockPos getClimbPos() {
        BlockPos targetPos = this.getClimbablePos();
        if (targetPos != null) {
            int climbHeight = this.getClimbHeight();
            if (climbHeight > 0) {
                return targetPos.above(climbHeight);
            }
        }
        return null;
    }

    private BlockPos getClimbablePos() {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        BlockPos entityPos = this.blockPosition();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    mutablePos.set(entityPos.getX() + x, entityPos.getY(), entityPos.getZ() + z);
                    if (this.isClimbable(mutablePos)) {
                        return mutablePos.immutable();
                    }
                }
            }
        }
        return null;
    }

    private boolean isStuckAtCeiling() {
        BlockPos above = this.blockPosition().above(2);
        return this.level().getBlockState(above).blocksMotion() && this.getDeltaMovement().y() <= 0.01D;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getRandomIdleSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.getRandomHurtSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOULDERING_ZOMBIE_DEATH.get();
    }

    private SoundEvent getRandomIdleSound() {
        RandomSource random = this.getRandom();
        int soundIndex = random.nextInt(5) + 1;
        switch (soundIndex) {
            case 1:
                return ModSounds.BOULDERING_ZOMBIE_IDLE_1.get();
            case 2:
                return ModSounds.BOULDERING_ZOMBIE_IDLE_2.get();
            case 3:
                return ModSounds.BOULDERING_ZOMBIE_IDLE_3.get();
            case 4:
                return ModSounds.BOULDERING_ZOMBIE_IDLE_4.get();
            case 5:
                return ModSounds.BOULDERING_ZOMBIE_IDLE_5.get();
            default:
                return ModSounds.BOULDERING_ZOMBIE_IDLE_1.get();
        }
    }

    private SoundEvent getRandomHurtSound() {
        RandomSource random = this.getRandom();
        int soundIndex = random.nextInt(4) + 1;
        switch (soundIndex) {
            case 1:
                return ModSounds.BOULDERING_ZOMBIE_HURT_1.get();
            case 2:
                return ModSounds.BOULDERING_ZOMBIE_HURT_2.get();
            case 3:
                return ModSounds.BOULDERING_ZOMBIE_HURT_3.get();
            case 4:
                return ModSounds.BOULDERING_ZOMBIE_HURT_4.get();
            default:
                return ModSounds.BOULDERING_ZOMBIE_HURT_1.get();
        }
    }

    public void playClimbSound() {
        if (!this.level().isClientSide) {
            RandomSource random = this.getRandom();
            int soundIndex = random.nextInt(4) + 1;
            SoundEvent climbSound;
            switch (soundIndex) {
                case 1:
                    climbSound = ModSounds.BOULDERING_ZOMBIE_CLIMB_1.get();
                    break;
                case 2:
                    climbSound = ModSounds.BOULDERING_ZOMBIE_CLIMB_2.get();
                    break;
                case 3:
                    climbSound = ModSounds.BOULDERING_ZOMBIE_CLIMB_3.get();
                    break;
                case 4:
                    climbSound = ModSounds.BOULDERING_ZOMBIE_CLIMB_4.get();
                    break;
                default:
                    climbSound = ModSounds.BOULDERING_ZOMBIE_CLIMB_1.get();
                    break;
            }
            this.playSound(climbSound, 1.0F, 1.0F);
        }
    }
}