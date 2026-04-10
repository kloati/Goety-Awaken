package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeletonServant.class)
public abstract class AbstractSkeletonServantMixin {

    @Shadow
    public abstract EntityType<?> getVariant(Player player, Level level, BlockPos blockPos);

    @Inject(method = "getVariant(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/entity/EntityType;", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifyGetVariantForDesertBiome(Player player, Level level, BlockPos blockPos,
            CallbackInfoReturnable<EntityType<?>> cir) {
        boolean isDesert = level.getBiome(blockPos).is(Tags.Biomes.IS_DESERT);
        if (isDesert) {
            cir.setReturnValue(ModEntityType.PARCHED_SERVANT.get());
        }
    }
}