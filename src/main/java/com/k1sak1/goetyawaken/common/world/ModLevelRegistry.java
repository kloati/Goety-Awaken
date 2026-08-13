package com.k1sak1.goetyawaken.common.world;

import com.k1sak1.goetyawaken.Config;
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
        if (!biome.containsTag(ModTags.Biomes.COMMON_BLACKLIST)
                && !biome.is(biomeResourceKey -> biomeResourceKey.registry().getNamespace().contains("alexscaves"))) {
            if (Config.zombieDarkguardSpawn && biome.is(ModTags.Biomes.ZOMBIE_DARKGUARD_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.ZOMBIE_DARKGUARD.get(),
                        1,
                        1,
                        1));
            }

            if (Config.skeletonVanguardSpawn && biome.is(ModTags.Biomes.SKELETON_VANGUARD_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.SKELETON_VANGUARD.get(),
                        1,
                        1,
                        1));
            }

            if (Config.parchedSpawn && biome.is(ModTags.Biomes.PARCHED_SPAWN)
                    && !biome.is(ModTags.Biomes.PARCHED_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.PARCHED.get(),
                        20,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.PARCHED.get(), 1.0D, 0.15D);
            }

            if (Config.iceCreeperSpawn && biome.is(ModTags.Biomes.ICE_CREEPER_SPAWN)
                    && !biome.is(ModTags.Biomes.ICE_CREEPER_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.ICE_CREEPER.get(),
                        10,
                        1,
                        1));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.ICE_CREEPER.get(), 1.0D, 0.15D);
            }

            if (Config.boulderingZombieSpawn && biome.is(ModTags.Biomes.BOULDERING_ZOMBIE_SPAWN)
                    && !biome.is(ModTags.Biomes.BOULDERING_ZOMBIE_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.BOULDERING_ZOMBIE.get(),
                        10,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.BOULDERING_ZOMBIE.get(), 1.0D, 0.15D);
            }

            if (Config.jungleZombieSpawn && biome.is(ModTags.Biomes.JUNGLE_ZOMBIE_SPAWN)
                    && !biome.is(ModTags.Biomes.JUNGLE_ZOMBIE_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.JUNGLE_ZOMBIE.get(),
                        10,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.JUNGLE_ZOMBIE.get(), 1.0D, 0.15D);
            }

            if (Config.frozenZombieSpawn && biome.is(ModTags.Biomes.FROZEN_ZOMBIE_SPAWN)
                    && !biome.is(ModTags.Biomes.FROZEN_ZOMBIE_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.FROZEN_ZOMBIE.get(),
                        3,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.FROZEN_ZOMBIE.get(), 1.0D, 0.15D);
            }
            if (Config.hostileTropicalSlimeSpawn && biome.is(ModTags.Biomes.HOSTILE_TROPICAL_SLIME_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_TROPICAL_SLIME.get(),
                        10,
                        1,
                        1));
            }

            if (Config.hostileGnasherSpawn && biome.is(ModTags.Biomes.HOSTILE_GNASHER_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_GNASHER.get(),
                        3,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.HOSTILE_GNASHER.get(), 1.0D, 0.15D);
            }

            if (Config.hostileSnapperSpawn && biome.is(ModTags.Biomes.HOSTILE_SNAPPER_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_SNAPPER.get(),
                        3,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.HOSTILE_SNAPPER.get(), 1.0D, 0.15D);
            }

            if (Config.sunkenSkeletonSpawn && biome.is(ModTags.Biomes.SUNKEN_SKELETON_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.SUNKEN_SKELETON.get(),
                        3,
                        1,
                        2));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.SUNKEN_SKELETON.get(), 1.0D, 0.15D);
            }

            if (Config.hostileMiniGhastSpawn && biome.is(ModTags.Biomes.HOSTILE_MINI_GHAST_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_MINI_GHAST.get(),
                        1,
                        1,
                        1));
            }

            if (Config.hostileSpiderCreederSpawn && biome.is(ModTags.Biomes.HOSTILE_SPIDER_CREEDER_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_SPIDER_CREEDER.get(),
                        1,
                        1,
                        1));
            }

            if (Config.hostileTwilightGoatSpawn && biome.is(ModTags.Biomes.HOSTILE_TWILIGHT_GOAT_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(
                        ModEntityType.HOSTILE_TWILIGHT_GOAT.get(),
                        3,
                        1,
                        1));
            }

        }
    }

    public static boolean containsName(ResourceKey<Biome> biomeResourceKey, String string) {
        return biomeResourceKey.registry().getNamespace().contains(string);
    }
}