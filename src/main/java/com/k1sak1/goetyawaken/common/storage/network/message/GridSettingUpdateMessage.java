package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class GridSettingUpdateMessage {
    public static final int SETTING_SORTING_DIRECTION = 0;
    public static final int SETTING_SORTING_TYPE = 1;
    public static final int SETTING_VIEW_TYPE = 2;
    public static final int SETTING_SEARCH_BOX_MODE = 3;

    private final int settingType;
    private final int value;

    public GridSettingUpdateMessage(int settingType, int value) {
        this.settingType = settingType;
        this.value = value;
    }

    public static void encode(GridSettingUpdateMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.settingType);
        buffer.writeInt(message.value);
    }

    public static GridSettingUpdateMessage decode(FriendlyByteBuf buffer) {
        return new GridSettingUpdateMessage(buffer.readInt(), buffer.readInt());
    }

    public static void handle(GridSettingUpdateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof EnderAccessLecternContainer container) {
                EnderAccessLecternBlockEntity be = container.getBlockEntity();
                if (be != null) {
                    switch (message.settingType) {
                        case SETTING_SORTING_DIRECTION -> be.setSortingDirection(message.value);
                        case SETTING_SORTING_TYPE -> be.setSortingType(message.value);
                        case SETTING_VIEW_TYPE -> be.setViewType(message.value);
                        case SETTING_SEARCH_BOX_MODE -> be.setSearchBoxMode(message.value);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
