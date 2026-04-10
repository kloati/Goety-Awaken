package com.k1sak1.goetyawaken.common.mobenchant;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobEnchantResurrectionManager {

    private static final Map<Level, Map<UUID, ResurrectionInfo>> RESURRECTION_QUEUE = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> RESURRECTION_COOLDOWN = new ConcurrentHashMap<>();
    private static final int RESURRECTION_COOLDOWN_TICKS = 500;
    private static final int AURA_RADIUS = 32;

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        LivingEntity dyingEntity = event.getEntity();
        Level level = dyingEntity.level();

        if (level.isClientSide) {
            return;
        }

        if (!isValidTargetForResurrection(dyingEntity)) {
            return;
        }
        if (killer instanceof LivingEntity killerLiving) {
            if (hasResurrectionAura(killerLiving) && isOwnedBy(killerLiving, dyingEntity)) {
                queueResurrection(dyingEntity, level);
                return;
            }
        }

        List<LivingEntity> auraEntities = level.getEntitiesOfClass(LivingEntity.class,
                dyingEntity.getBoundingBox().inflate(AURA_RADIUS),
                auraEntity -> auraEntity != dyingEntity && hasResurrectionAura(auraEntity));

        for (LivingEntity nearbyAuraEntity : auraEntities) {
            if (isOwnedBy(nearbyAuraEntity, dyingEntity)) {
                queueResurrection(dyingEntity, level);
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (!isValidTargetForResurrection(livingEntity)) {
            return;
        }

        Vec3 entityPos = livingEntity.position();

        AABB searchBox = new AABB(
                entityPos.x - AURA_RADIUS, entityPos.y - AURA_RADIUS, entityPos.z - AURA_RADIUS,
                entityPos.x + AURA_RADIUS, entityPos.y + AURA_RADIUS, entityPos.z + AURA_RADIUS);

        List<LivingEntity> auraEntities = level.getEntitiesOfClass(LivingEntity.class,
                searchBox,
                auraEntity -> auraEntity != livingEntity && hasResurrectionAura(auraEntity));

        for (LivingEntity nearbyAuraEntity : auraEntities) {
            if (isOwnedBy(nearbyAuraEntity, livingEntity)) {
                queueResurrection(livingEntity, level);
                break;
            }
        }
    }

    private static boolean isOwnedBy(LivingEntity owner, LivingEntity target) {
        if (target instanceof IOwned owned) {
            UUID ownerId = owned.getOwnerId();
            return ownerId != null && ownerId.equals(owner.getUUID());
        }
        return false;
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID entityUUID = entity.getUUID();
        Map<UUID, ResurrectionInfo> queue = RESURRECTION_QUEUE.computeIfAbsent(level, k -> new ConcurrentHashMap<>());

        if (queue.containsKey(entityUUID)) {
            ResurrectionInfo info = queue.remove(entityUUID);
            if (info.getTrueOwnerUUID() != null && entity instanceof IOwned owned) {
                owned.setOwnerId(info.getTrueOwnerUUID());
                owned.setOwnerClientId(info.getTrueOwnerClientId());
            }
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel,
                    net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, entity);
        }
    }

    private static void queueResurrection(Entity entity, Level level) {
        UUID entityUUID = entity.getUUID();
        Long lastRespawnTime = RESURRECTION_COOLDOWN.get(entityUUID);
        if (lastRespawnTime != null) {
            long currentTime = level.getGameTime();
            if (currentTime - lastRespawnTime < RESURRECTION_COOLDOWN_TICKS) {
                return;
            }
            RESURRECTION_COOLDOWN.remove(entityUUID);
        }
        Map<UUID, ResurrectionInfo> queue = RESURRECTION_QUEUE.computeIfAbsent(level, k -> new ConcurrentHashMap<>());
        if (queue.containsKey(entityUUID)) {
            return;
        }
        ResurrectionInfo info = new ResurrectionInfo(entity, ResurrectionInfo.RESURRECTION_TICKS, level.getGameTime());
        queue.put(entityUUID, info);
    }

    private static boolean hasResurrectionAura(LivingEntity entity) {
        MobEnchantCapability capability = MobEnchantEventHandler.getCapability(entity);
        if (capability != null) {
            return capability.hasMobEnchantment(MobEnchantType.RESURRECTION_AURA);
        }
        return false;
    }

    private static boolean isValidTargetForResurrection(LivingEntity entity) {
        return entity instanceof IOwned;
    }

    public static void spawnResurrectionParticles(ServerLevel serverLevel, Vec3 position) {
        ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
        ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.RISING_ENCHANT.get(),
                position.x, position.y, position.z, colorUtil.red, colorUtil.green,
                colorUtil.blue, 1.0F);
    }

    public static void processResurrection(ServerLevel serverLevel) {
        Map<UUID, ResurrectionInfo> queue = RESURRECTION_QUEUE.get(serverLevel);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        long currentGameTime = serverLevel.getGameTime();
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, ResurrectionInfo> entry : queue.entrySet()) {
            ResurrectionInfo info = entry.getValue();
            int remainingTicks = info.getRemainingTicks(currentGameTime);

            if (remainingTicks <= 0) {
                if (respawnEntity(serverLevel, info)) {
                    toRemove.add(entry.getKey());
                }
            } else {
                if (remainingTicks % 2 == 0) {
                    spawnResurrectionParticles(serverLevel, info.getDeathPosition());
                }
            }
        }
        for (UUID uuid : toRemove) {
            queue.remove(uuid);
        }
    }

    private static boolean respawnEntity(ServerLevel serverLevel, ResurrectionInfo info) {
        CompoundTag entityData = info.getEntityData();
        String entityId = info.getEntityTypeInfo().getEntityId();

        try {
            var entityType = ForgeRegistries.ENTITY_TYPES.getValue(
                    net.minecraft.resources.ResourceLocation.tryParse(entityId));

            if (entityType == null) {
                return false;
            }

            Entity entity = entityType.create(serverLevel);
            if (entity == null) {
                return false;
            }

            CompoundTag loadTag = entityData.copy();
            loadTag.remove("Health");
            loadTag.remove("deathTime");
            loadTag.remove("DeathTime");

            entity.load(loadTag);

            if (entity instanceof LivingEntity living && living instanceof IAncientGlint glint) {
                glint.setAncientGlint(info.hasAncientGlint());
                glint.setGlintTextureType(info.getGlintTextureType());
            }

            if (entity instanceof IOwned owned && info.getTrueOwnerUUID() != null) {
                owned.setOwnerId(info.getTrueOwnerUUID());
                if (info.getTrueOwnerClientId() > -1) {
                    owned.setOwnerClientId(info.getTrueOwnerClientId());
                }
            }

            Vec3 pos = info.getDeathPosition();
            entity.moveTo(pos.x, pos.y, pos.z, entity.getYRot(), entity.getXRot());

            if (entity instanceof LivingEntity living) {
                float maxHealth = living.getMaxHealth();
                living.setHealth(maxHealth);
                living.deathTime = 0;
            }
            entity.invulnerableTime = 40;
            serverLevel.addFreshEntity(entity);
            RESURRECTION_COOLDOWN.put(info.getOriginalUUID(), serverLevel.getGameTime());

            ColorUtil Color = new ColorUtil(0xFFFFFF);
            ServerParticleUtil.summonUndeadParticles(serverLevel, entity, Color, 0xFFFFFF,
                    0xFFFFFF);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
