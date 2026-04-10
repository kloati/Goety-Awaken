package com.k1sak1.goetyawaken.common.entities.projectiles;

// import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
// import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.entities.projectiles.ModWitherSkull;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;

public class ModWitherSkullNoBlockBreak extends ModWitherSkull {
    public ModWitherSkullNoBlockBreak(EntityType<? extends ModWitherSkull> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ModWitherSkullNoBlockBreak(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY,
            double pOffsetZ) {
        super(pLevel, pShooter, pOffsetX, pOffsetY, pOffsetZ);
    }

    @Override
    protected void onHit(net.minecraft.world.phys.HitResult pResult) {
        net.minecraft.world.phys.HitResult.Type hitresult$type = pResult.getType();
        if (hitresult$type == net.minecraft.world.phys.HitResult.Type.ENTITY) {
            this.onHitEntity((net.minecraft.world.phys.EntityHitResult) pResult);
            this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.PROJECTILE_LAND, pResult.getLocation(),
                    net.minecraft.world.level.gameevent.GameEvent.Context.of(this,
                            (net.minecraft.world.level.block.state.BlockState) null));
        } else if (hitresult$type == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.world.phys.BlockHitResult blockhitresult = (net.minecraft.world.phys.BlockHitResult) pResult;
            this.onHitBlock(blockhitresult);
            net.minecraft.core.BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.PROJECTILE_LAND, blockpos,
                    net.minecraft.world.level.gameevent.GameEvent.Context.of(this,
                            this.level().getBlockState(blockpos)));
        }
        if (!this.level().isClientSide) {
            ExplosionUtil.lootExplode(this.level(), this, this.getX(), this.getY(), this.getZ(),
                    this.getExplosionPower(), false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ServerLevel serverLevel = (ServerLevel) this.level();
            // float radius = this.getExplosionPower();
            // serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(0.0F, 0.0F,
            // 0.0F, radius, 1),
            // this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            // serverLevel.sendParticles(new CircleExplodeParticleOption(0.0F, 0.0F, 0.0F,
            // radius, 1),
            // this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.discard();
        }
    }
}