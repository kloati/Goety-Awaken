package com.k1sak1.goetyawaken.common.world;

import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModTags;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMobSpawnStructureModifier implements StructureModifier {
    private static final int HOSTILE_WILDFIRE_SPAWN_WEIGHT = 1;
    private static final int HOSTILE_WILDFIRE_SPAWN_ROLLS = 20;

    private static final RegistryObject<Codec<? extends StructureModifier>> SERIALIZER = RegistryObject.create(
            new ResourceLocation(GoetyAwaken.MODID, "structure_mob_spawns"),
            ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS,
            GoetyAwaken.MODID);

    public ModMobSpawnStructureModifier() {
    }

    @Override
    public void modify(Holder<Structure> structure, Phase phase,
            ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == Phase.ADD) {
            ModStructureSpawnRegistry.modifyStructure(structure, builder);
        }
    }

    public Codec<? extends StructureModifier> codec() {
        return (Codec) SERIALIZER.get();
    }

    public static Codec<ModMobSpawnStructureModifier> makeCodec() {
        return Codec.unit(ModMobSpawnStructureModifier::new);
    }

    public static class ModStructureSpawnRegistry {
        public static void modifyStructure(Holder<Structure> structure,
                ModifiableStructureInfo.StructureInfo.Builder builder) {
            if (Config.hostileWildfireSpawn && structure.is(ModTags.Structures.WILDFIRE_SPAWN)
                    && HOSTILE_WILDFIRE_SPAWN_WEIGHT > 0) {
                builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER)
                        .addSpawn(new MobSpawnSettings.SpawnerData(ModEntityType.HOSTILE_WILDFIRE.get(),
                                HOSTILE_WILDFIRE_SPAWN_WEIGHT, 1, 1));
            }
        }
    }

    public static int getHostileWildfireSpawnRolls() {
        return HOSTILE_WILDFIRE_SPAWN_ROLLS;
    }
}
