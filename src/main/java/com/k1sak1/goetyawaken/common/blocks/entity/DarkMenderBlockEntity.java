package com.k1sak1.goetyawaken.common.blocks.entity;

import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SoulMenderBlock;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntity;
import com.Polarice3.Goety.common.crafting.CursedInfuserRecipes;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public class DarkMenderBlockEntity extends ModBlockEntity implements Clearable, WorldlyContainer {
    private static final int[] SLOTS = new int[] { 0 };
    private ItemStack itemStack = ItemStack.EMPTY;
    private final NonNullList<ItemStack> items = NonNullList.withSize(64, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[64];
    private final int[] cookingTime = new int[64];
    private CursedCageBlockEntity cursedCageTile;

    public DarkMenderBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(com.k1sak1.goetyawaken.common.blocks.ModBlockEntities.DARK_MENDER.get(), p_155229_, p_155230_);
    }

    public void tick() {
        boolean flag = this.checkCage();
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (flag) {
                    if (!this.itemStack.isEmpty()) {
                        if (this.level.getGameTime() % 10 == 0) {
                            int enchantmentMultiplier = 5;
                            if (!this.itemStack.getAllEnchantments().isEmpty()) {
                                for (var enchantment : this.itemStack.getAllEnchantments().entrySet()) {
                                    enchantmentMultiplier += enchantment.getValue();
                                }
                            }
                            int soulCost = 5 * enchantmentMultiplier;
                            if (this.cursedCageTile.getSouls() >= soulCost) {
                                this.makeWorkParticles();
                                this.work(enchantmentMultiplier, soulCost);
                            }
                        }
                    } else if (!this.isEmpty()) {
                        this.makeWorkParticles();
                        this.workRecipes();
                    }
                }
            }
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SoulMenderBlock.LIT, flag), 3);
        }
    }

    private void work(int enchantmentMultiplier, int soulCost) {
        if (this.level != null) {
            if (!this.itemStack.isEmpty()) {
                if (this.itemStack.getItem() instanceof ITotem) {
                    if (!ITotem.isFull(this.itemStack)) {
                        ITotem.increaseSouls(this.itemStack, soulCost);
                        this.cursedCageTile.decreaseSouls(soulCost);
                        if (this.level.random.nextInt(24) == 0) {
                            this.level.playSound(null, this.getBlockPos(), SoundEvents.FIRE_AMBIENT,
                                    SoundSource.BLOCKS, 1.0F + this.level.random.nextFloat(),
                                    this.level.random.nextFloat() * 0.7F + 0.3F);
                        }
                    } else {
                        BlockPos blockpos = this.getBlockPos();
                        Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
                                this.itemStack);
                        this.itemStack = ItemStack.EMPTY;
                        this.finishParticles();
                        this.markUpdated();
                    }
                } else if (this.itemStack.isDamaged()) {
                    int repairAmount = Math.max(this.itemStack.getMaxDamage() / 50, 5);
                    this.itemStack.setDamageValue(Math.max(0, this.itemStack.getDamageValue() - repairAmount));
                    this.cursedCageTile.decreaseSouls(soulCost);
                    if (this.level.random.nextInt(24) == 0) {
                        this.level.playSound(null, this.getBlockPos(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                                1.0F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F);
                    }
                    if (!this.itemStack.isDamaged()) {
                        BlockPos blockpos = this.getBlockPos();
                        Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
                                this.itemStack);
                        this.itemStack = ItemStack.EMPTY;
                        this.finishParticles();
                        this.markUpdated();
                    }
                }
            }
        }
    }

    /**
     * 烧炼模式：处理配方，速度是 Grim Infuser 的 4 倍
     */
    private void workRecipes() {
        if (this.level != null) {
            for (int i = 0; i < this.items.size(); ++i) {
                ItemStack itemstack = this.items.get(i);
                if (!itemstack.isEmpty()) {
                    Container iinventory = new SimpleContainer(itemstack);
                    Optional<CursedInfuserRecipes> optional = this.level.getRecipeManager()
                            .getRecipeFor(ModRecipeSerializer.CURSED_INFUSER.get(), iinventory, this.level);
                    if (optional.isPresent()) {
                        CursedInfuserRecipes recipe = optional.get();
                        if (this.cookingTime[i] <= 0) {
                            this.cookingTime[i] = Math.max(1, recipe.getCookingTime() / 4);
                        }

                        this.cookingProgress[i]++;

                        if (this.cookingProgress[i] % 20 == 0) {
                            this.level.playSound(null, this.getBlockPos(), SoundEvents.FURNACE_FIRE_CRACKLE,
                                    SoundSource.BLOCKS, 1.0F, 1.0F);
                        }

                        if (this.cookingProgress[i] >= this.cookingTime[i]) {
                            ItemStack result = recipe.assemble(iinventory, this.level.registryAccess());
                            this.items.set(i, ItemStack.EMPTY);
                            BlockPos blockpos = this.getBlockPos();
                            Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
                                    result);
                            this.level.playSound(null, this.getBlockPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
                                    SoundSource.BLOCKS, 1.0F, 1.0F);
                            this.markUpdated();
                            this.cookingProgress[i] = 0;
                            this.cookingTime[i] = 0;
                        }
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        if (!this.itemStack.isEmpty()) {
            return false;
        }
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean placeRecipeItem(ItemStack pStack, int pCookTime) {
        if (this.level != null) {
            for (int i = 0; i < this.items.size(); ++i) {
                ItemStack itemstack = this.items.get(i);
                if (itemstack.isEmpty()) {
                    this.cookingTime[i] = Math.max(1, pCookTime / 4);
                    this.cookingProgress[i] = 0;
                    float volume = 0.25F;
                    if (this.isEmpty()) {
                        volume = 1.0F;
                    }
                    this.level.playSound(null, this.getBlockPos(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS,
                            volume, 1.0F);
                    this.items.set(i, pStack.split(1));
                    this.markUpdated();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public ItemStack getItem(int pIndex) {
        if (!this.itemStack.isEmpty()) {
            return this.itemStack;
        }
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        if (!this.itemStack.isEmpty()) {
            ItemStack itemstack = ContainerHelper.removeItem(this.items, 0, pCount);
            if (this.itemStack.isEmpty()) {
                this.setChanged();
            }
            return itemstack;
        }

        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                ItemStack itemstack = ContainerHelper.removeItem(this.items, i, pCount);
                if (this.items.get(i).isEmpty()) {
                    this.cookingProgress[i] = 0;
                    this.cookingTime[i] = 0;
                }
                this.setChanged();
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        if (!this.itemStack.isEmpty()) {
            ItemStack itemstack = this.itemStack.copy();
            this.itemStack = ItemStack.EMPTY;
            this.setChanged();
            return itemstack;
        }

        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                ItemStack itemstack = this.items.get(i).copy();
                this.items.set(i, ItemStack.EMPTY);
                this.cookingProgress[i] = 0;
                this.cookingTime[i] = 0;
                this.setChanged();
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        if (pStack.isEmpty()) {
            return;
        }

        boolean isRepairMode = !this.itemStack.isEmpty()
                || ((pStack.isDamaged() && pStack.isRepairable()) || pStack.getItem() instanceof ITotem);

        if (isRepairMode) {
            if ((pStack.isDamaged() && pStack.isRepairable()) || pStack.getItem() instanceof ITotem) {
                this.placeItem(pStack);
            }
        } else {
            Optional<CursedInfuserRecipes> recipe = this.level.getRecipeManager()
                    .getRecipeFor(ModRecipeSerializer.CURSED_INFUSER.get(), new SimpleContainer(pStack), this.level);
            if (recipe.isPresent()) {
                this.placeRecipeItem(pStack, recipe.get().getCookingTime());
            }
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
                    (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    private void finishParticles() {
        BlockPos blockpos = this.getBlockPos();

        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                serverWorld.sendParticles(ParticleTypes.LARGE_SMOKE, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
                        blockpos.getZ() + 0.5D, 1, 0, 0, 0, 0);
                for (int p = 0; p < 6; ++p) {
                    double d0 = (double) blockpos.getX() + serverWorld.random.nextDouble();
                    double d1 = (double) blockpos.getY() + serverWorld.random.nextDouble();
                    double d2 = (double) blockpos.getZ() + serverWorld.random.nextDouble();
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    private void makeWorkParticles() {
        BlockPos blockpos = this.getBlockPos();
        if (this.level instanceof ServerLevel serverLevel) {
            long t = serverLevel.getGameTime();
            if (t % 20 == 0) {
                for (int p = 0; p < 6; ++p) {
                    double d0 = (double) blockpos.getX() + serverLevel.random.nextDouble();
                    double d1 = (double) blockpos.getY() + serverLevel.random.nextDouble();
                    double d2 = (double) blockpos.getZ() + serverLevel.random.nextDouble();
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    public boolean placeItem(ItemStack pStack) {
        if (this.level != null) {
            if (this.itemStack.isEmpty()) {
                this.itemStack = pStack.split(1);
                this.level.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 0.5F);
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1,
                    this.getBlockPos().getZ());
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity) {
                    this.cursedCageTile = (CursedCageBlockEntity) tileentity;
                    return !cursedCageTile.getItem().isEmpty();
                }
            }
        }
        return false;
    }

    public void readNetwork(CompoundTag compoundNBT) {
        this.itemStack = ItemStack.of(compoundNBT.getCompound("Item"));
        this.items.clear();
        ContainerHelper.loadAllItems(compoundNBT, this.items);
        if (compoundNBT.contains("CookingTimes", 11)) {
            int[] aint = compoundNBT.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (compoundNBT.contains("CookingTotalTimes", 11)) {
            int[] aint1 = compoundNBT.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }
    }

    public CompoundTag writeNetwork(CompoundTag pCompound) {
        this.saveMetadataAndItems(pCompound);
        pCompound.putIntArray("CookingTimes", this.cookingProgress);
        pCompound.putIntArray("CookingTotalTimes", this.cookingTime);
        return pCompound;
    }

    private CompoundTag saveMetadataAndItems(CompoundTag pCompound) {
        pCompound.put("Item", this.itemStack.save(new CompoundTag()));
        ContainerHelper.saveAllItems(pCompound, this.items, true);
        return pCompound;
    }

    @Override
    public void clearContent() {
        if (!this.itemStack.isEmpty()) {
            this.itemStack.shrink(1);
        }
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                this.items.set(i, ItemStack.EMPTY);
                this.cookingProgress[i] = 0;
                this.cookingTime[i] = 0;
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        if (pItemStack.isEmpty()) {
            return false;
        }
        boolean isRepairMode = !this.itemStack.isEmpty()
                || ((pItemStack.isDamaged() && pItemStack.isRepairable()) || pItemStack.getItem() instanceof ITotem);

        if (isRepairMode) {
            if ((pItemStack.isDamaged() && pItemStack.isRepairable()) || pItemStack.getItem() instanceof ITotem) {
                if (this.cursedCageTile == null) {
                    return false;
                }
                return this.level != null && !this.level.isClientSide && this.placeItem(pItemStack);
            }
        } else {
            Optional<CursedInfuserRecipes> recipe = this.level.getRecipeManager()
                    .getRecipeFor(ModRecipeSerializer.CURSED_INFUSER.get(), new SimpleContainer(pItemStack),
                            this.level);
            if (recipe.isPresent()) {
                if (this.cursedCageTile == null) {
                    return false;
                }
                return this.level != null && !this.level.isClientSide
                        && this.placeRecipeItem(pItemStack, recipe.get().getCookingTime());
            }
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }
}
