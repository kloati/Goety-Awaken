package com.k1sak1.goetyawaken.common.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CClaymoreSweepPacket {
    public static void encode(CClaymoreSweepPacket packet, FriendlyByteBuf buffer) {
    }

    public static CClaymoreSweepPacket decode(FriendlyByteBuf buffer) {
        return new CClaymoreSweepPacket();
    }

    public static void consume(CClaymoreSweepPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.getAttackStrengthScale(0.5F) > 0.9F && playerEntity.onGround()) {
                    var stack = playerEntity.getMainHandItem();
                    var item = stack.getItem();
                    if (item instanceof com.k1sak1.goetyawaken.common.items.ClaymoreItem claymore) {
                        claymore.performFullSweepAttack(stack, playerEntity);
                    } else if (item instanceof com.k1sak1.goetyawaken.common.items.ObsidianClaymoreItem obsidianClaymore) {
                        obsidianClaymore.performFullSweepAttack(stack, playerEntity);
                    } else if (item instanceof com.k1sak1.goetyawaken.common.items.StarlessNightItem starlessNight) {
                        starlessNight.performFullSweepAttack(stack, playerEntity);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
