package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.k1sak1.goetyawaken.GoetyAwaken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShadowShriekerBlockEntity extends BlockEntity
        implements GameEventListener.Holder<VibrationSystem.Listener>, VibrationSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WARNING_SOUND_RADIUS = 10;
    private static final int WARDEN_SPAWN_ATTEMPTS = 20;
    private static final int WARDEN_SPAWN_RANGE_XZ = 5;
    private static final int WARDEN_SPAWN_RANGE_Y = 6;
    private static final int DARKNESS_RADIUS = 40;
    private static final int SHRIEKING_TICKS = 90;

    private UUID ownerUUID;
    private String ownerName;
    private int warningLevel;
    private long lastShriekTime = 0;
    private static final int SHRIEK_COOLDOWN = 200;
    private final VibrationSystem.User vibrationUser = new ShadowShriekerBlockEntity.VibrationUser();
    private VibrationSystem.Data vibrationData = new VibrationSystem.Data();
    private final VibrationSystem.Listener vibrationListener = new VibrationSystem.Listener(this);
    private com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity cursedCageTile;

    public ShadowShriekerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SHADOW_SHRIEKER.get(), pPos, pBlockState);
    }

    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("warning_level", 99)) {
            this.warningLevel = pTag.getInt("warning_level");
        }

        if (pTag.contains("ownerUUID")) {
            this.ownerUUID = pTag.getUUID("ownerUUID");
        }

        if (pTag.contains("ownerName")) {
            this.ownerName = pTag.getString("ownerName");
        }

        if (pTag.contains("lastShriekTime")) {
            this.lastShriekTime = pTag.getLong("lastShriekTime");
        }

        if (pTag.contains("listener", 10)) {
            VibrationSystem.Data.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, pTag.getCompound("listener"))
                    .resultOrPartial(GoetyAwaken.LOGGER::error).ifPresent((p_281147_) -> {
                        this.vibrationData = p_281147_;
                    });
        }
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("warning_level", this.warningLevel);

        if (this.ownerUUID != null) {
            pTag.putUUID("ownerUUID", this.ownerUUID);
        }

        if (this.ownerName != null) {
            pTag.putString("ownerName", this.ownerName);
        }

        pTag.putLong("lastShriekTime", this.lastShriekTime);

        VibrationSystem.Data.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.vibrationData).result()
                .ifPresent((p_281147_) -> {
                    pTag.put("listener", p_281147_);
                });
    }

    @Nullable
    public ServerPlayer getOwner() {
        if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            return ((ServerLevel) this.level).getServer().getPlayerList().getPlayer(this.ownerUUID);
        }
        return null;
    }

    public void setOwner(ServerPlayer player) {
        this.ownerUUID = player.getUUID();
        this.ownerName = player.getName().getString();
    }

    public void tryShriekByPlayer(ServerLevel pLevel, ServerPlayer pPlayer) {
        if (this.ownerUUID == null || !this.ownerUUID.equals(pPlayer.getUUID())) {
            return;
        }
        int servantCount = this.getWardenServantCount(pLevel, pPlayer);
        com.k1sak1.goetyawaken.common.entities.ally.WardenServant wardenServant = new com.k1sak1.goetyawaken.common.entities.ally.WardenServant(
                com.k1sak1.goetyawaken.common.entities.ModEntityType.WARDEN_SERVANT.get(), pLevel);
        int maxServants = wardenServant.getSummonLimit(pPlayer);
        if (servantCount >= maxServants) {
            return;
        }

        if (!this.checkCursedCagePower(pLevel) || this.cursedCageTile == null
                || this.cursedCageTile.getSouls() < 10000) {
            return;
        }

        long currentTime = pLevel.getGameTime();
        if (currentTime - this.lastShriekTime < SHRIEK_COOLDOWN) {
            return;
        }

        if (this.consumeEchoingShardFromPlayer(pPlayer)) {
            this.cursedCageTile.decreaseSouls(10000);
            this.shriek(pLevel, pPlayer);
        }
    }

    public void tryShriekByVibration(ServerLevel pLevel) {
        ServerPlayer owner = this.getOwner();
        if (owner == null) {
            return;
        }

        int servantCount = this.getWardenServantCount(pLevel, owner);
        com.k1sak1.goetyawaken.common.entities.ally.WardenServant wardenServant = new com.k1sak1.goetyawaken.common.entities.ally.WardenServant(
                com.k1sak1.goetyawaken.common.entities.ModEntityType.WARDEN_SERVANT.get(), pLevel);
        int maxServants = wardenServant.getSummonLimit(owner);
        if (servantCount >= maxServants) {
            return;
        }

        if (!this.checkCursedCagePower(pLevel) || this.cursedCageTile == null
                || this.cursedCageTile.getSouls() < 10000) {
            return;
        }

        long currentTime = pLevel.getGameTime();
        if (currentTime - this.lastShriekTime < SHRIEK_COOLDOWN) {
            return;
        }

        if (!this.checkNearbyContainersForEchoingShard(pLevel)) {
            return;
        }

        if (this.consumeEchoingShard(pLevel)) {
            this.cursedCageTile.decreaseSouls(10000);
            this.shriek(pLevel, owner);
        }
    }

    private int getWardenServantCount(ServerLevel pLevel, ServerPlayer pPlayer) {
        List<com.k1sak1.goetyawaken.common.entities.ally.WardenServant> servants = pLevel.getEntitiesOfClass(
                com.k1sak1.goetyawaken.common.entities.ally.WardenServant.class,
                new AABB(this.worldPosition).inflate(64));

        int servantCount = 0;
        for (com.k1sak1.goetyawaken.common.entities.ally.WardenServant warden : servants) {
            if (warden.getOwnerUUID() != null && warden.getOwnerUUID().equals(pPlayer.getUUID())) {
                servantCount++;
            }
        }
        return servantCount;
    }

    private boolean checkCursedCagePower(ServerLevel pLevel) {
        BlockPos pos = this.getBlockPos().below();
        BlockState blockState = pLevel.getBlockState(pos);
        if (blockState.is(com.Polarice3.Goety.common.blocks.ModBlocks.CURSED_CAGE_BLOCK.get())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pos);
            if (tileentity instanceof com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity) {
                this.cursedCageTile = (com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity) tileentity;
                return !this.cursedCageTile.getItem().isEmpty();
            }
        }
        return false;
    }

    private boolean checkNearbyContainersForEchoingShard(ServerLevel pLevel) {
        BlockPos blockPos = this.getBlockPos();
        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            BlockPos adjacentPos = blockPos.relative(direction);
            BlockState adjacentState = pLevel.getBlockState(adjacentPos);
            BlockEntity adjacentTile = pLevel.getBlockEntity(adjacentPos);

            if (adjacentTile != null) {
                net.minecraftforge.common.util.LazyOptional<IItemHandler> itemHandlerOpt = adjacentTile
                        .getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
                if (itemHandlerOpt.isPresent()) {
                    IItemHandler itemHandler = itemHandlerOpt.resolve().get();
                    if (this.hasEchoingShardInItemHandler(itemHandler)) {
                        return true;
                    }
                }

                if (adjacentTile instanceof Container) {
                    Container container = (Container) adjacentTile;
                    if (this.hasEchoingShardInContainer(container)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasEchoingShardInItemHandler(IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()
                    && stack.getItem() == com.k1sak1.goetyawaken.common.items.ModItems.PROFOUND_ECHOING_SHARD.get()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEchoingShardInContainer(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()
                    && stack.getItem() == com.k1sak1.goetyawaken.common.items.ModItems.PROFOUND_ECHOING_SHARD.get()) {
                return true;
            }
        }
        return false;
    }

    private boolean extractEchoingShardFromItemHandler(IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()
                    && stack.getItem() == com.k1sak1.goetyawaken.common.items.ModItems.PROFOUND_ECHOING_SHARD.get()) {
                ItemStack extracted = itemHandler.extractItem(i, 1, false);
                if (!extracted.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean extractEchoingShardFromContainer(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()
                    && stack.getItem() == com.k1sak1.goetyawaken.common.items.ModItems.PROFOUND_ECHOING_SHARD.get()) {
                ItemStack extracted = container.removeItem(i, 1);
                if (!extracted.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean consumeEchoingShard(ServerLevel pLevel) {
        BlockPos blockPos = this.getBlockPos();
        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            BlockPos adjacentPos = blockPos.relative(direction);
            BlockState adjacentState = pLevel.getBlockState(adjacentPos);
            BlockEntity adjacentTile = pLevel.getBlockEntity(adjacentPos);

            if (adjacentTile != null) {
                net.minecraftforge.common.util.LazyOptional<IItemHandler> itemHandlerOpt = adjacentTile
                        .getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
                if (itemHandlerOpt.isPresent()) {
                    IItemHandler itemHandler = itemHandlerOpt.resolve().get();
                    if (this.extractEchoingShardFromItemHandler(itemHandler)) {
                        if (adjacentTile instanceof Container) {
                            ((Container) adjacentTile).setChanged();
                        }
                        return true;
                    }
                }

                if (adjacentTile instanceof Container) {
                    Container container = (Container) adjacentTile;
                    if (this.extractEchoingShardFromContainer(container)) {
                        container.setChanged();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean consumeEchoingShardFromPlayer(ServerPlayer player) {
        for (net.minecraft.world.InteractionHand hand : net.minecraft.world.InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty()
                    && stack.getItem() == com.k1sak1.goetyawaken.common.items.ModItems.PROFOUND_ECHOING_SHARD.get()) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private void shriek(ServerLevel pLevel, @Nullable Entity pSourceEntity) {
        this.lastShriekTime = pLevel.getGameTime();

        BlockPos blockpos = this.getBlockPos();
        BlockState blockstate = this.getBlockState();
        pLevel.setBlock(blockpos, blockstate.setValue(ShadowShriekerBlock.SHRIEKING, Boolean.valueOf(true)), 2);
        pLevel.scheduleTick(blockpos, blockstate.getBlock(), 90);
        pLevel.levelEvent(3007, blockpos, 0);
    }

    public void tryRespond(ServerLevel pLevel) {
        AABB aabb = new AABB(this.worldPosition).inflate(DARKNESS_RADIUS);
        List<Entity> entities = pLevel.getEntitiesOfClass(Entity.class, aabb);
        for (Entity entity : entities) {
            if (entity instanceof ServerPlayer) {
                ((ServerPlayer) entity).addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
            }
        }
        pLevel.getServer().tell(new net.minecraft.server.TickTask(pLevel.getServer().getTickCount() + 100, () -> {
            this.summonWardenServant(pLevel);
        }));
    }

    private void summonWardenServant(ServerLevel pLevel) {
        ServerPlayer owner = this.getOwner();
        if (owner != null) {
            int servantCount = this.getWardenServantCount(pLevel, owner);

            com.k1sak1.goetyawaken.common.entities.ally.WardenServant wardenServant = new com.k1sak1.goetyawaken.common.entities.ally.WardenServant(
                    com.k1sak1.goetyawaken.common.entities.ModEntityType.WARDEN_SERVANT.get(), pLevel);
            int maxServants = wardenServant.getSummonLimit(owner);

            if (servantCount < maxServants) {
                Optional<com.k1sak1.goetyawaken.common.entities.ally.WardenServant> warden = net.minecraft.util.SpawnUtil
                        .trySpawnMob(
                                com.k1sak1.goetyawaken.common.entities.ModEntityType.WARDEN_SERVANT.get(),
                                MobSpawnType.MOB_SUMMONED,
                                pLevel,
                                this.worldPosition,
                                WARDEN_SPAWN_ATTEMPTS,
                                WARDEN_SPAWN_RANGE_XZ,
                                WARDEN_SPAWN_RANGE_Y,
                                net.minecraft.util.SpawnUtil.Strategy.ON_TOP_OF_COLLIDER)
                        .map(entity -> (com.k1sak1.goetyawaken.common.entities.ally.WardenServant) entity);

                warden.ifPresent(entity -> {
                    entity.setOwnerId(owner.getUUID());
                    entity.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(this.worldPosition),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    pLevel.playSound(null, this.worldPosition, SoundEvents.WARDEN_EMERGE, SoundSource.BLOCKS, 1.0F,
                            1.0F);
                });
            }
        }
    }

    public VibrationSystem.Listener getListener() {
        return this.vibrationListener;
    }

    class VibrationUser implements VibrationSystem.User {
        private static final int LISTENER_RADIUS = 8;
        private final PositionSource positionSource = new BlockPositionSource(
                ShadowShriekerBlockEntity.this.worldPosition);

        public VibrationUser() {
        }

        public int getListenerRadius() {
            return 8;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public net.minecraft.tags.TagKey<net.minecraft.world.level.gameevent.GameEvent> getListenableEvents() {
            return net.minecraft.tags.GameEventTags.SHRIEKER_CAN_LISTEN;
        }

        public boolean canReceiveVibration(ServerLevel p_281256_, BlockPos p_281528_,
                net.minecraft.world.level.gameevent.GameEvent p_282632_, GameEvent.Context p_282914_) {
            if (!p_282632_.is(net.minecraft.tags.GameEventTags.SHRIEKER_CAN_LISTEN)) {
                return false;
            }
            Entity sourceEntity = p_282914_.sourceEntity();
            if (!(sourceEntity instanceof LivingEntity)) {
                return false;
            }

            LivingEntity livingSource = (LivingEntity) sourceEntity;
            if (!livingSource.getType().getCategory().isFriendly()) {
                if (ShadowShriekerBlockEntity.this.ownerUUID != null) {
                    if (livingSource instanceof ServerPlayer &&
                            ((ServerPlayer) livingSource).getUUID().equals(ShadowShriekerBlockEntity.this.ownerUUID)) {
                        return false;
                    }
                }
                if (livingSource instanceof com.Polarice3.Goety.api.entities.IOwned) {
                    com.Polarice3.Goety.api.entities.IOwned owned = (com.Polarice3.Goety.api.entities.IOwned) livingSource;
                    if (owned.getOwnerId() != null && ShadowShriekerBlockEntity.this.ownerUUID != null &&
                            owned.getOwnerId().equals(ShadowShriekerBlockEntity.this.ownerUUID)) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        public void onReceiveVibration(ServerLevel p_283372_, BlockPos p_281679_,
                net.minecraft.world.level.gameevent.GameEvent p_282474_, @Nullable Entity p_282286_,
                @Nullable Entity p_281384_, float p_283119_) {
            ShadowShriekerBlockEntity.this.tryShriekByVibration(p_283372_);
        }

        public void onDataChanged() {
            ShadowShriekerBlockEntity.this.setChanged();
        }

        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
}