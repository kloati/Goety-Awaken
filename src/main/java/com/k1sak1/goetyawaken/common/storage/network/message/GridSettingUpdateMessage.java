package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.items.magic.AccessFocus;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

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
    public static final int SETTING_SIZE = 4;

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
                if (!isValidSetting(message.settingType, message.value)) {
                    return;
                }

                ItemStack focusStack = AccessFocus.findAccessFocus(player);

                if (focusStack != null) {
                    switch (message.settingType) {
                        case SETTING_SORTING_DIRECTION -> {
                            AccessFocus.setSortingDirection(focusStack, message.value);
                            container.getBlockEntity().setSortingDirection(message.value);
                        }
                        case SETTING_SORTING_TYPE -> {
                            AccessFocus.setSortingType(focusStack, message.value);
                            container.getBlockEntity().setSortingType(message.value);
                        }
                        case SETTING_VIEW_TYPE -> {
                            AccessFocus.setViewType(focusStack, message.value);
                            container.getBlockEntity().setViewType(message.value);
                        }
                        case SETTING_SEARCH_BOX_MODE -> {
                            AccessFocus.setSearchBoxMode(focusStack, message.value);
                            container.getBlockEntity().setSearchBoxMode(message.value);
                        }
                        case SETTING_SIZE -> {
                            AccessFocus.setSize(focusStack, message.value);
                            container.getBlockEntity().setSize(message.value);
                            container.setSize(message.value);
                            reopenLectern(player, container.getBlockEntity());
                        }
                    }
                } else {
                    EnderAccessLecternBlockEntity be = container.getBlockEntity();
                    if (be != null) {
                        switch (message.settingType) {
                            case SETTING_SORTING_DIRECTION -> be.setSortingDirection(message.value);
                            case SETTING_SORTING_TYPE -> be.setSortingType(message.value);
                            case SETTING_VIEW_TYPE -> be.setViewType(message.value);
                            case SETTING_SEARCH_BOX_MODE -> be.setSearchBoxMode(message.value);
                            case SETTING_SIZE -> {
                                be.setSize(message.value);
                                container.setSize(message.value);
                                reopenLectern(player, be);
                            }
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    private static boolean isValidSetting(int settingType, int value) {
        return switch (settingType) {
            case SETTING_SORTING_DIRECTION ->
                value == com.k1sak1.goetyawaken.common.storage.api.GridConstants.SORTING_DIRECTION_ASCENDING
                        || value == com.k1sak1.goetyawaken.common.storage.api.GridConstants.SORTING_DIRECTION_DESCENDING;
            case SETTING_SORTING_TYPE ->
                value == com.k1sak1.goetyawaken.common.storage.api.GridConstants.SORTING_TYPE_NAME
                        || value == com.k1sak1.goetyawaken.common.storage.api.GridConstants.SORTING_TYPE_QUANTITY;
            case SETTING_VIEW_TYPE -> com.k1sak1.goetyawaken.common.storage.api.GridConstants.isValidViewType(value);
            case SETTING_SEARCH_BOX_MODE -> value >= 0 && value <= 5;
            case SETTING_SIZE -> com.k1sak1.goetyawaken.common.storage.api.GridConstants.isValidSize(value);
            default -> false;
        };
    }

    private static void reopenLectern(ServerPlayer player, EnderAccessLecternBlockEntity be) {
        if (be == null) {
            return;
        }
        Level level = be.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = be.getBlockPos();
        NetworkHooks.openScreen(player, be, buf -> {
            buf.writeBlockPos(pos);
            buf.writeResourceLocation(level.dimension().location());
            buf.writeInt(be.getSortingDirection());
            buf.writeInt(be.getSortingType());
            buf.writeInt(be.getViewType());
            buf.writeInt(be.getSearchBoxMode());
            buf.writeInt(be.getSize());
        });
    }
}
