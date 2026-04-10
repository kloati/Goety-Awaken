package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import net.minecraft.network.FriendlyByteBuf;
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
public class StorageDiskSizeResponseMessage {
    private final UUID diskId;
    private final int stored;
    private final int capacity;

    public StorageDiskSizeResponseMessage(UUID diskId, int stored, int capacity) {
        this.diskId = diskId;
        this.stored = stored;
        this.capacity = capacity;
    }

    public static void encode(StorageDiskSizeResponseMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.diskId);
        buffer.writeInt(message.stored);
        buffer.writeInt(message.capacity);
    }

    public static StorageDiskSizeResponseMessage decode(FriendlyByteBuf buffer) {
        UUID diskId = buffer.readUUID();
        int stored = buffer.readInt();
        int capacity = buffer.readInt();
        return new StorageDiskSizeResponseMessage(diskId, stored, capacity);
    }

    public static void handle(StorageDiskSizeResponseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            StorageAPI.instance().getStorageDiskSync().setData(message.diskId, message.stored, message.capacity);
        });
        context.setPacketHandled(true);
    }
}
