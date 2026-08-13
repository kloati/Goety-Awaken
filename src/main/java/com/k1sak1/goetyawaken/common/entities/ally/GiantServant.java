package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class GiantServant extends ZombieServant {

    public GiantServant(EntityType<? extends ZombieServant> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return pLevel.getPathfindingCostFromLightLevels(pPos);
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 10.440001F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GiantServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GiantServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GiantServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.GiantServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.GiantServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.GiantServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.GiantServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.GiantServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.GiantServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.GiantServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return Config.GIANT_SERVANT_LIMIT.get();
    }
}
