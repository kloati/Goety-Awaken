package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.kyanite.deeperdarker.content.entities.DDMobType;
import com.kyanite.deeperdarker.content.DDSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class SculkLeechServant extends Summoned {
    public final AnimationState attackState = new AnimationState();

    public SculkLeechServant(EntityType<? extends SculkLeechServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.5));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SculkLeechServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SculkLeechServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SculkLeechServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.SculkLeechServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.SculkLeechServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SculkLeechServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.SculkLeechServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.SculkLeechServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.SculkLeechServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.SculkLeechServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.SCULK_LEECH_SERVANT_LIMIT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return DDSounds.LEECH_HURT.get();
    }

    @Override
    public MobType getMobType() {
        return DDMobType.SCULK;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        if (pEntity instanceof Player player)
            player.giveExperiencePoints(-4);
        return super.doHurtTarget(pEntity);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4)
            this.attackState.start(this.tickCount);
        else
            super.handleEntityEvent(pId);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (health != null && attack != null && speed != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.SculkLeechServantHealth.get() * 1.5D);
                attack.setBaseValue(AttributesConfig.SculkLeechServantDamage.get() + 4.0D);
                speed.setBaseValue(AttributesConfig.SculkLeechServantMovementSpeed.get() + 0.05D);
            } else {
                health.setBaseValue(AttributesConfig.SculkLeechServantHealth.get());
                attack.setBaseValue(AttributesConfig.SculkLeechServantDamage.get());
                speed.setBaseValue(AttributesConfig.SculkLeechServantMovementSpeed.get());
            }
        }
        this.refreshDimensions();
        this.setHealth(this.getMaxHealth());
    }
}
