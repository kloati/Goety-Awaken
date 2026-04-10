package com.k1sak1.goetyawaken.common.mobenchant;

import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class ResurrectionInfo {
    public static final int RESURRECTION_TICKS = 60;

    private final Vec3 deathPosition;
    private final CompoundTag entityData;
    private final Level level;
    private final UUID originalUUID;
    @Nullable
    private final UUID trueOwnerUUID;
    private final int trueOwnerClientId;
    private final boolean hasAncientGlint;
    private final String glintTextureType;
    private final long deathGameTime;
    private final EntityTypeInfo entityTypeInfo;

    public ResurrectionInfo(Entity entity, int resurrectionTicks, long currentGameTime) {
        this.deathPosition = entity.position();
        this.level = entity.level();
        this.deathGameTime = currentGameTime;
        this.originalUUID = entity.getUUID();
        this.hasAncientGlint = (entity instanceof LivingEntity living && living instanceof IAncientGlint glint)
                ? glint.hasAncientGlint()
                : false;
        this.glintTextureType = (entity instanceof LivingEntity living && living instanceof IAncientGlint glint)
                ? glint.getGlintTextureType()
                : "ancient";

        CompoundTag nbt = new CompoundTag();
        entity.saveWithoutId(nbt);
        nbt.remove("UUID");
        nbt.remove("UUIDMost");
        nbt.remove("UUIDLeast");
        nbt.remove("RootVehicle");
        nbt.remove("Leash");
        nbt.remove("Passengers");
        nbt.remove("DeathTime");
        nbt.remove("deathTime");
        nbt.remove("dead");
        nbt.remove("Motion");
        nbt.remove("FallDistance");
        nbt.remove("OnGround");

        if (nbt.contains("Fuse")) {
        }
        nbt.putShort("Fuse", nbt.contains("Fuse") ? nbt.getShort("Fuse") : (short) 30);
        nbt.putBoolean("ignited", false);
        this.entityData = nbt;

        if (entity instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            this.trueOwnerUUID = owned.getOwnerId();
            this.trueOwnerClientId = owned.getOwnerClientId();
        } else {
            this.trueOwnerUUID = null;
            this.trueOwnerClientId = -1;
        }

        this.entityTypeInfo = new EntityTypeInfo(entity.getType());
    }

    public UUID getOriginalUUID() {
        return originalUUID;
    }

    public Vec3 getDeathPosition() {
        return deathPosition;
    }

    public CompoundTag getEntityData() {
        return entityData;
    }

    public Level getLevel() {
        return level;
    }

    @Nullable
    public UUID getTrueOwnerUUID() {
        return trueOwnerUUID;
    }

    public int getTrueOwnerClientId() {
        return trueOwnerClientId;
    }

    public boolean hasAncientGlint() {
        return hasAncientGlint;
    }

    public String getGlintTextureType() {
        return glintTextureType;
    }

    public int getRemainingTicks(long currentGameTime) {
        return (int) Math.max(0, RESURRECTION_TICKS - (currentGameTime - this.deathGameTime));
    }

    public long getDeathGameTime() {
        return deathGameTime;
    }

    public EntityTypeInfo getEntityTypeInfo() {
        return entityTypeInfo;
    }

    public static class EntityTypeInfo {
        private final String entityId;

        public EntityTypeInfo(net.minecraft.world.entity.EntityType<?> type) {
            this.entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        }

        public String getEntityId() {
            return entityId;
        }
    }
}
