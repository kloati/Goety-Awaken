package com.k1sak1.goetyawaken.common.entities.ally;

import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.server.level.ServerLevel;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.k1sak1.goetyawaken.init.ModSounds;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class PoisonousPotatoZombieServant extends ZombieServant {

    public PoisonousPotatoZombieServant(EntityType<? extends ZombieServant> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean killedEntity(ServerLevel world, LivingEntity killedEntity) {
        boolean flag = super.killedEntity(world, killedEntity);
        if (killedEntity instanceof Zombie zombieEntity) {
            if (ForgeEventFactory.canLivingConvert(zombieEntity,
                    ModEntityType.POISONOUS_POTATO_ZOMBIE_SERVANT.get(), (timer) -> {
                    })) {
                PoisonousPotatoZombieServant servant = zombieEntity.convertTo(
                        ModEntityType.POISONOUS_POTATO_ZOMBIE_SERVANT.get(), true);
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
                    ForgeEventFactory.onLivingConvert(zombieEntity, servant);
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
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        if (flag && pEntity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                    MathHelper.secondsToTicks(5), 0, false, false));
        }
        return flag;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PoisonousPotatoZombieServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PoisonousPotatoZombieServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.PoisonousPotatoZombieServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.PoisonousPotatoZombieServantArmorToughness.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.POISONOUS_POTATO_ZOMBIE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.POISONOUS_POTATO_ZOMBIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.POISONOUS_POTATO_ZOMBIE_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.POISONOUS_POTATO_ZOMBIE_STEP.get();
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.PoisonousPotatoZombieServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.PoisonousPotatoZombieServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.PoisonousPotatoZombieServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.PoisonousPotatoZombieServantArmorToughness.get());
    }

}
