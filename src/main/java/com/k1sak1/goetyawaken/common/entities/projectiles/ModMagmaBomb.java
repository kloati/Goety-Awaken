package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.projectiles.MagmaBomb;
import com.Polarice3.Goety.common.entities.projectiles.Pyroclast;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ModMagmaBomb extends MagmaBomb {

    public ModMagmaBomb(EntityType<? extends MagmaBomb> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public ModMagmaBomb(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public ModMagmaBomb(LivingEntity pOwner, Level p_37464_) {
        super(pOwner, p_37464_);
    }

    @Override
    public void explode(HitResult pResult) {
        if (!this.level().isClientSide) {
            Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
            if (pResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                if (BlockFinder.canBeReplaced(this.level(), blockpos)) {
                    vec3 = Vec3.atCenterOf(blockpos);
                }
            } else if (pResult instanceof EntityHitResult entityHitResult) {
                Entity entity1 = entityHitResult.getEntity();
                vec3 = Vec3.atCenterOf(entity1.blockPosition());
            }
            MobUtil.explosionDamage(this.level(), this.getOwner() != null ? this.getOwner() : this,
                    this.damageSources().explosion(this, this.getOwner()), vec3.x, vec3.y, vec3.z, this.explosionPower,
                    0);
            if (this.level() instanceof ServerLevel serverLevel) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue,
                                this.explosionPower + 1.0F, 1),
                        vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_FIRE_GROUND.get(), vec3.x,
                        this.getY() + 0.25D, vec3.z, 0, 0, 0, this.explosionPower);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(
                        new Vector3f(Vec3.fromRGB24(0x7a6664).toVector3f()), 1.0F);
                DustCloudParticleOption cloudParticleOptions2 = new DustCloudParticleOption(
                        new Vector3f(Vec3.fromRGB24(0xeca294).toVector3f()), 1.0F);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D,
                            vec3.z, 0, 0.14D, 0, this.explosionPower / 2.0F);
                }
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2, vec3.x, this.getY() + 0.25D,
                        vec3.z, 0, 0.14D, 0, this.explosionPower / 2.0F);
            }
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
            int i = 3;
            for (int i1 = 0; i1 < 4; i1++) {
                Pyroclast pyroclast = new Pyroclast(this.getX(), this.getY() + 1.5F, this.getZ(), this.level());
                if (this.getOwner() != null) {
                    pyroclast.setOwner(this.getOwner());
                } else {
                    pyroclast.setOwner(this);
                }
                pyroclast.setExplosionPower(this.getExplosionPower() / 2.0F);
                pyroclast.setPotency((int) this.getExtraDamage());
                pyroclast.setFlaming(0);
                MobUtil.shootUp(pyroclast, this, MobUtil.ceilingVelocity(this, 0.75F));
            }

            this.discard();
        }
    }

}
