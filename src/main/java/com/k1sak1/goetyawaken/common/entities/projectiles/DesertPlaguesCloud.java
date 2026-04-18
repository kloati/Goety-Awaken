package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.InsectSwarm;
import com.Polarice3.Goety.common.entities.projectiles.AbstractSpellCloud;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

public class DesertPlaguesCloud extends AbstractSpellCloud {
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(DesertPlaguesCloud.class,
            EntityDataSerializers.INT);

    public DesertPlaguesCloud(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setRainParticle(
                new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SNOW_BLOCK.defaultBlockState()));
    }

    public DesertPlaguesCloud(Level pLevel, LivingEntity pOwner, LivingEntity pTarget) {
        super(com.k1sak1.goetyawaken.common.entities.ModEntityType.DESERT_PLAGUES_CLOUD.get(), pLevel);
        if (pOwner != null) {
            this.setOwner(pOwner);
        }
        if (pTarget != null) {
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(pTarget.getX(), pTarget.getY(),
                    pTarget.getZ());

            while (blockpos$mutable.getY() < pTarget.getY() + 8.0D
                    && !this.level().getBlockState(blockpos$mutable).blocksMotion()) {
                blockpos$mutable.move(Direction.UP);
            }
            this.setPos(pTarget.getX(), blockpos$mutable.getY(), pTarget.getZ());
            this.setTarget(pTarget);
        }
        this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5F, 1.25F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COLOR, 0xb1ebdc);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Color", this.getLightningColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Color")) {
            this.setLightningColor(compound.getInt("Color"));
        }
    }

    public int getLightningColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setLightningColor(int color) {
        this.entityData.set(DATA_COLOR, color);
    }

    @Override
    public ParticleOptions getRainParticle() {
        return new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SNOW_BLOCK.defaultBlockState());
    }

    @Override
    public int getColor() {
        return 800000;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.isStaff()) {
                if (this.getTarget() == null) {
                    for (Entity entity : this.level().getEntitiesOfClass(Entity.class,
                            this.getBoundingBox().inflate(16.0F))) {
                        LivingEntity livingEntity = MobUtil.getLivingTarget(entity);
                        if (livingEntity != null) {
                            if (MobUtil.ownedPredicate(this).test(livingEntity)) {
                                this.setTarget(livingEntity);
                            }
                        }
                    }
                }
                float speed = 0.175F;
                if (this.getTarget() != null && this.getTarget().isAlive()) {
                    this.setDeltaMovement(Vec3.ZERO);
                    double d0 = this.getTarget().getX() - this.getX();
                    double d1 = (this.getTarget().getY() + 8.0D) - this.getY();
                    double d2 = this.getTarget().getZ() - this.getZ();
                    double d = Math.sqrt((d0 * d0 + d2 * d2));
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    if (d > 0.5) {
                        this.setDeltaMovement(this.getDeltaMovement().add(d0 / d3, d1 / d3, d2 / d3).scale(speed));
                    }
                }
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
            if (this.tickCount % 2 == 0) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                float radius = this.getRadius();
                float area = (float) Math.PI * radius * radius;
                float height = 8.0f;
                for (int i = 0; i < Math.max(5, (int) (area / 3)); i++) {
                    float angle = this.random.nextFloat() * (float) (Math.PI * 2);
                    float distance = this.random.nextFloat() * radius;
                    double x = this.getX() + Math.cos(angle) * distance;
                    double z = this.getZ() + Math.sin(angle) * distance;
                    double y = this.getY() + (this.random.nextFloat() - 0.5) * height - 4;
                    serverLevel.sendParticles(
                            ModParticleTypes.FLY.get(),
                            x, y, z,
                            1,
                            0.0D, 0.0D, 0.0D,
                            0.0D);
                }
            }
        }
    }

    public void hurtEntities(LivingEntity livingEntity) {
        if (livingEntity != null) {
            float baseDamage = SpellConfig.HailDamage.get().floatValue() * WandUtil.damageMultiply();
            baseDamage += this.getExtraDamage();
            if (livingEntity.hurt(ModDamageSource.frostBreath(this, this.getOwner()), baseDamage / 2)) {
                livingEntity
                        .addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(5)));
            }
            livingEntity
                    .addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), MathHelper.secondsToTicks(5)));
            livingEntity
                    .addEffect(new MobEffectInstance(MobEffects.WEAKNESS, MathHelper.secondsToTicks(5)));
        }
        ColorUtil colorUtil = new ColorUtil(this.getLightningColor());
        if (this.level() instanceof ServerLevel serverLevel) {
            if (livingEntity != null && !livingEntity.isDeadOrDying()
                    && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                if (livingEntity.isSensitiveToWater()) {
                    livingEntity.hurt(livingEntity.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }
                if (livingEntity.hurt(ModDamageSource.swarm(this, this.getOwner()),
                        SpellConfig.SwarmDamage.get().floatValue() * WandUtil.damageMultiply() / 2)) {
                    if (livingEntity instanceof LivingEntity) {
                        LivingEntity livingTarget = livingEntity;
                        MobEffect mobEffect = MobEffects.POISON;
                        if (CuriosFinder.hasWildRobe(this.getOwner())) {
                            mobEffect = GoetyEffects.ACID_VENOM.get();
                        }
                        livingTarget.addEffect(
                                new MobEffectInstance(mobEffect,
                                        MathHelper.secondsToTicks(5)));
                    }

                    if (!livingEntity.isAlive()) {
                        InsectSwarm insectSwarm = new InsectSwarm(serverLevel, this.getOwner(),
                                livingEntity.position());
                        insectSwarm.setLimitedLife(200);
                        float extraDamage = this.getExtraDamage();
                        if (extraDamage > 0) {
                            insectSwarm.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(),
                                    EffectsUtil.infiniteEffect(), (int) extraDamage, false, false));
                        }
                        serverLevel.addFreshEntity(insectSwarm);
                    }
                }

                if (this.random.nextFloat() <= 0.05F) {
                    Vec3 vec3 = this.position();
                    float damage = SpellConfig.ThunderboltDamage.get().floatValue() * WandUtil.damageMultiply();
                    damage += this.getExtraDamage();
                    BlockHitResult rayTraceResult = this.blockResult(serverLevel, this, 16);
                    Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(serverLevel,
                            BlockPos.containing(rayTraceResult.getLocation()), 16);
                    if (lightningRod.isPresent() && !this.isStaff()) {
                        BlockPos blockPos = lightningRod.get();
                        ModNetwork.sendToALL(new SThunderBoltPacket(vec3,
                                new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), colorUtil, 10));
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.THUNDERBOLT.get(),
                                this.getSoundSource(), 1.0F, 1.0F);
                    } else {
                        Vec3 vec31 = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2,
                                livingEntity.getZ());
                        ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, colorUtil, 10));
                        if (livingEntity.hurt(ModDamageSource.indirectShock(this, this.getOwner()), damage / 2)) {
                            float chance = this.isStaff() ? 0.25F : 0.05F;
                            float chainDamage = damage / 2.0F;
                            if (serverLevel.isThundering() && serverLevel.isRainingAt(livingEntity.blockPosition())) {
                                chance += 0.25F;
                                chainDamage = damage;
                            }
                            if (serverLevel.getRandom().nextFloat() <= chance) {
                                livingEntity.addEffect(
                                        new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                            }
                            if (this.isStaff()) {
                                WandUtil.chainLightning(livingEntity, this.getOwner() != null ? this.getOwner() : null,
                                        6.0D, chainDamage);
                            }
                        }
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.THUNDERBOLT.get(),
                                this.getSoundSource(), 1.0F, 1.0F);
                        if (!livingEntity.isAlive()) {
                            InsectSwarm insectSwarm = new InsectSwarm(serverLevel, this.getOwner(),
                                    livingEntity.position());
                            insectSwarm.setLimitedLife(200);
                            float extraDamage = this.getExtraDamage();
                            if (extraDamage > 0) {
                                insectSwarm.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(),
                                        EffectsUtil.infiniteEffect(), (int) extraDamage, false, false));
                            }
                            serverLevel.addFreshEntity(insectSwarm);
                        }
                    }
                }
            }
        }
    }

    public BlockHitResult blockResult(Level worldIn, Entity entity, double range) {
        float f = entity.getXRot();
        float f1 = entity.getYRot();
        Vec3 vector3d = entity.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double) f6 * range, (double) f5 * range, (double) f7 * range);
        return worldIn
                .clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
    }
}