package com.k1sak1.goetyawaken.common.world.structures;

import com.k1sak1.goetyawaken.common.world.structures.ModStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class SecretKitchenStructure extends Structure {

        public static final Codec<SecretKitchenStructure> CODEC = RecordCodecBuilder
                        .<SecretKitchenStructure>mapCodec((p_227640_) -> {
                                return p_227640_.group(
                                                settingsCodec(p_227640_),
                                                StructureTemplatePool.CODEC.fieldOf("start_pool")
                                                                .forGetter((p_227656_) -> {
                                                                        return p_227656_.startPool;
                                                                }),
                                                ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name")
                                                                .forGetter((p_227654_) -> {
                                                                        return p_227654_.startJigsawName;
                                                                }),
                                                Codec.intRange(0, 30).fieldOf("size").forGetter((p_227652_) -> {
                                                        return p_227652_.maxDepth;
                                                }),
                                                HeightProvider.CODEC.fieldOf("start_height").forGetter((p_227649_) -> {
                                                        return p_227649_.startHeight;
                                                }),
                                                Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap")
                                                                .forGetter((p_227644_) -> {
                                                                        return p_227644_.projectStartToHeightmap;
                                                                }),
                                                Codec.intRange(1, 1024).fieldOf("max_distance_from_center")
                                                                .forGetter((p_227642_) -> {
                                                                        return p_227642_.maxDistanceFromCenter;
                                                                }))
                                                .apply(p_227640_, SecretKitchenStructure::new);
                        }).codec();

        private final Holder<StructureTemplatePool> startPool;
        private final Optional<ResourceLocation> startJigsawName;
        private final int maxDepth;
        private final HeightProvider startHeight;
        private final Optional<Heightmap.Types> projectStartToHeightmap;
        private final int maxDistanceFromCenter;

        public SecretKitchenStructure(
                        StructureSettings p_227627_,
                        Holder<StructureTemplatePool> p_227628_,
                        Optional<ResourceLocation> p_227629_,
                        int p_227630_,
                        HeightProvider p_227631_,
                        Optional<Heightmap.Types> p_227633_,
                        int p_227634_) {
                super(p_227627_);
                this.startPool = p_227628_;
                this.startJigsawName = p_227629_;
                this.maxDepth = p_227630_;
                this.startHeight = p_227631_;
                this.projectStartToHeightmap = p_227633_;
                this.maxDistanceFromCenter = p_227634_;
        }

        @Override
        protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext p_227636_) {

                ChunkPos chunkpos = p_227636_.chunkPos();
                BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), 129, chunkpos.getMinBlockZ());
                return JigsawPlacement.addPieces(
                                p_227636_,
                                this.startPool,
                                this.startJigsawName,
                                this.maxDepth,
                                blockpos,
                                false,
                                this.projectStartToHeightmap,
                                this.maxDistanceFromCenter);
        }

        @Override
        public @NotNull StructureType<?> type() {
                return ModStructureTypes.SECRET_KITCHEN.get();
        }
}
