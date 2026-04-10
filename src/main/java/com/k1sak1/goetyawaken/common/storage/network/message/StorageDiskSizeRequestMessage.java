package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.api.IStorageDisk;
import com.k1sak1.goetyawaken.common.storage.api.IStorageDiskManager;
import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class StorageDiskSizeRequestMessage {
    private UUID diskId;

    public StorageDiskSizeRequestMessage(UUID diskId) {
        this.diskId = diskId;
    }

    public static void encode(StorageDiskSizeRequestMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.diskId);
    }

    public static StorageDiskSizeRequestMessage decode(FriendlyByteBuf buffer) {
        UUID diskId = buffer.readUUID();
        return new StorageDiskSizeRequestMessage(diskId);
    }

    public static void handle(StorageDiskSizeRequestMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.level() instanceof ServerLevel serverLevel) {
                IStorageDiskManager diskManager = StorageAPI.instance().getStorageDiskManager(serverLevel);
                IStorageDisk<ItemStack> disk = diskManager.get(message.diskId);
                if (disk != null) {
                    int stored = disk.getStored();
                    int capacity = disk.getCapacity();
                    ModNetwork.sendToClient(player,
                            new StorageDiskSizeResponseMessage(message.diskId, stored, capacity));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
