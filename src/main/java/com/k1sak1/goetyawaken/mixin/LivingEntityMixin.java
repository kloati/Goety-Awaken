package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.mobenchant.IMobEnchantable;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantCapability;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantEventHandler;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantType;
import com.k1sak1.goetyawaken.init.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IAncientGlint, IMobEnchantable {

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_ANCIENT_GLINT = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<String> DATA_GLINT_TEXTURE_TYPE = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.STRING);

    @Unique
    private static final EntityDataAccessor<Integer> DATA_ANCIENT_HUNT_NUMBER = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.INT);

    @Unique
    private MobEnchantCapability goetyawaken$mobEnchantCapability;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineAncientGlintData(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getEntityData().define(DATA_ANCIENT_GLINT, false);
        ((LivingEntity) (Object) this).getEntityData().define(DATA_GLINT_TEXTURE_TYPE, "ancient");
        ((LivingEntity) (Object) this).getEntityData().define(DATA_ANCIENT_HUNT_NUMBER, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeAncientGlintData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("AncientGlint", this.hasAncientGlint());
        tag.putString("GlintTextureType", this.getGlintTextureType());
        tag.putInt("AncientHuntNumber", this.getAncientHuntNumber());
        if (this.goetyawaken$mobEnchantCapability != null) {
            this.goetyawaken$mobEnchantCapability.saveToNBT(tag);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAncientGlintData(CompoundTag tag, CallbackInfo ci) {
        if (this.goetyawaken$mobEnchantCapability == null) {
            this.goetyawaken$mobEnchantCapability = new MobEnchantCapability((LivingEntity) (Object) this);
        }
        this.goetyawaken$mobEnchantCapability.loadFromNBT(tag);
        MobEnchantEventHandler.syncCapabilityToCache((LivingEntity) (Object) this,
                this.goetyawaken$mobEnchantCapability);

        if (tag.contains("AncientGlint")) {
            this.setAncientGlint(tag.getBoolean("AncientGlint"));
        }
        if (tag.contains("GlintTextureType")) {
            this.setGlintTextureType(tag.getString("GlintTextureType"));
        }
        if (tag.contains("AncientHuntNumber")) {
            this.setAncientHuntNumber(tag.getInt("AncientHuntNumber"));
        }

        if (this.goetyawaken$mobEnchantCapability.getMobEnchantLevel(MobEnchantType.HUGE) > 0) {
            ((LivingEntity) (Object) this).refreshDimensions();
        }
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModEffects.MUCILAGE_POSSESSION.get())) {
            cir.setReturnValue(true);
        }
    }

    @Override
    @Unique
    public boolean hasAncientGlint() {
        return ((LivingEntity) (Object) this).getEntityData().get(DATA_ANCIENT_GLINT);
    }

    @Override
    @Unique
    public void setAncientGlint(boolean hasGlint) {
        ((LivingEntity) (Object) this).getEntityData().set(DATA_ANCIENT_GLINT, hasGlint);
    }

    @Override
    @Unique
    public String getGlintTextureType() {
        return ((LivingEntity) (Object) this).getEntityData().get(DATA_GLINT_TEXTURE_TYPE);
    }

    @Override
    @Unique
    public void setGlintTextureType(String textureType) {
        ((LivingEntity) (Object) this).getEntityData().set(DATA_GLINT_TEXTURE_TYPE, textureType);
    }

    @Override
    @Unique
    public int getAncientHuntNumber() {
        return ((LivingEntity) (Object) this).getEntityData().get(DATA_ANCIENT_HUNT_NUMBER);
    }

    @Override
    @Unique
    public void setAncientHuntNumber(int huntNumber) {
        ((LivingEntity) (Object) this).getEntityData().set(DATA_ANCIENT_HUNT_NUMBER, huntNumber);
    }

    @Unique
    @Override
    public int getMobEnchantLevel(MobEnchantType enchantType) {
        if (this.goetyawaken$mobEnchantCapability == null) {
            this.goetyawaken$mobEnchantCapability = MobEnchantEventHandler.getCapability((LivingEntity) (Object) this);
        }
        return this.goetyawaken$mobEnchantCapability.getMobEnchantLevel(enchantType);
    }

    @Unique
    @Override
    public void setMobEnchantLevel(MobEnchantType enchantType, int level) {
        if (this.goetyawaken$mobEnchantCapability == null) {
            this.goetyawaken$mobEnchantCapability = MobEnchantEventHandler.getCapability((LivingEntity) (Object) this);
        }
        this.goetyawaken$mobEnchantCapability.setMobEnchantLevel(enchantType, level);
    }

    @Unique
    @Override
    public java.util.Map<MobEnchantType, Integer> getMobEnchants() {
        if (this.goetyawaken$mobEnchantCapability == null) {
            this.goetyawaken$mobEnchantCapability = MobEnchantEventHandler.getCapability((LivingEntity) (Object) this);
        }
        return this.goetyawaken$mobEnchantCapability.getMobEnchants();
    }

    @Unique
    @Override
    public void setMobEnchants(java.util.Map<MobEnchantType, Integer> enchants) {
        if (this.goetyawaken$mobEnchantCapability == null) {
            this.goetyawaken$mobEnchantCapability = MobEnchantEventHandler.getCapability((LivingEntity) (Object) this);
        }
        this.goetyawaken$mobEnchantCapability.setMobEnchants(enchants);
    }

    @Unique
    private MobEnchantCapability goetyawaken$getMobEnchantCapability() {
        if (this.goetyawaken$mobEnchantCapability == null) {
            MobEnchantCapability cached = MobEnchantEventHandler.getCapabilityFromCache((LivingEntity) (Object) this);
            if (cached != null) {
                this.goetyawaken$mobEnchantCapability = cached;
            } else {
                this.goetyawaken$mobEnchantCapability = new MobEnchantCapability((LivingEntity) (Object) this);
                MobEnchantEventHandler.syncCapabilityToCache((LivingEntity) (Object) this,
                        this.goetyawaken$mobEnchantCapability);
            }
        }
        return this.goetyawaken$mobEnchantCapability;
    }

    @Unique
    @Override
    public MobEnchantCapability getMobEnchantCapabilityInstance() {
        return goetyawaken$getMobEnchantCapability();
    }

}
