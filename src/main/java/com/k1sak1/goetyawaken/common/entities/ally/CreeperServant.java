package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class CreeperServant extends AbstractCreeperServant {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(CreeperServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(CreeperServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(CreeperServant.class,
            EntityDataSerializers.BOOLEAN);

    public CreeperServant(EntityType<? extends Owned> type, Level worldIn) {
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

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CreeperServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, AttributesConfig.CreeperServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.CreeperServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.CreeperServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CreeperServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.CreeperServantArmorToughness.get());
    }

    @Override
    protected void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f,
                    Level.ExplosionInteraction.NONE);
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0x00FF00);
                float explosionSize = (float) this.explosionRadius * f;
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil, explosionSize * 2.0F, 1),
                        this.getX(), BlockFinder.moveDownToGround(this) + 0.5F, this.getZ(),
                        1, 0, 0, 0, 0);
            }

            this.discard();
        }
    }

    @Override
    protected void spawnExplosionParticles(ServerLevel serverLevel, float explosionRadius) {
    }
}
