package com.k1sak1.goetyawaken.common.network;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.SNamelessOneQuotePacket;
import com.k1sak1.goetyawaken.common.network.server.SMobEnchantSyncPacket;
import com.k1sak1.goetyawaken.common.network.server.SOpenAccessFocusMessage;
import com.k1sak1.goetyawaken.common.network.client.CWardenRoarPacket;
import com.k1sak1.goetyawaken.common.network.client.CAutoRideablePacket;
import com.k1sak1.goetyawaken.common.network.client.CCombatHealthSyncPacket;
import com.k1sak1.goetyawaken.common.network.server.SBossBarPacket;
import com.k1sak1.goetyawaken.common.network.client.CWitherFlightPacket;
import com.k1sak1.goetyawaken.common.network.client.CWitherRoarPacket;
import com.k1sak1.goetyawaken.common.network.client.CFrostScytheStrikePacket;
import com.k1sak1.goetyawaken.common.network.client.CStarlessNightSlashPacket;
// import com.k1sak1.goetyawaken.common.network.client.CBlueMoonSlashPacket;
import com.k1sak1.goetyawaken.common.network.client.CClaymoreSweepPacket;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemExtractMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemInsertMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemUpdateMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemDeltaMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemPullMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemInsertHeldMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridClearMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemScrollMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridSettingUpdateMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridTransferMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.StorageDiskSizeRequestMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.StorageDiskSizeResponseMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
        public static SimpleChannel channel;
        private static int id = 0;

        public static int nextID() {
                return id++;
        }

        public static void init() {
                channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(GoetyAwaken.MODID, "channel"),
                                () -> "1.0",
                                s -> true, s -> true);

                channel.registerMessage(nextID(), CWardenRoarPacket.class, CWardenRoarPacket::encode,
                                CWardenRoarPacket::decode,
                                CWardenRoarPacket::handle);
                channel.registerMessage(nextID(), CAutoRideablePacket.class, CAutoRideablePacket::encode,
                                CAutoRideablePacket::decode,
                                CAutoRideablePacket::handle);
                channel.registerMessage(nextID(), SBossBarPacket.class, SBossBarPacket::encode,
                                SBossBarPacket::decode,
                                SBossBarPacket::consume);
                channel.registerMessage(nextID(), CWitherFlightPacket.class, CWitherFlightPacket::encode,
                                CWitherFlightPacket::decode,
                                CWitherFlightPacket::handle);
                channel.registerMessage(nextID(), CWitherRoarPacket.class, CWitherRoarPacket::encode,
                                CWitherRoarPacket::decode,
                                CWitherRoarPacket::handle);
                channel.registerMessage(nextID(), CFrostScytheStrikePacket.class, CFrostScytheStrikePacket::encode,
                                CFrostScytheStrikePacket::decode,
                                CFrostScytheStrikePacket::consume);
                channel.registerMessage(nextID(), CStarlessNightSlashPacket.class, CStarlessNightSlashPacket::encode,
                                CStarlessNightSlashPacket::decode,
                                CStarlessNightSlashPacket::consume);
                // channel.registerMessage(nextID(), CBlueMoonSlashPacket.class,
                // CBlueMoonSlashPacket::encode,
                // CBlueMoonSlashPacket::decode,
                // CBlueMoonSlashPacket::consume);
                channel.registerMessage(nextID(), CClaymoreSweepPacket.class, CClaymoreSweepPacket::encode,
                                CClaymoreSweepPacket::decode,
                                CClaymoreSweepPacket::consume);

                channel.registerMessage(nextID(), CCombatHealthSyncPacket.class, CCombatHealthSyncPacket::encode,
                                CCombatHealthSyncPacket::decode,
                                CCombatHealthSyncPacket::handle,
                                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));

                channel.registerMessage(nextID(), GridItemExtractMessage.class, GridItemExtractMessage::encode,
                                GridItemExtractMessage::decode,
                                GridItemExtractMessage::handle);
                channel.registerMessage(nextID(), GridItemInsertMessage.class, GridItemInsertMessage::encode,
                                GridItemInsertMessage::decode,
                                GridItemInsertMessage::handle);
                channel.registerMessage(nextID(), StorageDiskSizeRequestMessage.class,
                                StorageDiskSizeRequestMessage::encode,
                                StorageDiskSizeRequestMessage::decode,
                                StorageDiskSizeRequestMessage::handle);

                channel.registerMessage(nextID(), StorageDiskSizeResponseMessage.class,
                                StorageDiskSizeResponseMessage::encode,
                                StorageDiskSizeResponseMessage::decode,
                                StorageDiskSizeResponseMessage::handle,
                                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));

                channel.registerMessage(nextID(), GridItemUpdateMessage.class, GridItemUpdateMessage::encode,
                                GridItemUpdateMessage::decode,
                                GridItemUpdateMessage::handle);
                channel.registerMessage(nextID(), GridItemDeltaMessage.class, GridItemDeltaMessage::encode,
                                GridItemDeltaMessage::decode,
                                GridItemDeltaMessage::handle);

                channel.registerMessage(nextID(), GridItemPullMessage.class, GridItemPullMessage::encode,
                                GridItemPullMessage::decode,
                                GridItemPullMessage::handle);
                channel.registerMessage(nextID(), GridItemInsertHeldMessage.class, GridItemInsertHeldMessage::encode,
                                GridItemInsertHeldMessage::decode,
                                GridItemInsertHeldMessage::handle);
                channel.registerMessage(nextID(), GridClearMessage.class, GridClearMessage::encode,
                                GridClearMessage::decode,
                                GridClearMessage::handle);
                channel.registerMessage(nextID(), GridItemScrollMessage.class, GridItemScrollMessage::encode,
                                GridItemScrollMessage::decode,
                                GridItemScrollMessage::handle);

                channel.registerMessage(nextID(), GridSettingUpdateMessage.class,
                                GridSettingUpdateMessage::encode,
                                GridSettingUpdateMessage::decode,
                                GridSettingUpdateMessage::handle);
                channel.registerMessage(nextID(), GridTransferMessage.class,
                                GridTransferMessage::encode,
                                GridTransferMessage::decode,
                                GridTransferMessage::handle);

                channel.registerMessage(nextID(), SNamelessOneQuotePacket.class, SNamelessOneQuotePacket::encode,
                                SNamelessOneQuotePacket::decode,
                                SNamelessOneQuotePacket::handle);

                channel.registerMessage(nextID(), SMobEnchantSyncPacket.class, SMobEnchantSyncPacket::encode,
                                SMobEnchantSyncPacket::decode,
                                SMobEnchantSyncPacket::handle);

                channel.registerMessage(nextID(), SOpenAccessFocusMessage.class,
                                SOpenAccessFocusMessage::encode,
                                SOpenAccessFocusMessage::decode,
                                SOpenAccessFocusMessage::handle);
        }

        public static <MSG> void sendTo(Player player, MSG msg) {
                channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), msg);
        }

        public static <MSG> void sendToServer(MSG msg) {
                channel.sendToServer(msg);
        }

        public static <MSG> void sentToTrackingChunk(LevelChunk chunk, MSG msg) {
                channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
        }

        public static <MSG> void sentToTrackingEntity(Entity entity, MSG msg) {
                channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
        }

        public static <MSG> void sentToTrackingEntityAndPlayer(Entity entity, MSG msg) {
                channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
        }

        public static <MSG> void sendToALL(MSG msg) {
                channel.send(PacketDistributor.ALL.noArg(), msg);
        }

        public static <MSG> void sendToClient(ServerPlayer player, MSG msg) {
                channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
}