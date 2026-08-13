package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SApostleProgressSyncPacket {
    private final int entityId;
    private final String entityName;
    private final CompoundTag dataTag;
    private final double maxHealth;
    private final double[] targets;

    public SApostleProgressSyncPacket(LivingEntity entity) {
        this.entityId = entity.getId();
        this.entityName = entity.getName().getString();
        this.dataTag = ApostleUpgradeManager.getUpgradeData(entity).saveNBT();
        this.maxHealth = entity.getAttributeValue(Attributes.MAX_HEALTH);
        this.targets = new double[]{
                maxHealth * Config.UPGRADE_HEAL_MULTIPLIER.get(),
                maxHealth * Config.UPGRADE_DAMAGE_MULTIPLIER.get(),
                Config.UPGRADE_BLIGHT_KILLS.get(),
                Config.UPGRADE_WITHER_DAMAGE.get(),
                Config.UPGRADE_WITHER_KILLS.get(),
                Config.UPGRADE_WARDEN_DAMAGE.get(),
                Config.UPGRADE_WARDEN_KILLS.get(),
                Config.UPGRADE_POSITIVE_EFFECTS.get(),
                Config.UPGRADE_NEGATIVE_EFFECTS.get(),
                Config.UPGRADE_BLAZE_KILLS.get(),
                Config.UPGRADE_TRADING_EMERALDS.get(),
                maxHealth * Config.UPGRADE_FROZEN_DAMAGE_MULTIPLIER.get(),
                Config.UPGRADE_SWIFT_TICKS.get(),
                Config.UPGRADE_FOLLOWER_COUNT.get(),
                Config.UPGRADE_VILLAGER_KILLS.get()
        };
    }

    private SApostleProgressSyncPacket(int entityId, String entityName, CompoundTag dataTag, double maxHealth,
            double[] targets) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.dataTag = dataTag;
        this.maxHealth = maxHealth;
        this.targets = targets;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public CompoundTag getDataTag() {
        return this.dataTag;
    }

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public double[] getTargets() {
        return this.targets;
    }

    public static void encode(SApostleProgressSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
        buffer.writeUtf(packet.entityName);
        buffer.writeNbt(packet.dataTag);
        buffer.writeDouble(packet.maxHealth);
        buffer.writeInt(packet.targets.length);
        for (double target : packet.targets) {
            buffer.writeDouble(target);
        }
    }

    public static SApostleProgressSyncPacket decode(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();
        String entityName = buffer.readUtf();
        CompoundTag dataTag = buffer.readNbt();
        double maxHealth = buffer.readDouble();
        int size = buffer.readInt();
        double[] targets = new double[size];
        for (int i = 0; i < size; i++) {
            targets[i] = buffer.readDouble();
        }
        return new SApostleProgressSyncPacket(entityId, entityName, dataTag, maxHealth, targets);
    }

    public static void handle(SApostleProgressSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (packet.dataTag == null) {
                return;
            }
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.k1sak1.goetyawaken.client.ClientScreenHelper.openApostleProgressScreen(packet));
        });
        context.setPacketHandled(true);
    }
}
