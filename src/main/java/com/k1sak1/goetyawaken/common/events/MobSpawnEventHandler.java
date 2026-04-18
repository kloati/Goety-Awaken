package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileWildfire;
import com.k1sak1.goetyawaken.common.entities.hostile.IceCreeper;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.Parched;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.BoulderingZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.FrozenZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.JungleZombie;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;
import java.util.List;

@Mod.EventBusSubscriber
public class MobSpawnEventHandler {
    private static final Random RANDOM = new Random();
    private static final int CHECK_RADIUS = 32;

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof Creeper creeper &&
                creeper.getSpawnType() == MobSpawnType.NATURAL &&
                event.getLevel() instanceof ServerLevel serverLevel &&
                serverLevel.dimension() == net.minecraft.world.level.Level.OVERWORLD) {

            BlockPos pos = entity.blockPosition();
            if (isColdBiome(serverLevel, pos)) {
                if (canReplaceEntity(serverLevel, pos, ModEntityType.ICE_CREEPER.get(), creeper)) {
                    if (RANDOM.nextFloat() < 0.5f) {
                        IceCreeper iceCreeper = ModEntityType.ICE_CREEPER.get().create(serverLevel);
                        if (iceCreeper != null) {
                            iceCreeper.moveTo(
                                    creeper.getX(),
                                    creeper.getY(),
                                    creeper.getZ(),
                                    creeper.getYRot(),
                                    creeper.getXRot());
                            iceCreeper.finalizeSpawn(
                                    serverLevel,
                                    serverLevel.getCurrentDifficultyAt(pos),
                                    MobSpawnType.NATURAL,
                                    null,
                                    null);
                            event.setCanceled(true);
                            creeper.discard();
                            serverLevel.addFreshEntity(iceCreeper);
                        }
                    }
                }
            }
        }

        if (entity instanceof Zombie zombie &&
                zombie.getSpawnType() == MobSpawnType.NATURAL &&
                event.getLevel() instanceof ServerLevel serverLevel) {

            BlockPos pos = entity.blockPosition();
            if (isMountainBiome(serverLevel, pos)) {
                if (canReplaceEntity(serverLevel, pos, ModEntityType.BOULDERING_ZOMBIE.get(), zombie)) {
                    if (RANDOM.nextFloat() < 0.25f) {
                        BoulderingZombie boulderingZombie = ModEntityType.BOULDERING_ZOMBIE.get().create(serverLevel);
                        if (boulderingZombie != null) {
                            boulderingZombie.moveTo(
                                    zombie.getX(),
                                    zombie.getY(),
                                    zombie.getZ(),
                                    zombie.getYRot(),
                                    zombie.getXRot());
                            boulderingZombie.finalizeSpawn(
                                    serverLevel,
                                    serverLevel.getCurrentDifficultyAt(pos),
                                    MobSpawnType.NATURAL,
                                    null,
                                    null);
                            event.setCanceled(true);
                            zombie.discard();
                            serverLevel.addFreshEntity(boulderingZombie);
                        }
                    }
                }
            }

            if (isJungleBiome(serverLevel, pos)) {
                if (canReplaceEntity(serverLevel, pos, ModEntityType.JUNGLE_ZOMBIE.get(), zombie)) {
                    if (RANDOM.nextFloat() < 0.5f) {
                        JungleZombie jungleZombie = ModEntityType.JUNGLE_ZOMBIE.get().create(serverLevel);
                        if (jungleZombie != null) {
                            jungleZombie.moveTo(
                                    zombie.getX(),
                                    zombie.getY(),
                                    zombie.getZ(),
                                    zombie.getYRot(),
                                    zombie.getXRot());
                            jungleZombie.finalizeSpawn(
                                    serverLevel,
                                    serverLevel.getCurrentDifficultyAt(pos),
                                    MobSpawnType.NATURAL,
                                    null,
                                    null);
                            event.setCanceled(true);
                            zombie.discard();
                            serverLevel.addFreshEntity(jungleZombie);
                        }
                    }
                }
            }
            if (isColdBiome(serverLevel, pos)) {
                if (canReplaceEntity(serverLevel, pos, ModEntityType.FROZEN_ZOMBIE.get(), zombie)) {
                    if (RANDOM.nextFloat() < 0.5f) {
                        FrozenZombie frozenZombie = ModEntityType.FROZEN_ZOMBIE.get().create(serverLevel);
                        if (frozenZombie != null) {
                            frozenZombie.moveTo(
                                    zombie.getX(),
                                    zombie.getY(),
                                    zombie.getZ(),
                                    zombie.getYRot(),
                                    zombie.getXRot());
                            frozenZombie.finalizeSpawn(
                                    serverLevel,
                                    serverLevel.getCurrentDifficultyAt(pos),
                                    MobSpawnType.NATURAL,
                                    null,
                                    null);
                            event.setCanceled(true);
                            zombie.discard();
                            serverLevel.addFreshEntity(frozenZombie);
                        }
                    }
                }
            }
        }

        if (entity instanceof Skeleton skeleton &&
                skeleton.getSpawnType() == MobSpawnType.NATURAL &&
                event.getLevel() instanceof ServerLevel serverLevel) {

            BlockPos pos = entity.blockPosition();
            if (isDesertBiome(serverLevel, pos) && serverLevel.canSeeSky(pos)) {
                if (canReplaceEntity(serverLevel, pos, ModEntityType.PARCHED.get(), skeleton)) {
                    if (RANDOM.nextFloat() < 0.25f) {
                        Parched parched = ModEntityType.PARCHED.get().create(serverLevel);
                        if (parched != null) {
                            parched.moveTo(
                                    skeleton.getX(),
                                    skeleton.getY(),
                                    skeleton.getZ(),
                                    skeleton.getYRot(),
                                    skeleton.getXRot());
                            parched.finalizeSpawn(
                                    serverLevel,
                                    serverLevel.getCurrentDifficultyAt(pos),
                                    MobSpawnType.NATURAL,
                                    null,
                                    null);
                            event.setCanceled(true);
                            skeleton.discard();
                            serverLevel.addFreshEntity(parched);
                        }
                    }
                }
            }
        }

        if (entity instanceof HostileWildfire hostileWildfire &&
                hostileWildfire.getSpawnType() == MobSpawnType.NATURAL &&
                event.getLevel() instanceof ServerLevel serverLevel) {

            if (RANDOM.nextFloat() < 0.95f) {
                event.setCanceled(true);
                hostileWildfire.discard();
            }
        }

    }

    private static boolean canReplaceEntity(ServerLevel serverLevel, BlockPos pos, EntityType<?> newEntityType,
            Mob originalEntity) {
        int count = countNearbyEntities(serverLevel, pos, newEntityType, CHECK_RADIUS);
        int maxCount = 6;
        if (count >= maxCount) {
            return false;
        }

        int totalMonsterCount = countNearbyEntitiesByCategory(serverLevel, pos, MobCategory.MONSTER, CHECK_RADIUS);
        int maxMonsterCount = 70;
        if (totalMonsterCount >= maxMonsterCount) {
            return false;
        }
        return true;
    }

    private static int countNearbyEntities(ServerLevel serverLevel, BlockPos pos, EntityType<?> entityType,
            int radius) {
        AABB box = new AABB(pos).inflate(radius);
        List<? extends Entity> entities = serverLevel.getEntitiesOfClass(Mob.class, box,
                mob -> mob.getType() == entityType);
        return entities.size();
    }

    private static int countNearbyEntitiesByCategory(ServerLevel serverLevel, BlockPos pos, MobCategory category,
            int radius) {
        AABB box = new AABB(pos).inflate(radius);
        int count = 0;
        for (Player player : serverLevel.players()) {
            List<? extends Mob> mobs = serverLevel.getEntitiesOfClass(Mob.class, box,
                    mob -> mob.getType().getCategory() == category && !mob.isPersistenceRequired()
                            && !mob.requiresCustomPersistence());
            count += mobs.size();
        }
        return count / Math.max(1, serverLevel.players().size());
    }

    private static boolean isColdBiome(ServerLevel world, BlockPos pos) {
        return world.getBiome(pos).is(Tags.Biomes.IS_COLD);
    }

    private static boolean isMountainBiome(ServerLevel world, BlockPos pos) {
        return world.getBiome(pos).is(Tags.Biomes.IS_MOUNTAIN);
    }

    private static boolean isJungleBiome(ServerLevel world, BlockPos pos) {
        ResourceKey<net.minecraft.world.level.biome.Biome> biomeKey = world.getBiome(pos).unwrapKey().orElse(null);
        return biomeKey == Biomes.JUNGLE || biomeKey == Biomes.SPARSE_JUNGLE || biomeKey == Biomes.BAMBOO_JUNGLE;
    }

    private static boolean isDesertBiome(ServerLevel world, BlockPos pos) {
        return world.getBiome(pos).is(Tags.Biomes.IS_DESERT);
    }
}