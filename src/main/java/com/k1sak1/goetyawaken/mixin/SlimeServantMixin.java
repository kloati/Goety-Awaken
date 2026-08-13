package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.k1sak1.goetyawaken.utils.SwellingPendantEnhancer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeServant.class)
public class SlimeServantMixin {
    @Unique
    private static final String PENDING_KEY = "ga:swelling_pendant_pending";

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType spawnType, SpawnGroupData spawnData, CompoundTag tag,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (spawnType == MobSpawnType.MOB_SUMMONED) {
            SlimeServant self = (SlimeServant) (Object) this;
            self.getPersistentData().putBoolean(PENDING_KEY, true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        SlimeServant self = (SlimeServant) (Object) this;
        if (!self.level().isClientSide()
                && self.tickCount <= 1
                && self.getPersistentData().getBoolean(PENDING_KEY)) {
            self.getPersistentData().remove(PENDING_KEY);
            LivingEntity owner = self.getTrueOwner();
            if (owner != null && owner.isAlive()) {
                SwellingPendantEnhancer.tryEnhance(self, owner);
            }
        }
    }
}
