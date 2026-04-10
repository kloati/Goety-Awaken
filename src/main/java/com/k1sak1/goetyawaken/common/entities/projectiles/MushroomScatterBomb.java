package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.projectiles.ScatterBomb;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.Polarice3.Goety.utils.SpellExplosion;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.BlockFinder;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MushroomScatterBomb extends ScatterBomb {

    public MushroomScatterBomb(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public MushroomScatterBomb(double x, double y, double z, Level level) {
        this(entityType(), level);
        this.setPos(x, y, z);
    }

    public static EntityType<MushroomScatterBomb> entityType() {
        return (EntityType<MushroomScatterBomb>) (EntityType<?>) com.Polarice3.Goety.common.entities.ModEntityType.SCATTER_BOMB
                .get();
    }

    public MushroomScatterBomb(LivingEntity owner, Level level) {
        this(owner.getX(), owner.getEyeY() - (double) 0.1F, owner.getZ(), level);
        this.setOwner(owner);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide()) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F,
                    (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);

            float damage = 12.5F;
            if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F;
            }
            new SpellExplosion(this.level(), this, this.damageSources().explosion(this, this.getOwner()), this.getX(),
                    this.getY(), this.getZ(), 4.5F, damage) {
                @Override
                public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z,
                        double seen, float actualDamage) {
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    target.invulnerableTime = 15;
                }
            };

            if (this.level() instanceof ServerLevel serverLevel) {
                AcidPool acidPool = new AcidPool(com.Polarice3.Goety.common.entities.ModEntityType.ACID_POOL.get(),
                        this.level());
                acidPool.setPos(this.getX(), this.getY(), this.getZ());
                if (this.getOwner() != null && this.getOwner() instanceof LivingEntity) {
                    acidPool.setOwner((LivingEntity) this.getOwner());
                }
                acidPool.setRadius(3.0F);
                acidPool.setDamage(3.0F);
                acidPool.setColor(2143038);
                acidPool.setDuration(160);

                serverLevel.addFreshEntity(acidPool);

                BlockPos centerPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
                int radius = 5;

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            BlockPos pos = centerPos.offset(dx, dy, dz);

                            // if (this.getOwner() instanceof MushroomMonstrosityHostile) {
                            if (this.level().random.nextDouble() < 0.04
                                    && Config.ALLOW_MUSHROOM_MONSTROSITY_PLANT_POISONOUS_MUSHROOM.get()) {
                                BlockPos mushroomPos = pos.above();
                                boolean isAboveBlockNonSolid = !this.level().getBlockState(mushroomPos)
                                        .isSolidRender(this.level(), mushroomPos);

                                if (isAboveBlockNonSolid
                                        && canMushroomSurviveIgnoringFluid(this.level(), mushroomPos)) {
                                    this.level().setBlock(mushroomPos,
                                            ModBlocks.POISONOUS_MUSHROOM.get().defaultBlockState(), 3);
                                    BlockEntity blockEntity = this.level().getBlockEntity(mushroomPos);
                                    if (blockEntity instanceof com.k1sak1.goetyawaken.common.blocks.entity.PoisonousMushroomBlockEntity poisonousMushroomBE) {
                                        if (this.getOwner() != null
                                                && this.getOwner() instanceof LivingEntity) {
                                            poisonousMushroomBE.setOwner((LivingEntity) this.getOwner());
                                        }
                                    }
                                }
                            }
                            // }
                        }
                    }
                }
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                ServerParticleUtil.addAuraParticles(serverLevel, ModParticleTypes.BIG_FIRE_GROUND.get(), this, 2.0F);
                ColorUtil colorUtil = new ColorUtil(0xffe183);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4.5F, 1),
                        this.getX(), BlockFinder.moveDownToGround(this), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel
                        .sendParticles(
                                new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue,
                                        4.5F, 1),
                                this.getX(), BlockFinder.moveDownToGround(this) + 0.5F, this.getZ(), 1, 0, 0, 0, 0);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(
                        new Vector3f(Vec3.fromRGB24(0x814342).toVector3f()), 1.0F);
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, this.getX(),
                        this.getY() + 0.25D, this.getZ(), 0, 0.14D, 0, 1.0F);
            }

            this.discard();
        }
    }

    private boolean canMushroomSurviveIgnoringFluid(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        net.minecraft.world.level.block.state.BlockState belowState = level.getBlockState(belowPos);
        net.minecraft.world.level.block.state.BlockState currentState = level.getBlockState(pos);
        boolean isCurrentPositionValid = currentState.isAir() || currentState.getFluidState().isSource();
        boolean canSupportMushroom = belowState.isFaceSturdy(level, belowPos, net.minecraft.core.Direction.UP) ||
                belowState.is(net.minecraft.tags.BlockTags.DIRT) ||
                belowState.is(net.minecraft.tags.BlockTags.MUSHROOM_GROW_BLOCK);
        return isCurrentPositionValid && canSupportMushroom;
    }
}