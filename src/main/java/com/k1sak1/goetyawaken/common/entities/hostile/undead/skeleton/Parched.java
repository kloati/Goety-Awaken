package com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;

public class Parched extends AbstractSkeleton {
    public Parched(EntityType<? extends AbstractSkeleton> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

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

    protected SoundEvent getDeathSound() {
        return ModSounds.PARCHED_DEATH.get();
    }

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

    protected AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrow = super.getArrow(pArrowStack, pDistanceFactor);
        if (abstractarrow instanceof Arrow) {
            ((Arrow) abstractarrow).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
        }

        return abstractarrow;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        if (pEffectInstance.getEffect() == MobEffects.WEAKNESS) {
            return false;
        }
        return super.canBeAffected(pEffectInstance);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && source.getDirectEntity() != null &&
                (source.getDirectEntity().getType().toString().contains("necro_bolt"))) {
            if (source.getEntity() instanceof Player player) {
                this.convertToServant(player);
            }
        }
        super.die(source);
    }

    private boolean convertToServant(Player player) {
        if (this.level() instanceof ServerLevel serverLevel) {
            ParchedServant servant = (ParchedServant) this
                    .convertTo(ModEntityType.PARCHED_SERVANT.get(), true);
            if (servant != null) {
                servant.setTrueOwner(player);
                if (this.getTarget() != null) {
                    servant.setTarget(this.getTarget());
                }
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = this.getItemBySlot(slot);
                    if (!stack.isEmpty()) {
                        servant.setItemSlot(slot, stack.copy());
                    }
                }
                servant.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(servant.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                if (!servant.isSilent()) {
                    servant.level().levelEvent(null, 1026, servant.blockPosition(), 0);
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                return true;
            }
        }
        return false;
    }
}