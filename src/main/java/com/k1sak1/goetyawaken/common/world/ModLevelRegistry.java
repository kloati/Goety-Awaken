package com.k1sak1.goetyawaken.common.world;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class ModLevelRegistry {

    public static void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (!biome.is(biomeResourceKey -> biomeResourceKey.registry().getNamespace().contains("alexscaves"))) {
            if (biome.is(ModTags.Biomes.ZOMBIE_DARKGUARD_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.ZOMBIE_DARKGUARD.get(),
                        5,
                        1,
                        3));
            }

            if (biome.is(ModTags.Biomes.SKELETON_VANGUARD_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.SKELETON_VANGUARD.get(),
                        5,
                        1,
                        3));
            }

            // if (biome.is(ModTags.Biomes.PARCHED_SPAWN)) {
            // builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new
            // MobSpawnSettings.SpawnerData(
            // ModEntityType.PARCHED.get(),
            // 20,
            // 1,
            // 4));
            // }

            if (biome.is(ModTags.Biomes.HOSTILE_TROPICAL_SLIME_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_TROPICAL_SLIME.get(),
                        10,
                        1,
                        1));
            }

            if (biome.is(ModTags.Biomes.HOSTILE_GNASHER_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_GNASHER.get(),
                        10,
                        1,
                        2));
            }

            if (biome.is(ModTags.Biomes.HOSTILE_SNAPPER_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_GNASHER.get(),
                        10,
                        1,
                        2));
            }

            if (biome.is(ModTags.Biomes.SUNKEN_SKELETON_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.SUNKEN_SKELETON.get(),
                        10,
                        1,
                        2));
            }

            if (biome.is(ModTags.Biomes.HOSTILE_MINI_GHAST_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_MINI_GHAST.get(),
                        1,
                        1,
                        1));
            }

            if (biome.is(ModTags.Biomes.HOSTILE_SPIDER_CREEDER_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_SPIDER_CREEDER.get(),
                        1,
                        1,
                        1));
            }

            if (biome.is(ModTags.Biomes.HOSTILE_TWILIGHT_GOAT_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_TWILIGHT_GOAT.get(),
                        10,
                        1,
                        1));
            }

        }
    }

    public static boolean containsName(ResourceKey<Biome> biomeResourceKey, String string) {
        return biomeResourceKey.registry().getNamespace().contains(string);
    }
}