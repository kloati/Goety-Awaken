package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.world.structures.foundation.TerrainConformUtil;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorSurfaceMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"))
    private void goetyawaken_applyFoundation(WorldGenLevel pLevel, ChunkAccess pChunk,
            StructureManager pStructureManager,
            CallbackInfo ci) {
        TerrainConformUtil.applyDuringSurface(pLevel, pStructureManager, pChunk);
    }
}
