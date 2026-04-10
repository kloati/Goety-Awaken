package com.k1sak1.goetyawaken.common.network.server;

import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantCapability;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantEventHandler;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SMobEnchantSyncPacket {

    private final int entityId;
    private final String enchantTypeName;
    private final int level;

    public SMobEnchantSyncPacket(int entityId, MobEnchantType enchantType, int level) {
        this.entityId = entityId;
        this.enchantTypeName = enchantType != null ? enchantType.getName() : "";
        this.level = level;
    }

    public static void encode(SMobEnchantSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
        buffer.writeUtf(packet.enchantTypeName);
        buffer.writeInt(packet.level);
    }

    public static SMobEnchantSyncPacket decode(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();
        String enchantTypeName = buffer.readUtf();
        int level = buffer.readInt();
        return new SMobEnchantSyncPacket(entityId, MobEnchantType.byName(enchantTypeName), level);
    }

    public static void handle(SMobEnchantSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.level != null) {
                    Entity entity = minecraft.level.getEntity(packet.entityId);
                    if (entity instanceof LivingEntity living) {
                        MobEnchantType enchantType = MobEnchantType.byName(packet.enchantTypeName);
                        if (enchantType != null) {
                            MobEnchantCapability cap = MobEnchantEventHandler.getCapability(living);
                            cap.setMobEnchantLevelClientOnly(enchantType, packet.level);
                            living.refreshDimensions();
                        }
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}
