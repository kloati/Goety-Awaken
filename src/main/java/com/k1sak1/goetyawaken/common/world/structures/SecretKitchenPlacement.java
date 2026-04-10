package com.k1sak1.goetyawaken.common.world.structures;

import com.k1sak1.goetyawaken.common.world.structures.ModStructurePlacementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class SecretKitchenPlacement extends StructurePlacement {

    private static final int TARGET_CHUNK_X = 3333 >> 4;
    private static final int TARGET_CHUNK_Z = 3333 >> 4;

    public static final Codec<SecretKitchenPlacement> CODEC = ExtraCodecs.validate(
            RecordCodecBuilder.mapCodec((p_204996_) -> {
                return placementCodec(p_204996_).apply(p_204996_, SecretKitchenPlacement::new);
            }),
            SecretKitchenPlacement::validate).codec();

    public SecretKitchenPlacement(Vec3i p_227000_, StructurePlacement.FrequencyReductionMethod p_227001_,
            float p_227002_, int p_227003_, Optional<StructurePlacement.ExclusionZone> p_227004_) {
        super(p_227000_, p_227001_, p_227002_, p_227003_, p_227004_);
    }

    public SecretKitchenPlacement(int p_204983_) {
        this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, p_204983_, Optional.empty());
    }

    private static DataResult<SecretKitchenPlacement> validate(SecretKitchenPlacement p_286361_) {
        return DataResult.success(p_286361_);
    }

    public static boolean mayInSecretKitchen(int chunkX, int chunkZ) {

        return Math.abs(chunkX - TARGET_CHUNK_X) <= 0 && Math.abs(chunkZ - TARGET_CHUNK_Z) <= 0;
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState p_256267_, int x, int z) {
        AtomicBoolean should = new AtomicBoolean(false);
        if (x == TARGET_CHUNK_X && z == TARGET_CHUNK_Z) {
            should.set(true);
        }
        return should.get();
    }

    @Override
    public StructurePlacementType<?> type() {
        return ModStructurePlacementTypes.SECRET_KITCHEN_PLACEMENT.get();
    }
}
