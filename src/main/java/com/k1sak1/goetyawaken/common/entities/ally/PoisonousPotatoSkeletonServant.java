package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;

public class PoisonousPotatoSkeletonServant extends AbstractSkeletonServant {

    public PoisonousPotatoSkeletonServant(EntityType<? extends AbstractSkeletonServant> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean killedEntity(ServerLevel world, LivingEntity killedEntity) {
        boolean flag = super.killedEntity(world, killedEntity);
        if (killedEntity instanceof AbstractSkeleton skeletonEntity) {
            if (ForgeEventFactory.canLivingConvert(skeletonEntity,
                    ModEntityType.POISONOUS_POTATO_SKELETON_SERVANT.get(), (timer) -> {
                    })) {
                PoisonousPotatoSkeletonServant servant = skeletonEntity.convertTo(
                        ModEntityType.POISONOUS_POTATO_SKELETON_SERVANT.get(), true);
                if (servant != null) {
                    if (this.getTrueOwner() != null) {
                        servant.setTrueOwner(this.getTrueOwner());
                    }
                    servant.finalizeSpawn(world, world.getCurrentDifficultyAt(servant.blockPosition()),
                            MobSpawnType.CONVERSION, null, null);
                    servant.setLimitedLife(10 * (15 + world.random.nextInt(45)));
                    if (this.isHostile()) {
                        servant.setHostile(true);
                    }
                    ForgeEventFactory.onLivingConvert(skeletonEntity, servant);
                    this.playSound(ModSounds.POISONOUS_POTATO_ZOMBIE_INFECT.get(), 1.0F, 1.0F);
                    if (!servant.isSilent()) {
                        world.levelEvent(null, 1026, servant.blockPosition(), 0);
                    }
                }
            }
        }
        return flag;
    }

    @Override
    protected AbstractArrow getMobArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractArrow = super.getMobArrow(pArrowStack, pDistanceFactor);
        if (abstractArrow instanceof Arrow arrow) {
            arrow.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                    MathHelper.secondsToTicks(5), 0, false, false));
        }
        return abstractArrow;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PoisonousPotatoSkeletonServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PoisonousPotatoSkeletonServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.PoisonousPotatoSkeletonServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PoisonousPotatoSkeletonServantDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.PoisonousPotatoSkeletonServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.PoisonousPotatoSkeletonServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.PoisonousPotatoSkeletonServantArmorToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.PoisonousPotatoSkeletonServantDamage.get());
    }

    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

}
