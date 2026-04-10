package com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ParchedServant extends AbstractSkeletonServant {

    public ParchedServant(EntityType<? extends AbstractSkeletonServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkeletonServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SkeletonServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkeletonServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SkeletonServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SkeletonServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.SkeletonServantDamage.get());
    }

    public double getBaseRangeDamage() {
        return AttributesConfig.StrayServantRangeDamage.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int soundIndex = this.random.nextInt(4) + 1;
        switch (soundIndex) {
            case 1:
                return ModSounds.PARCHED_IDLE_1.get();
            case 2:
                return ModSounds.PARCHED_IDLE_2.get();
            case 3:
                return ModSounds.PARCHED_IDLE_3.get();
            case 4:
                return ModSounds.PARCHED_IDLE_4.get();
            default:
                return ModSounds.PARCHED_IDLE_1.get();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        int soundIndex = this.random.nextInt(4) + 1;
        switch (soundIndex) {
            case 1:
                return ModSounds.PARCHED_HURT_1.get();
            case 2:
                return ModSounds.PARCHED_HURT_2.get();
            case 3:
                return ModSounds.PARCHED_HURT_3.get();
            case 4:
                return ModSounds.PARCHED_HURT_4.get();
            default:
                return ModSounds.PARCHED_HURT_1.get();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PARCHED_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        int soundIndex = this.random.nextInt(4) + 1;
        switch (soundIndex) {
            case 1:
                return ModSounds.PARCHED_STEP_1.get();
            case 2:
                return ModSounds.PARCHED_STEP_2.get();
            case 3:
                return ModSounds.PARCHED_STEP_3.get();
            case 4:
                return ModSounds.PARCHED_STEP_4.get();
            default:
                return ModSounds.PARCHED_STEP_1.get();
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected AbstractArrow getMobArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrowentity = super.getMobArrow(pArrowStack, pDistanceFactor);
        if (abstractarrowentity instanceof Arrow) {
            int amplifier = this.isUpgraded() ? 1 : 0;
            ((Arrow) abstractarrowentity)
                    .addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, amplifier));
        }

        return abstractarrowentity;
    }

    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        if (pEffectInstance.getEffect() == MobEffects.WEAKNESS) {
            return false;
        }
        return super.canBeAffected(pEffectInstance);
    }
}