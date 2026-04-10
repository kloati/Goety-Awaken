package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.items.*;
import com.k1sak1.goetyawaken.common.storage.api.IStorage;
import com.k1sak1.goetyawaken.common.storage.api.IStorageDisk;
import com.k1sak1.goetyawaken.common.storage.api.IStorageDiskProvider;
import com.k1sak1.goetyawaken.common.storage.api.InvalidateCause;
import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import com.k1sak1.goetyawaken.common.storage.network.INetwork;
import com.k1sak1.goetyawaken.common.storage.network.INetworkNode;
import com.k1sak1.goetyawaken.common.storage.network.IStorageProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class EchoingEnderShelfBlockEntity extends BlockEntity implements Container, IStorageProvider, INetworkNode {
    public static final int MAX_BOOKS_IN_STORAGE = 6;
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoingEnderShelfBlockEntity.class);
    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int lastInteractedSlot = -1;

    @Nullable
    private INetwork network;
    private boolean isActive = false;
    private final Set<EnderAccessLecternBlockEntity> connectedLecterns = new HashSet<>();

    public EchoingEnderShelfBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.ECHOING_ENDER_SHELF.get(), pPos, pState);
    }

    private void updateState(int pSlot) {
        if (pSlot >= 0 && pSlot < 6) {
            this.lastInteractedSlot = pSlot;
            BlockState blockstate = this.getBlockState();

            for (int i = 0; i < EchoingEnderShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++i) {
                boolean flag = !this.getItem(i).isEmpty();
                BooleanProperty booleanproperty = EchoingEnderShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
                blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(flag));
            }

            if (this.level != null) {
                this.level.setBlock(this.worldPosition, blockstate, 3);
            }
        } else {
            LOGGER.error("Expected slot 0-5, got {}", pSlot);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items.clear();
        net.minecraft.world.ContainerHelper.loadAllItems(pTag, this.items);
        this.lastInteractedSlot = pTag.getInt("last_interacted_slot");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        net.minecraft.world.ContainerHelper.saveAllItems(pTag, this.items, true);
        pTag.putInt("last_interacted_slot", this.lastInteractedSlot);
    }

    public int count() {
        return (int) this.items.stream().filter(java.util.function.Predicate.not(ItemStack::isEmpty)).count();
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack itemstack = this.items.get(pSlot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = itemstack.copy();
        if (pAmount >= itemstack.getCount()) {
            this.items.set(pSlot, ItemStack.EMPTY);
        } else {
            result = itemstack.split(pAmount);
            if (itemstack.isEmpty()) {
                this.items.set(pSlot, ItemStack.EMPTY);
            }
        }

        if (!result.isEmpty()) {
            this.updateState(pSlot);
            onStorageChanged();
        }

        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return this.removeItem(pSlot, 1);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        if (this.isValidBook(pStack)) {
            this.items.set(pSlot, pStack);
            this.updateState(pSlot);
            onStorageChanged();
        }
    }

    @Override
    public boolean canTakeItem(Container pTarget, int pIndex, ItemStack pStack) {
        return pTarget.hasAnyMatching((p_281577_) -> {
            if (p_281577_.isEmpty()) {
                return true;
            } else {
                return ItemStack.isSameItemSameTags(pStack, p_281577_) && p_281577_.getCount()
                        + pStack.getCount() <= Math.min(p_281577_.getMaxStackSize(), pTarget.getMaxStackSize());
            }
        });
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return this.isValidBook(pStack) && this.getItem(pIndex).isEmpty();
    }

    public int getLastInteractedSlot() {
        return this.lastInteractedSlot;
    }

    private boolean isValidBook(ItemStack stack) {
        return stack.getItem() instanceof IStorageDiskProvider;
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        if (level == null || level.isClientSide) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            ItemStack bookStack = items.get(i);
            if (!bookStack.isEmpty() && bookStack.getItem() instanceof IStorageDiskProvider provider) {
                IStorageDisk<ItemStack> disk = getStorageDisk(bookStack);
                if (disk != null) {
                    storages.add(disk);
                }
            }
        }
    }

    @Nullable
    public IStorageDisk<ItemStack> getStorageDisk(ItemStack bookStack) {
        if (level == null || level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (bookStack.getItem() instanceof IStorageDiskProvider provider) {
            UUID id = provider.getId(bookStack);
            if (id != null) {
                return StorageAPI.instance().getStorageDiskManager(serverLevel).get(id);
            }
        }
        return null;
    }

    public List<IStorageDisk<ItemStack>> getStorageDisks() {
        List<IStorageDisk<ItemStack>> disks = new ArrayList<>();
        if (level == null || level.isClientSide) {
            return disks;
        }

        for (int i = 0; i < items.size(); i++) {
            ItemStack bookStack = items.get(i);
            IStorageDisk<ItemStack> disk = getStorageDisk(bookStack);
            if (disk != null) {
                disks.add(disk);
            }
        }
        return disks;
    }

    @Override
    public boolean isActive() {
        return isActive && network != null;
    }

    @Override
    public BlockPos getPosition() {
        return worldPosition;
    }

    public void setNetwork(@Nullable INetwork network) {
        this.network = network;
        this.isActive = network != null;
        if (network != null) {
            network.getItemStorageCache().invalidate(InvalidateCause.CONNECTED_STATE_CHANGED);
        }
    }

    public void addConnectedLectern(EnderAccessLecternBlockEntity lectern) {
        connectedLecterns.add(lectern);
    }

    public void removeConnectedLectern(EnderAccessLecternBlockEntity lectern) {
        connectedLecterns.remove(lectern);
        if (connectedLecterns.isEmpty()) {
            setNetwork(null);
        }
    }

    public void notifyOtherNetworks(INetwork sourceNetwork) {
        for (EnderAccessLecternBlockEntity lectern : new ArrayList<>(connectedLecterns)) {
            INetwork net = lectern.getNetwork();
            if (net != null && net != sourceNetwork) {
                net.getItemStorageCache().invalidate(InvalidateCause.DISK_INVENTORY_CHANGED);
            }
        }
    }

    @Nullable
    public INetwork getNetwork() {
        return network;
    }

    public boolean hasStorageBooks() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty() && stack.getItem() instanceof IStorageDiskProvider) {
                return true;
            }
        }
        return false;
    }

    public int getStorageBookCount() {
        int count = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty() && stack.getItem() instanceof IStorageDiskProvider) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (EnderAccessLecternBlockEntity lectern : new ArrayList<>(connectedLecterns)) {
            lectern.onShelfRemoved(this);
        }
        connectedLecterns.clear();
        if (network != null) {
            network.getNodeGraph().invalidate();
            setNetwork(null);
        }
    }

    public void onStorageChanged() {
        if (network != null) {
            network.getItemStorageCache().invalidate(InvalidateCause.DISK_INVENTORY_CHANGED);
            network.markDirty();
        }
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional
            .of(this::createUnSidedHandler);

    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    @Override
    public <T> @NotNull net.minecraftforge.common.util.LazyOptional<T> getCapability(
            net.minecraftforge.common.capabilities.Capability<T> cap,
            @org.jetbrains.annotations.Nullable net.minecraft.core.Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && !this.remove)
            return itemHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(this::createUnSidedHandler);
    }
}