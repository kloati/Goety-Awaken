package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IceCreeperServant extends AbstractCreeperServant {
        private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(
                        IceCreeperServant.class,
                        EntityDataSerializers.INT);
        private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(
                        IceCreeperServant.class,
                        EntityDataSerializers.BOOLEAN);
        private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(
                        IceCreeperServant.class,
                        EntityDataSerializers.BOOLEAN);

        public IceCreeperServant(EntityType<? extends Owned> type, Level worldIn) {
                super(type, worldIn);
                this.explosionRadius = 3;
        }

        @Override
        protected EntityDataAccessor<Integer> getSwellDirAccessor() {
                return DATA_SWELL_DIR;
        }

        @Override
        protected EntityDataAccessor<Boolean> getPoweredAccessor() {
                return DATA_IS_POWERED;
        }

        @Override
        protected EntityDataAccessor<Boolean> getIgnitedAccessor() {
                return DATA_IS_IGNITED;
        }

        @Override
        protected void onSwellTargetApproach(LivingEntity target) {
                this.getNavigation().stop();
        }

        @Override
        protected void spawnSwellParticles() {
                if (this.level().isClientSide) {
                        if (this.tickCount % 5 == 0) {
                                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                                                this.getX() + (this.random.nextDouble() - 0.5D)
                                                                * (double) this.getBbWidth(),
                                                this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                                                this.getZ() + (this.random.nextDouble() - 0.5D)
                                                                * (double) this.getBbWidth(),
                                                (this.random.nextDouble() - 0.5D) * 0.1D,
                                                (this.random.nextDouble() - 0.5D) * 0.1D,
                                                (this.random.nextDouble() - 0.5D) * 0.1D);
                        }

                        if (this.swell > 0 && this.swell % 5 == 0) {
                                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                                                this.getX() + (this.random.nextDouble() - 0.5D)
                                                                * (double) this.getBbWidth(),
                                                this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                                                this.getZ() + (this.random.nextDouble() - 0.5D)
                                                                * (double) this.getBbWidth(),
                                                (this.isPowered() ? 0.5D : 1.0D), 0.0D, 0.0D);
                        }
                }
        }

        public static AttributeSupplier.Builder setCustomAttributes() {
                return Monster.createMonsterAttributes()
                                .add(Attributes.MAX_HEALTH, AttributesConfig.IceCreeperServantHealth.get())
                                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                                .add(Attributes.ARMOR, AttributesConfig.IceCreeperServantArmor.get())
                                .add(Attributes.ARMOR_TOUGHNESS,
                                                AttributesConfig.IceCreeperServantArmorToughness.get());
        }

        @Override
        public void setConfigurableAttributes() {
                MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                                AttributesConfig.IceCreeperServantHealth.get());
                MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
                MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 0.5D);
                MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                                AttributesConfig.IceCreeperServantArmor.get());
                MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                                AttributesConfig.IceCreeperServantArmorToughness.get());
        }

        @Override
        protected void applyEffectToEntity(LivingEntity entity) {
                entity.addEffect(
                                new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(60), 3));
        }

        @Override
        protected void spawnExplosionParticles(ServerLevel serverLevel, float explosionRadius) {
                ColorUtil colorUtil = new ColorUtil(0x00a8ff);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                                new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                                explosionRadius * 2, 1),
                                vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                                new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                                explosionRadius, 1),
                                vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                                new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(),
                                                colorUtil.blue(),
                                                explosionRadius * 2, 1),
                                vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                                new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                                explosionRadius * 2.0F, 1),
                                vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);

                for (int i = 0; i < 100; i++) {
                        double offsetX = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                        double offsetY = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                        double offsetZ = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                                        this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                                        1, 0, 0, 0, 0);
                }
        }

        @Override
        public boolean canFreeze() {
                return false;
        }

        @Override
        public int getTicksFrozen() {
                return 0;
        }

}
