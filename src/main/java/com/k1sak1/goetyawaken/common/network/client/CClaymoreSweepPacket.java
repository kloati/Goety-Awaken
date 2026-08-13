package com.k1sak1.goetyawaken.common.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CClaymoreSweepPacket {
    private final int exemptTargetId;

    public CClaymoreSweepPacket() {
        this.exemptTargetId = -1;
    }

    public CClaymoreSweepPacket(int exemptTargetId) {
        this.exemptTargetId = exemptTargetId;
    }

    public static void encode(CClaymoreSweepPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.exemptTargetId);
    }

    public static CClaymoreSweepPacket decode(FriendlyByteBuf buffer) {
        int exemptTargetId = buffer.readInt();
        return new CClaymoreSweepPacket(exemptTargetId);
    }

    public static void consume(CClaymoreSweepPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.getAttackStrengthScale(0.5F) > 0.9F && playerEntity.onGround()) {
                    var stack = playerEntity.getMainHandItem();
                    var item = stack.getItem();

                    LivingEntity exemptTarget = null;
                    if (packet.exemptTargetId >= 0) {
                        Entity entity = playerEntity.level().getEntity(packet.exemptTargetId);
                        if (entity instanceof LivingEntity) {
                            exemptTarget = (LivingEntity) entity;
                        }
                    }

                    if (item instanceof com.k1sak1.goetyawaken.common.items.ClaymoreItem claymore) {
                        claymore.performFullSweepAttack(stack, playerEntity, exemptTarget);
                    } else if (item instanceof com.k1sak1.goetyawaken.common.items.ObsidianClaymoreItem obsidianClaymore) {
                        obsidianClaymore.performFullSweepAttack(stack, playerEntity, exemptTarget);
                    } else if (item instanceof com.k1sak1.goetyawaken.common.items.StarlessNightItem starlessNight) {
                        starlessNight.performFullSweepAttack(stack, playerEntity, exemptTarget);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
