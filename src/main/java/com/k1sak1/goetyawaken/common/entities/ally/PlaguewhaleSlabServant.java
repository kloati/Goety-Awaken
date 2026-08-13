package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.init.ModSounds;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class PlaguewhaleSlabServant extends ToxifinServant {
   public PlaguewhaleSlabServant(EntityType entityType, Level world) {
      super(entityType, world);
      if (this.wanderGoal != null) {
         this.wanderGoal.setInterval(400);
      }
   }

   public int getAttackDuration() {
      return 60;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PlaguewhaleSlabServantDamage.get())
            .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PlaguewhaleSlabServantMovementSpeed.get())
            .add(Attributes.FOLLOW_RANGE, AttributesConfig.PlaguewhaleSlabServantFollowRange.get())
            .add(Attributes.MAX_HEALTH, AttributesConfig.PlaguewhaleSlabServantHealth.get());
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return createAttributes();
   }

   @Override
   public void setConfigurableAttributes() {
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
            AttributesConfig.PlaguewhaleSlabServantHealth.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
            AttributesConfig.PlaguewhaleSlabServantDamage.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
            AttributesConfig.PlaguewhaleSlabServantMovementSpeed.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
            AttributesConfig.PlaguewhaleSlabServantFollowRange.get());
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble() ? ModSounds.PLAGUEWHALE_AMBIENT.get()
            : ModSounds.PLAGUEWHALE_AMBIENT_LAND.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSrc) {
      return this.isInWaterOrBubble() ? ModSounds.PLAGUEWHALE_HURT.get()
            : ModSounds.PLAGUEWHALE_HURT_LAND.get();
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble() ? ModSounds.PLAGUEWHALE_DEATH.get()
            : ModSounds.PLAGUEWHALE_DEATH_LAND.get();
   }

   protected SoundEvent getFlopSound() {
      return ModSounds.PLAGUEWHALE_FLOP.get();
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }
}
