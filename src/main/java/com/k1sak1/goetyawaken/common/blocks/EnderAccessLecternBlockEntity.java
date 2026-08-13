package com.k1sak1.goetyawaken.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.k1sak1.goetyawaken.common.storage.api.*;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import com.k1sak1.goetyawaken.common.storage.grid.IItemGridHandler;
import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import com.k1sak1.goetyawaken.common.storage.network.INetwork;
import com.k1sak1.goetyawaken.common.storage.network.impl.EnderNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnderAccessLecternBlockEntity extends BlockEntity implements MenuProvider {
    private static final int CRAFTING_GRID_SIZE = 9;

    private final AbstractContainerMenu dummyMenu = new AbstractContainerMenu(null, -1) {
        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void slotsChanged(net.minecraft.world.Container container) {
            onCraftingMatrixChanged();
            setChanged();
        }
    };

    private final CraftingContainer craftingMatrix = new TransientCraftingContainer(dummyMenu, 3, 3);
    private final ResultContainer craftingResult = new ResultContainer();
    private CraftingRecipe currentRecipe;

    @Nullable
    private EnderNetwork network;
    private boolean networkInitialized = false;
    private final List<EchoingEnderShelfBlockEntity> connectedShelves = new ArrayList<>();

    private boolean cachedHasSoulEnergy = false;
    private long lastEnergyCheckTick = Long.MIN_VALUE;

    private static final String NBT_SORTING_DIRECTION = "SortingDirection";
    private static final String NBT_SORTING_TYPE = "SortingType";
    private static final String NBT_VIEW_TYPE = "ViewType";
    private static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";
    private static final String NBT_SIZE = "GridSize";
    private int sortingDirection = 0;
    private int sortingType = 0;
    private int viewType = 0;
    private int searchBoxMode = 0;
    private int size = GridConstants.SIZE_MEDIUM;

    public EnderAccessLecternBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ENDER_ACCESS_LECTERN.get(), pPos, pBlockState);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("CraftingMatrix", Tag.TAG_LIST)) {
            ListTag list = pTag.getList("CraftingMatrix", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                int slot = itemTag.getInt("Slot");
                if (slot >= 0 && slot < CRAFTING_GRID_SIZE) {
                    craftingMatrix.setItem(slot, ItemStack.of(itemTag));
                }
            }
        }
        if (pTag.contains(NBT_SORTING_DIRECTION)) {
            sortingDirection = pTag.getInt(NBT_SORTING_DIRECTION);
        }
        if (pTag.contains(NBT_SORTING_TYPE)) {
            sortingType = pTag.getInt(NBT_SORTING_TYPE);
        }
        if (pTag.contains(NBT_VIEW_TYPE)) {
            viewType = pTag.getInt(NBT_VIEW_TYPE);
        }
        if (pTag.contains(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = pTag.getInt(NBT_SEARCH_BOX_MODE);
        }
        if (pTag.contains(NBT_SIZE)) {
            int s = pTag.getInt(NBT_SIZE);
            if (GridConstants.isValidSize(s)) {
                size = s;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ListTag list = new ListTag();
        for (int i = 0; i < CRAFTING_GRID_SIZE; i++) {
            ItemStack item = craftingMatrix.getItem(i);
            if (!item.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                item.save(itemTag);
                list.add(itemTag);
            }
        }
        pTag.put("CraftingMatrix", list);
        pTag.putInt(NBT_SORTING_DIRECTION, sortingDirection);
        pTag.putInt(NBT_SORTING_TYPE, sortingType);
        pTag.putInt(NBT_VIEW_TYPE, viewType);
        pTag.putInt(NBT_SEARCH_BOX_MODE, searchBoxMode);
        pTag.putInt(NBT_SIZE, size);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
            EnderAccessLecternBlockEntity blockEntity) {
        if (!blockEntity.networkInitialized) {
            blockEntity.initializeNetwork();
        }
        if (level.getGameTime() % 40 == 0) {
            blockEntity.scanForShelves();
        }

        blockEntity.refreshSoulEnergy();

        if (blockEntity.network != null) {
            blockEntity.network.update();
        }

        boolean hasEnergy = blockEntity.cachedHasSoulEnergy;
        boolean isActive = state.getValue(EnderAccessLecternBlock.ACTIVE);
        if (hasEnergy != isActive) {
            level.setBlock(pos, state.setValue(EnderAccessLecternBlock.ACTIVE, Boolean.valueOf(hasEnergy)), 3);
        }
    }

    @Nullable
    public CursedCageBlockEntity getCursedCage() {
        if (level == null) {
            return null;
        }
        BlockPos below = worldPosition.below();
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(below);
        if (be instanceof CursedCageBlockEntity cage) {
            return cage;
        }
        return null;
    }

    public boolean hasSoulEnergy() {
        if (level != null && !level.isClientSide) {
            long gameTime = level.getGameTime();
            if (gameTime - lastEnergyCheckTick < 20) {
                return cachedHasSoulEnergy;
            }
            refreshSoulEnergy();
        }
        return cachedHasSoulEnergy;
    }

    public void refreshSoulEnergy() {
        lastEnergyCheckTick = level != null ? level.getGameTime() : -1;
        cachedHasSoulEnergy = computeSoulEnergy();
    }

    private boolean computeSoulEnergy() {
        CursedCageBlockEntity cage = getCursedCage();
        boolean hasEnergy = false;
        if (cage != null) {
            int souls = cage.getSouls();
            if (souls == 0 && !cage.getItem().isEmpty()) {
                net.minecraft.world.item.ItemStack item = cage.getItem();
                if (item.hasTag() && item.getTag().contains("owner")) {
                    java.util.UUID ownerUUID = item.getTag().getUUID("owner");
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.server.MinecraftServer server = serverLevel.getServer();
                        if (server != null) {
                            net.minecraft.server.level.ServerPlayer owner = server.getPlayerList().getPlayer(ownerUUID);
                            if (owner != null) {
                                souls = com.Polarice3.Goety.utils.SEHelper.getSESouls(owner);
                            }
                        }
                    }
                }
            }
            hasEnergy = souls > 0;
        }
        return hasEnergy;
    }

    private void initializeNetwork() {
        if (level == null || level.isClientSide || isRemoved()) {
            return;
        }

        network = new EnderNetwork(level, worldPosition, this);
        networkInitialized = true;
        scanForShelves();
        network.getItemStorageCache().invalidate(InvalidateCause.NETWORK_CHANGED);
    }

    private void scanForShelves() {
        if (level == null || level.isClientSide || network == null) {
            return;
        }

        Set<BlockPos> newShelfPositions = new HashSet<>();
        List<EchoingEnderShelfBlockEntity> newShelves = new ArrayList<>();
        int range = 2;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;

                    BlockPos checkPos = worldPosition.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(checkPos);

                    if (be instanceof EchoingEnderShelfBlockEntity shelf) {
                        newShelfPositions.add(checkPos);
                        newShelves.add(shelf);
                    }
                }
            }
        }

        Set<BlockPos> oldShelfPositions = new HashSet<>();
        for (EchoingEnderShelfBlockEntity shelf : connectedShelves) {
            oldShelfPositions.add(shelf.getBlockPos());
        }

        if (oldShelfPositions.equals(newShelfPositions)) {
            boolean bound = false;
            for (EchoingEnderShelfBlockEntity shelf : newShelves) {
                if (!shelf.getNetworks().contains(network)) {
                    shelf.bindNetwork(network);
                    shelf.addConnectedLectern(this);
                    bound = true;
                }
            }
            connectedShelves.clear();
            connectedShelves.addAll(newShelves);
            if (bound) {
                network.getNodeGraph().invalidate();
                network.getItemStorageCache().invalidate(InvalidateCause.CONNECTED_STATE_CHANGED);
            }
            return;
        }

        for (EchoingEnderShelfBlockEntity shelf : connectedShelves) {
            if (!newShelfPositions.contains(shelf.getBlockPos())) {
                shelf.removeConnectedLectern(this);
            }
        }

        for (EchoingEnderShelfBlockEntity shelf : newShelves) {
            if (!oldShelfPositions.contains(shelf.getBlockPos())) {
                shelf.bindNetwork(network);
                shelf.addConnectedLectern(this);
            }
        }

        connectedShelves.clear();
        connectedShelves.addAll(newShelves);
        network.getNodeGraph().invalidate();
    }

    public void onCraftingMatrixChanged() {
        if (level == null || level.isClientSide)
            return;

        Optional<CraftingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, craftingMatrix, level);

        if (recipe.isPresent()) {
            currentRecipe = recipe.get();
            craftingResult.setItem(0, currentRecipe.assemble(craftingMatrix, level.registryAccess()));
        } else {
            currentRecipe = null;
            craftingResult.setItem(0, ItemStack.EMPTY);
        }
    }

    public void onCrafted(Player player, ItemStack crafted) {
        if (currentRecipe == null || level == null)
            return;

        NonNullList<ItemStack> remainder = currentRecipe.getRemainingItems(craftingMatrix);

        for (int i = 0; i < craftingMatrix.getContainerSize(); i++) {
            ItemStack slot = craftingMatrix.getItem(i);

            if (i < remainder.size() && !remainder.get(i).isEmpty()) {

                if (!slot.isEmpty() && slot.getCount() > 1) {

                    if (!player.getInventory().add(remainder.get(i).copy())) {
                        ItemStack rem = network != null
                                ? network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(),
                                        Action.PERFORM)
                                : remainder.get(i).copy();
                        if (!rem.isEmpty()) {
                            Containers.dropItemStack(player.level(), player.getX(), player.getY(), player.getZ(), rem);
                        }
                    }
                    craftingMatrix.removeItem(i, 1);
                } else {
                    craftingMatrix.setItem(i, remainder.get(i).copy());
                }
            } else if (!slot.isEmpty()) {

                if (slot.getCount() == 1 && network != null) {

                    ItemStack refill = network.extractItem(slot, 1, IComparer.COMPARE_NBT, Action.PERFORM, s -> true);
                    craftingMatrix.setItem(i, refill);
                } else {
                    craftingMatrix.removeItem(i, 1);
                }
            }
        }

        onCraftingMatrixChanged();
    }

    public void onCraftedShift(Player player) {
        if (currentRecipe == null || level == null)
            return;

        ItemStack crafted = craftingResult.getItem(0);
        if (crafted.isEmpty())
            return;

        int maxCrafted = crafted.getMaxStackSize();
        int amountCrafted = 0;
        boolean useNetwork = network != null;

        IStackList<ItemStack> availableItems = StorageAPI.instance().createItemStackList();
        if (useNetwork) {

            for (int i = 0; i < craftingMatrix.getContainerSize(); i++) {
                ItemStack matItem = craftingMatrix.getItem(i);
                if (!matItem.isEmpty()) {
                    ItemStack fromNet = network.getItemStorageCache().getList().get(matItem);
                    if (fromNet != null && availableItems.get(fromNet) == null) {
                        availableItems.add(fromNet);
                    }
                }
            }
        }

        IStackList<ItemStack> usedItems = StorageAPI.instance().createItemStackList();
        List<ItemStack> craftedItemsList = new ArrayList<>();

        ForgeHooks.setCraftingPlayer(player);
        do {
            onCraftedWithTracking(player, crafted, availableItems, usedItems);
            craftedItemsList.add(crafted.copy());
            amountCrafted += crafted.getCount();
        } while (StorageAPI.instance().getComparer().isEqual(crafted, craftingResult.getItem(0))
                && amountCrafted < maxCrafted
                && amountCrafted + crafted.getCount() <= maxCrafted);

        if (useNetwork) {
            for (StackListEntry<ItemStack> entry : usedItems.getStacks()) {
                network.extractItem(entry.getStack(), entry.getStack().getCount(), IComparer.COMPARE_NBT,
                        Action.PERFORM, s -> true);
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            ItemStack rem = ItemHandlerHelper.insertItem(
                    new PlayerMainInvWrapper(player.getInventory()), craftedItem.copy(), false);
            if (!rem.isEmpty() && useNetwork) {
                rem = network.insertItem(rem, rem.getCount(), Action.PERFORM);
            }
            if (!rem.isEmpty()) {
                Containers.dropItemStack(player.level(), player.getX(), player.getY(), player.getZ(), rem);
            }
        }

        crafted.onCraftedBy(player.level(), player, amountCrafted);
        ForgeHooks.setCraftingPlayer(null);
    }

    private void onCraftedWithTracking(Player player, ItemStack crafted,
            IStackList<ItemStack> availableItems, IStackList<ItemStack> usedItems) {
        if (currentRecipe == null || level == null)
            return;

        NonNullList<ItemStack> remainder = currentRecipe.getRemainingItems(craftingMatrix);

        for (int i = 0; i < craftingMatrix.getContainerSize(); i++) {
            ItemStack slot = craftingMatrix.getItem(i);

            if (i < remainder.size() && !remainder.get(i).isEmpty()) {
                if (!slot.isEmpty() && slot.getCount() > 1) {
                    if (!player.getInventory().add(remainder.get(i).copy())) {
                        ItemStack rem = network != null
                                ? network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(),
                                        Action.PERFORM)
                                : remainder.get(i).copy();
                        if (!rem.isEmpty()) {
                            Containers.dropItemStack(player.level(), player.getX(), player.getY(), player.getZ(), rem);
                        }
                    }
                    craftingMatrix.removeItem(i, 1);
                } else {
                    craftingMatrix.setItem(i, remainder.get(i).copy());
                }
            } else if (!slot.isEmpty()) {
                if (slot.getCount() == 1 && network != null) {

                    ItemStack refill;
                    if (availableItems.get(slot) != null) {
                        refill = availableItems.remove(slot, 1).getStack().copy();
                        refill.setCount(1);
                        usedItems.add(refill);
                    } else {
                        refill = ItemStack.EMPTY;
                    }
                    craftingMatrix.setItem(i, refill);
                } else {
                    craftingMatrix.removeItem(i, 1);
                }
            }
        }

        onCraftingMatrixChanged();
    }

    public void onClear(Player player) {
        for (int i = 0; i < craftingMatrix.getContainerSize(); i++) {
            ItemStack slot = craftingMatrix.getItem(i);
            if (!slot.isEmpty()) {
                if (network != null) {
                    ItemStack rem = network.insertItem(slot, slot.getCount(), Action.PERFORM);
                    if (!rem.isEmpty()) {
                        if (!player.getInventory().add(rem.copy())) {
                            Containers.dropItemStack(player.level(), player.getX(), player.getY(), player.getZ(), rem);
                        }
                    }
                } else {
                    if (!player.getInventory().add(slot.copy())) {
                        Containers.dropItemStack(player.level(), player.getX(), player.getY(), player.getZ(), slot);
                    }
                }
                craftingMatrix.setItem(i, ItemStack.EMPTY);
            }
        }
        onCraftingMatrixChanged();
    }

    public int getSortingDirection() {
        return sortingDirection;
    }

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
        setChanged();
    }

    public int getSortingType() {
        return sortingType;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
        setChanged();
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        setChanged();
    }

    public int getSearchBoxMode() {
        return searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
        setChanged();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (GridConstants.isValidSize(size)) {
            this.size = size;
            setChanged();
        }
    }

    public void onRecipeTransfer(Player player, ItemStack[][] recipe) {
        for (int i = 0; i < craftingMatrix.getContainerSize(); ++i) {
            ItemStack slot = craftingMatrix.getItem(i);

            if (!slot.isEmpty()) {
                if (network != null) {
                    if (!network.insertItem(slot, slot.getCount(), Action.SIMULATE).isEmpty()) {
                        return;
                    }
                    network.insertItem(slot, slot.getCount(), Action.PERFORM);
                } else {
                    if (!player.getInventory().add(slot.copy())) {
                        return;
                    }
                }
                craftingMatrix.setItem(i, ItemStack.EMPTY);
            }
        }

        for (int i = 0; i < craftingMatrix.getContainerSize(); ++i) {
            if (i < recipe.length && recipe[i] != null && recipe[i].length > 0) {
                ItemStack[] possibilities = recipe[i];
                boolean found = false;

                if (network != null) {
                    for (ItemStack possibility : possibilities) {
                        if (possibility.isEmpty())
                            continue;
                        ItemStack took = network.extractItem(possibility, possibility.getCount(),
                                IComparer.COMPARE_NBT, Action.PERFORM, s -> true);
                        if (!took.isEmpty()) {
                            craftingMatrix.setItem(i, took);
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    for (ItemStack possibility : possibilities) {
                        if (possibility.isEmpty())
                            continue;
                        boolean match = false;
                        for (int j = 0; j < player.getInventory().getContainerSize(); ++j) {
                            ItemStack invStack = player.getInventory().getItem(j);
                            if (StorageAPI.instance().getComparer().isEqual(possibility, invStack,
                                    IComparer.COMPARE_NBT)) {
                                craftingMatrix.setItem(i,
                                        ItemHandlerHelper.copyStackWithSize(invStack, 1));
                                player.getInventory().removeItem(j, 1);
                                match = true;
                                break;
                            }
                        }
                        if (match)
                            break;
                    }
                }
            }
        }

        onCraftingMatrixChanged();
    }

    @Nullable
    public INetwork getNetwork() {
        return network;
    }

    @Nullable
    public IItemGridHandler getItemGridHandler() {
        if (isRemoved()) {
            return null;
        }
        if (!networkInitialized && level != null && !level.isClientSide) {
            initializeNetwork();
        }

        return network != null ? network.getItemGridHandler() : null;
    }

    @Nullable
    public IStorageCache<ItemStack> getStorageCache() {
        if (isRemoved()) {
            return null;
        }
        if (!networkInitialized && level != null && !level.isClientSide) {

            initializeNetwork();
        }

        return network != null ? network.getItemStorageCache() : null;
    }

    public CraftingContainer getCraftingMatrix() {
        return craftingMatrix;
    }

    public ResultContainer getCraftingResult() {
        return craftingResult;
    }

    @Nullable
    public CraftingRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public List<EchoingEnderShelfBlockEntity> getConnectedShelves() {
        return connectedShelves;
    }

    public List<IStorageDisk<ItemStack>> getAllStorageDisks() {
        List<IStorageDisk<ItemStack>> disks = new ArrayList<>();
        for (EchoingEnderShelfBlockEntity shelf : connectedShelves) {
            disks.addAll(shelf.getStorageDisks());
        }
        return disks;
    }

    public int getTotalCapacity() {
        int total = 0;
        for (IStorageDisk<ItemStack> disk : getAllStorageDisks()) {
            total += disk.getCapacity();
        }
        return total;
    }

    public int getUsedStorage() {
        int used = 0;
        for (IStorageDisk<ItemStack> disk : getAllStorageDisks()) {
            used += disk.getStored();
        }
        return used;
    }

    public void onShelfRemoved(EchoingEnderShelfBlockEntity shelf) {
        connectedShelves.remove(shelf);
        if (network != null) {
            network.getItemStorageCache().invalidate(InvalidateCause.NETWORK_CHANGED);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (EchoingEnderShelfBlockEntity shelf : connectedShelves) {
            shelf.removeConnectedLectern(this);
        }
        connectedShelves.clear();

        if (network != null) {
            network.onRemoved();
            network = null;
        }
        networkInitialized = false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.goetyawaken.ender_access_lectern");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new EnderAccessLecternContainer(containerId, inventory, this);
    }

    private LazyOptional<IItemHandler> craftingHandler = LazyOptional
            .of(() -> new net.minecraftforge.items.wrapper.InvWrapper(craftingMatrix));

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return craftingHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        craftingHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        craftingHandler = LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(craftingMatrix));
    }
}
