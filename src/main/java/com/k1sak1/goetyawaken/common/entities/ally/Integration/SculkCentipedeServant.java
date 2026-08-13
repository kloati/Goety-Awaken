package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.kyanite.deeperdarker.content.entities.DDMobType;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.UUID;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class SculkCentipedeServant extends Summoned implements NeutralMob {
    public final AnimationState attackState = new AnimationState();
    private UUID angerTarget;
    private int remainingAngerTime;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(15, 30);

    public SculkCentipedeServant(EntityType<? extends SculkCentipedeServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SculkCentipedeServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SculkCentipedeServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SculkCentipedeServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.SculkCentipedeServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.SculkCentipedeServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SculkCentipedeServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.SculkCentipedeServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.SculkCentipedeServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.SculkCentipedeServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.SculkCentipedeServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.SCULK_CENTIPEDE_SERVANT_LIMIT.get();
    }

    @Override
    public MobType getMobType() {
        return DDMobType.SCULK;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        level().broadcastEntityEvent(this, (byte) 4);
        return super.doHurtTarget(pEntity);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackState.start(this.tickCount);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime) {
        this.remainingAngerTime = pRemainingPersistentAngerTime;
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(UUID pPersistentAngerTarget) {
        this.angerTarget = pPersistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (health != null && attack != null && speed != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.SculkCentipedeServantHealth.get() * 1.5D);
                attack.setBaseValue(AttributesConfig.SculkCentipedeServantDamage.get() + 4.0D);
                speed.setBaseValue(AttributesConfig.SculkCentipedeServantMovementSpeed.get() + 0.05D);
            } else {
                health.setBaseValue(AttributesConfig.SculkCentipedeServantHealth.get());
                attack.setBaseValue(AttributesConfig.SculkCentipedeServantDamage.get());
                speed.setBaseValue(AttributesConfig.SculkCentipedeServantMovementSpeed.get());
            }
        }
        this.refreshDimensions();
        this.setHealth(this.getMaxHealth());
    }
}
