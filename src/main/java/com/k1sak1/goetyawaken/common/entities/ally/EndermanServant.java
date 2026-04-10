package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndermanServant extends AbstractEnderling implements ICustomAttributes {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndermanServant.class);
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(
            SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", (double) 0.15F,
            AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData.defineId(EndermanServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData.defineId(EndermanServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_CARRIED_ITEM = SynchedEntityData.defineId(
            EndermanServant.class,
            EntityDataSerializers.ITEM_STACK);
    private int lastStareSound = Integer.MIN_VALUE;
    private int targetChangeTime;
    private boolean isBeingStaredAt;
    private int teleportCooldown = 0;
    private int stareCooldown = 0;

    public EndermanServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level p_32613_) {
        return new GroundPathNavigation(this, p_32613_);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.EndermanServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EndermanServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ARMOR, AttributesConfig.EndermanServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.EndermanServantArmorToughness.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
    }

    public void playStareSound() {
        if (this.tickCount >= this.lastStareSound + 400) {
            this.lastStareSound = this.tickCount;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE,
                        this.getSoundSource(), 2.5F, 1.0F, false);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_CREEPY.equals(pKey) && this.hasBeenStaredAt() && this.level().isClientSide) {
            this.playStareSound();
        }

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENDERMAN_STARE, 0.15F, 1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CREEPY, false);
        this.entityData.define(DATA_STARED_AT, false);
        this.entityData.define(DATA_TARGET_BLOCK, new CompoundTag());
        this.entityData.define(DATA_CARRIED_ITEM, ItemStack.EMPTY);
    }

    public boolean isCreepy() {
        return this.entityData.get(DATA_CREEPY);
    }

    public boolean hasBeenStaredAt() {
        return this.entityData.get(DATA_STARED_AT);
    }

    public void setBeingStaredAt() {
        this.entityData.set(DATA_STARED_AT, true);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.EndermanServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.3D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.EndermanServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), 64.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EndermanServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.EndermanServantArmorToughness.get());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            boolean flag = pSource.getDirectEntity() instanceof Projectile;
            if (!pSource.is(DamageTypeTags.IS_PROJECTILE) && !flag) {
                boolean flag2 = super.hurt(pSource, pAmount);
                if (!this.level().isClientSide() && !(pSource.getEntity() instanceof LivingEntity)
                        && this.random.nextInt(10) != 0) {
                    this.teleport();
                }

                return flag2;
            } else {
                for (int i = 0; i < 64; ++i) {
                    if (this.teleport()) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public void setTarget(@Nullable LivingEntity pLivingEntity) {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (pLivingEntity == null) {
            this.targetChangeTime = 0;
            this.entityData.set(DATA_CREEPY, false);
            attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(DATA_CREEPY, true);
            if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
                attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
        }

        super.setTarget(pLivingEntity);
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D,
                        this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        if (this.isAlive() && this.isSensitiveToWater()) {
            if (this.isInWaterOrRain()) {
                if (this.level() != null && !this.level().isClientSide()) {
                    this.hurt(this.damageSources().drown(), 1.0F);
                    if (this.random.nextInt(20) == 0) {
                        this.teleport();
                    }
                }
            }
        }

        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }
        if (this.level() != null && !this.level().isClientSide() && this.isAlive()) {
            LivingEntity target = this.getTarget();
            if (target != null && this.teleportCooldown <= 0) {
                double distanceToTarget = this.distanceToSqr(target);
                if (distanceToTarget > 36.0D || this.random.nextInt(100) == 0) {
                    if (this.random.nextInt(3) == 0) {
                        this.teleportTowardsEntity(target);
                        this.teleportCooldown = 20 + this.random.nextInt(40);
                    }
                }
            }

            LivingEntity owner = this.getTrueOwner();
            if (owner != null && this.distanceToSqr(owner) > 1024.0D && this.getTarget() == null
                    && this.isFollowing()) {
                this.teleportTowardsEntity(owner);
                this.teleportCooldown = 40;
            }
        }

        this.handleBlockFetching();

        super.tick();
    }

    @Override
    public boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.getY() + (double) (this.random.nextInt(64) - 32);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleportAway(d0, d1, d2, true);
        } else {
            return false;
        }
    }

    private boolean teleportTowardsEntity(Entity target) {
        if (!this.level().isClientSide() && this.isAlive() && target != null) {
            Vec3 vector3d = new Vec3(this.getX() - target.getX(), this.getY(0.5D) - target.getEyeY(),
                    this.getZ() - target.getZ());
            vector3d = vector3d.normalize();
            double distance = 4.0D;
            double d1 = target.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * distance;
            double d2 = target.getY() + (double) (this.random.nextInt(8) - 4) - vector3d.y * distance;
            double d3 = target.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * distance;

            return this.teleportAway(d1, d2, d3, true);
        } else {
            return false;
        }
    }

    private boolean teleportAway(double p_32544_, double p_32545_, double p_32546_, boolean p_32547_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_32544_, p_32545_, p_32546_);

        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight()
                && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(p_32544_, p_32545_, p_32546_, p_32547_);
            if (flag2 && p_32547_) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT,
                            this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    private static final EntityDataAccessor<CompoundTag> DATA_TARGET_BLOCK = SynchedEntityData
            .defineId(EndermanServant.class, EntityDataSerializers.COMPOUND_TAG);

    private static final int MAX_BLOCK_TYPES = 5;
    private int blockSearchCooldown = 0;
    private BlockPos targetBlockPos = null;
    private BlockState targetBlockState = null;
    private CompoundTag targetBlockEntityTag = null;
    private int returnTimer = 0;
    private boolean isFetchingBlock = false;
    private boolean isHidingWithBlock = false;

    public ItemStack getCarriedItem() {
        return this.entityData.get(DATA_CARRIED_ITEM);
    }

    @Override
    public int getHidingDuration() {
        return 40;
    }

    private void handleBlockFetching() {
        if (this.level().isClientSide) {
            return;
        }

        if (this.getTarget() != null) {
            return;
        }

        if (this.isFetchingBlock) {
            if (!this.isHiding()) {
                this.returnTimer--;

                if (this.returnTimer <= 0) {
                    this.finishFetchingBlock();
                }
            }
        } else if (this.isHidingWithBlock) {
            this.returnTimer--;
            if (this.returnTimer <= 0) {
                this.deliverBlockToOwner();
            }
        } else if (!this.getCarriedItem().isEmpty()) {
            this.returnTimer--;
            if (this.returnTimer <= 0) {
                this.throwCarriedItem();
            }
        } else if (this.hasTargetBlock() && !this.isHiding()) {
            if (this.blockSearchCooldown > 0) {
                this.blockSearchCooldown--;
            } else {
                this.searchForTargetBlock();
                this.blockSearchCooldown = 100;
            }
        }
    }

    private boolean hasTargetBlock() {
        return !this.entityData.get(DATA_TARGET_BLOCK).isEmpty() && this.entityData.get(DATA_TARGET_BLOCK).size() > 0;
    }

    private BlockState getTargetBlockState() {
        CompoundTag tag = this.entityData.get(DATA_TARGET_BLOCK);
        if (!tag.isEmpty()) {
            for (String key : tag.getAllKeys()) {
                return NbtUtils.readBlockState(
                        this.level().holderLookup(net.minecraft.core.registries.Registries.BLOCK),
                        tag.getCompound(key));
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    private void setTargetBlock(BlockState blockState) {
        CompoundTag tag = this.entityData.get(DATA_TARGET_BLOCK);
        if (tag.isEmpty()) {
            tag = new CompoundTag();
        }

        if (blockState != null && !blockState.isAir()) {
            String blockKey = blockState.getBlock().getDescriptionId();
            tag.put(blockKey, NbtUtils.writeBlockState(blockState));
        }
        this.entityData.set(DATA_TARGET_BLOCK, tag);
    }

    private void searchForTargetBlock() {
        if (!this.hasTargetBlock()) {
            return;
        }

        CompoundTag targetBlocks = this.entityData.get(DATA_TARGET_BLOCK);
        if (targetBlocks.isEmpty()) {
            return;
        }

        LivingEntity owner = this.getTrueOwner();
        if (owner == null) {
            return;
        }

        BlockPos ownerPos = owner.blockPosition();
        BlockPos currentPos = this.blockPosition();
        List<BlockPos> validPositions = new ArrayList<>();
        Map<BlockPos, BlockState> blockStates = new HashMap<>();
        Map<BlockPos, CompoundTag> blockEntities = new HashMap<>();
        for (int x = -32; x <= 32; x++) {
            for (int y = -32; y <= 32; y++) {
                for (int z = -32; z <= 32; z++) {
                    BlockPos checkPos = currentPos.offset(x, y, z);
                    BlockState checkState = this.level().getBlockState(checkPos);
                    Block checkBlock = checkState.getBlock();
                    String blockKey = checkBlock.getDescriptionId();

                    if (targetBlocks.contains(blockKey)) {
                        if (checkPos.closerThan(ownerPos, 32.0D)) {
                            continue;
                        }
                        validPositions.add(checkPos);
                        blockStates.put(checkPos, checkState);
                        BlockEntity blockEntity = this.level().getBlockEntity(checkPos);
                        if (blockEntity != null) {
                            blockEntities.put(checkPos, blockEntity.saveWithoutMetadata());
                        }
                    }
                }
            }
        }
        if (!validPositions.isEmpty()) {
            BlockPos selectedPos = validPositions.get(this.random.nextInt(validPositions.size()));
            this.targetBlockPos = selectedPos;
            this.targetBlockState = blockStates.get(selectedPos);
            this.targetBlockEntityTag = blockEntities.get(selectedPos);
            this.startFetchingBlock();
        }
    }

    private void startFetchingBlock() {
        if (this.targetBlockPos == null) {
            return;
        }

        this.teleportAwayFromBlock();

        this.returnTimer = 40;
        this.isFetchingBlock = true;
    }

    private void teleportAwayFromBlock() {
        if (this.targetBlockPos != null) {
            this.startHide();
            double d0 = this.targetBlockPos.getX() + (this.random.nextDouble() - 0.5D) * 16.0D;
            double d1 = this.targetBlockPos.getY() + (double) (this.random.nextInt(16) - 8);
            double d2 = this.targetBlockPos.getZ() + (this.random.nextDouble() - 0.5D) * 16.0D;
            this.teleportAway(d0, d1, d2, true);
        }
    }

    private void finishFetchingBlock() {
        if (this.targetBlockPos == null || this.targetBlockState == null) {
            this.isFetchingBlock = false;
            return;
        }

        BlockState currentBlockState = this.level().getBlockState(this.targetBlockPos);
        if (!currentBlockState.is(this.targetBlockState.getBlock())) {
            this.isFetchingBlock = false;
            this.targetBlockPos = null;
            this.targetBlockState = null;
            this.targetBlockEntityTag = null;
            return;
        }

        this.level().removeBlock(this.targetBlockPos, false);

        ItemStack blockItem = new ItemStack(this.targetBlockState.getBlock());

        if (this.targetBlockEntityTag != null) {
            blockItem.addTagElement("BlockEntityTag", this.targetBlockEntityTag);
        }

        this.entityData.set(DATA_CARRIED_ITEM, blockItem);

        this.stopHide();

        this.startHidingWithBlock();

        this.targetBlockPos = null;
        this.targetBlockState = null;
        this.targetBlockEntityTag = null;
        this.isFetchingBlock = false;
    }

    private void startHidingWithBlock() {
        this.startHide();
        this.isHidingWithBlock = true;

        this.returnTimer = 40;
    }

    private void deliverBlockToOwner() {
        LivingEntity owner = this.getTrueOwner();
        if (owner != null) {
            Vec3 lookVec = owner.getLookAngle();
            Vec3 spawnPos = owner.position().add(lookVec.x * 2.0D, 0.5D, lookVec.z * 2.0D);

            this.teleportAway(spawnPos.x, spawnPos.y, spawnPos.z, true);

            this.stopHide();
            this.isHidingWithBlock = false;

            this.returnTimer = 20;
        } else {
            this.stopHide();
            this.isHidingWithBlock = false;
            this.throwCarriedItem();
        }
    }

    private void throwCarriedItem() {
        if (!this.getCarriedItem().isEmpty()) {
            LivingEntity owner = this.getTrueOwner();
            if (owner != null) {
                this.throwItemToOwner(this.getCarriedItem(), owner);
            } else {
                ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getEyeY() - 0.3D, this.getZ(),
                        this.getCarriedItem());
                itemEntity.setThrower(this.getUUID());
                itemEntity.setDeltaMovement(this.random.nextGaussian() * 0.02D,
                        this.random.nextGaussian() * 0.02D + 0.2D, this.random.nextGaussian() * 0.02D);
                itemEntity.setDefaultPickUpDelay();
                this.level().addFreshEntity(itemEntity);
            }
            this.entityData.set(DATA_CARRIED_ITEM, ItemStack.EMPTY);
        }

        this.stopHide();
    }

    private void throwItemToOwner(ItemStack itemStack, LivingEntity owner) {
        if (itemStack.isEmpty() || owner == null) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getEyeY() - 0.3D, this.getZ(),
                itemStack);
        itemEntity.setThrower(this.getUUID());
        Vec3 direction = owner.position().add(0, owner.getEyeHeight(), 0)
                .subtract(this.position().add(0, this.getEyeHeight(), 0));
        direction = direction.normalize().scale(0.3D);
        itemEntity.setDeltaMovement(direction.x, direction.y, direction.z);
        itemEntity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itemEntity);
    }

    private void clearTargetBlocks() {
        this.entityData.set(DATA_TARGET_BLOCK, new CompoundTag());
    }

    private boolean isBlockBreakable(BlockState blockState) {
        Block block = blockState.getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);

        if (blockId != null) {
            if (!"minecraft".equals(blockId.getNamespace())) {
                return false;
            }
            if (blockState.hasBlockEntity()) {
                return false;
            }
            for (String blacklistEntry : com.k1sak1.goetyawaken.Config.endermanServantBlacklist) {
                if (blockId.toString().equals(blacklistEntry)) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getTargetBlockCount() {
        CompoundTag tag = this.entityData.get(DATA_TARGET_BLOCK);
        return tag.getAllKeys().size();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isCrouching() && this.getTrueOwner() != null && player == this.getTrueOwner()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.isEmpty()) {
                this.clearTargetBlocks();
                if (!this.level().isClientSide && player instanceof ServerPlayer) {
                    ((ServerPlayer) player).sendSystemMessage(
                            Component.translatable("goetyawaken.enderman_servant.clear_blocks"), true);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (Block.byItem(itemStack.getItem()) != Blocks.AIR) {
                Block block = Block.byItem(itemStack.getItem());
                BlockState blockState = block.defaultBlockState();
                if (this.isBlockBreakable(blockState)) {
                    if (this.getTargetBlockCount() < MAX_BLOCK_TYPES) {
                        this.setTargetBlock(blockState);
                        if (!this.level().isClientSide && player instanceof ServerPlayer) {
                            ((ServerPlayer) player).sendSystemMessage(Component
                                    .translatable("goetyawaken.enderman_servant.set_target_block", block.getName()),
                                    true);
                        }
                    } else {
                        if (!this.level().isClientSide && player instanceof ServerPlayer) {
                            ((ServerPlayer) player).sendSystemMessage(
                                    Component.translatable("goetyawaken.enderman_servant.max_blocks_reached"), true);
                        }
                    }
                } else {
                    if (!this.level().isClientSide && player instanceof ServerPlayer) {
                        ((ServerPlayer) player).sendSystemMessage(Component
                                .translatable("goetyawaken.enderman_servant.unbreakable_block", block.getName()), true);
                    }
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.endermanServantLimit;
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == com.Polarice3.Goety.common.effects.GoetyEffects.VOID_TOUCHED.get()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }
}