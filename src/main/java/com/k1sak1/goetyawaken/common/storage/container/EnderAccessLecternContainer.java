package com.k1sak1.goetyawaken.common.storage.container;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.storage.api.GridConstants;
import com.k1sak1.goetyawaken.common.storage.api.IStorageCache;
import com.k1sak1.goetyawaken.common.storage.api.IStorageCacheListener;
import com.k1sak1.goetyawaken.common.storage.container.slot.CraftingGridSlot;
import com.k1sak1.goetyawaken.common.storage.container.slot.ResultCraftingGridSlot;
import com.k1sak1.goetyawaken.common.storage.grid.IItemGridHandler;
import com.k1sak1.goetyawaken.common.storage.impl.ItemGridStorageCacheListener;
import com.k1sak1.goetyawaken.init.ModContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnderAccessLecternContainer extends AbstractContainerMenu {
    public static final int VISIBLE_ROWS = 5;
    private static final int TOP_HEIGHT = 19;
    private static final int BOTTOM_HEIGHT = 156;

    private final EnderAccessLecternBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final Player player;
    private final Inventory playerInventory;

    @Nullable
    private IStorageCache<ItemStack> storageCache;

    @Nullable
    private IStorageCacheListener<ItemStack> storageCacheListener;

    private Object view;
    private int searchBoxMode = 0;
    private int size = GridConstants.SIZE_MEDIUM;

    public EnderAccessLecternContainer(int containerId, Inventory playerInventory,
            EnderAccessLecternBlockEntity blockEntity) {
        super(ModContainerTypes.ENDER_ACCESS_LECTERN.get(), containerId);
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;
        this.playerInventory = playerInventory;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        if (playerInventory.player.level().isClientSide) {
            this.view = new com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl();
        }

        this.searchBoxMode = blockEntity.getSearchBoxMode();
        this.size = blockEntity.getSize();

        initSlots();

        if (!playerInventory.player.level().isClientSide) {
            blockEntity.onCraftingMatrixChanged();
        }
    }

    public EnderAccessLecternContainer(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, getBlockEntitySafe(playerInventory, data));
        if (data.isReadable()) {
            int sortDir = data.readInt();
            int sortType = data.readInt();
            int viewTypeVal = data.readInt();
            int searchBoxModeVal = data.isReadable() ? data.readInt() : 0;
            int sizeVal = data.isReadable() ? data.readInt() : GridConstants.SIZE_MEDIUM;
            if (!GridConstants.isValidSize(sizeVal)) {
                sizeVal = GridConstants.SIZE_MEDIUM;
            }
            if (this.view instanceof com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl impl) {
                impl.setSortingDirection(sortDir);
                impl.setSortingType(sortType);
                impl.setViewType(viewTypeVal);
            }
            blockEntity.setSortingDirection(sortDir);
            blockEntity.setSortingType(sortType);
            blockEntity.setViewType(viewTypeVal);
            blockEntity.setSize(sizeVal);
            this.searchBoxMode = searchBoxModeVal;
            this.size = sizeVal;
            initSlots();
        }
    }

    public void initSlots() {
        this.slots.clear();

        int headerAndSlots = TOP_HEIGHT + getVisibleRows() * 18;

        CraftingContainer matrix = blockEntity.getCraftingMatrix();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                int x = 26 + col * 18;
                int y = headerAndSlots + 4 + row * 18;
                addSlot(new CraftingGridSlot(matrix, index, x, y));
            }
        }

        ResultContainer result = blockEntity.getCraftingResult();
        addSlot(new ResultCraftingGridSlot(
                playerInventory.player, matrix, result, 0,
                134, headerAndSlots + 22, blockEntity));

        int playerInvY = headerAndSlots + 73;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = 9 + row * 9 + col;
                int x = 8 + col * 18;
                int y = playerInvY + row * 18;
                addSlot(new Slot(playerInventory, index, x, y));
            }
        }

        int hotbarY = playerInvY + 58;
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            addSlot(new Slot(playerInventory, col, x, hotbarY));
        }
    }

    private static EnderAccessLecternBlockEntity getBlockEntitySafe(Inventory playerInventory, FriendlyByteBuf data) {
        if (data == null) {
            throw new IllegalStateException("Cannot create container: FriendlyByteBuf data is null.");
        }

        BlockPos pos = data.readBlockPos();

        ResourceLocation dimLoc = null;
        if (data.isReadable()) {
            dimLoc = data.readResourceLocation();
        }

        if (playerInventory.player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            Level targetLevel = playerInventory.player.level();
            if (dimLoc != null) {
                ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimLoc);
                Level found = serverPlayer.getServer().getLevel(dimKey);
                if (found != null) {
                    targetLevel = found;
                }
            }
            if (targetLevel.getBlockEntity(pos) instanceof EnderAccessLecternBlockEntity blockEntity) {
                return blockEntity;
            }
            throw new IllegalStateException("Block entity not found at " + pos + " (server)");
        }

        Level clientLevel = playerInventory.player.level();
        if (clientLevel.getBlockEntity(pos) instanceof EnderAccessLecternBlockEntity blockEntity) {
            return blockEntity;
        }
        EnderAccessLecternBlockEntity dummy = new EnderAccessLecternBlockEntity(pos, clientLevel.getBlockState(pos));
        dummy.setLevel(clientLevel);
        return dummy;
    }

    @Override
    public void broadcastChanges() {
        if (!player.level().isClientSide) {
            IStorageCache<ItemStack> currentCache = blockEntity.getStorageCache();

            if (currentCache == null) {
                if (storageCacheListener != null) {
                    storageCache.removeListener(storageCacheListener);
                    storageCacheListener = null;
                    storageCache = null;
                }
            } else if (storageCache != currentCache) {

                if (storageCacheListener != null) {
                    storageCache.removeListener(storageCacheListener);
                    storageCacheListener = null;
                }
                storageCacheListener = new ItemGridStorageCacheListener(
                        (ServerPlayer) player, currentCache);
                storageCache = currentCache;
                storageCache.addListener(storageCacheListener);
            }

        }

        super.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide) {
            if (storageCache != null && storageCacheListener != null) {
                storageCache.removeListener(storageCacheListener);
            }
        }
    }

    public EnderAccessLecternBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Nullable
    public IItemGridHandler getItemGridHandler() {

        return blockEntity.getItemGridHandler();
    }

    @Nullable
    public Object getView() {
        return view;
    }

    public Player getPlayer() {
        return player;
    }

    public int getVisibleRows() {
        switch (size) {
            case GridConstants.SIZE_SMALL:
                return 3;
            case GridConstants.SIZE_LARGE:
                return 8;
            case GridConstants.SIZE_STRETCH:
            case GridConstants.SIZE_MEDIUM:
            default:
                return VISIBLE_ROWS;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (GridConstants.isValidSize(size)) {
            this.size = size;
        }
    }

    public int getTopHeight() {
        return TOP_HEIGHT;
    }

    public int getSearchBoxMode() {
        return searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();
        int craftingEnd = 9;
        int resultSlot = 9;
        int playerInvStart = 10;
        int playerInvEnd = 36;
        int hotbarStart = 37;
        int hotbarEnd = 45;

        if (index == resultSlot) {

            if (!player.level().isClientSide && player instanceof ServerPlayer) {
                blockEntity.onCraftedShift(player);
            }
            return ItemStack.EMPTY;
        } else if (index < craftingEnd) {

            if (!this.moveItemStackTo(slotStack, playerInvStart, hotbarEnd + 1, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= playerInvStart && index <= hotbarEnd) {
            if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                IItemGridHandler handler = blockEntity.getItemGridHandler();
                if (handler != null) {
                    ItemStack remainder = handler.onInsert(serverPlayer, slotStack.copy(), true);
                    if (remainder.getCount() != slotStack.getCount()) {
                        int inserted = slotStack.getCount() - remainder.getCount();
                        slotStack.shrink(inserted);
                        if (slotStack.isEmpty()) {
                            slot.set(ItemStack.EMPTY);
                        } else {
                            slot.setChanged();
                        }
                        return originalStack;
                    }
                }
            }

            if (player.level().isClientSide) {
                return ItemStack.EMPTY;
            }

            boolean movedWithinInventory = false;
            if (index < hotbarStart) {
                movedWithinInventory = this.moveItemStackTo(slotStack, hotbarStart, hotbarEnd + 1, false);
            } else {
                movedWithinInventory = this.moveItemStackTo(slotStack, playerInvStart, playerInvEnd + 1, false);
            }

            if (!movedWithinInventory && slotStack.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return slotStack.getCount() != originalStack.getCount() ? originalStack : ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
