package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServantAi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CWardenRoarPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(CWardenRoarPacket.class);

    public CWardenRoarPacket() {
    }

    public static void encode(CWardenRoarPacket packet, FriendlyByteBuf buf) {
    }

    public static CWardenRoarPacket decode(FriendlyByteBuf buf) {
        return new CWardenRoarPacket();
    }

    public static void handle(CWardenRoarPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ServerPlayer player = ctx.getSender();

        if (player != null) {
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof WardenServant wardenServant) {
                if (!wardenServant.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_COOLDOWN)) {
                    WardenServantAi.executeSonicBoomCrystallizationEffect(wardenServant, player);
                }
            }
        }

        ctx.setPacketHandled(true);
    }
}