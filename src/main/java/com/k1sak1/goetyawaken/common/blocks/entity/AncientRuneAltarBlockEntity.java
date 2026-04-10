package com.k1sak1.goetyawaken.common.blocks.entity;

import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.items.NBTEntitySpawnEggItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.registries.ForgeRegistries;

public class AncientRuneAltarBlockEntity extends BlockEntity {
    private CompoundTag storedEntityData;
    private String storedEntityId;
    private boolean hasStoredEntity = false;
    private String teamName;

    public AncientRuneAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_RUNE_ALTAR.get(), pos, state);
        this.teamName = "ancient_altar_" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public InteractionResult onBlockActivated(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() instanceof NBTEntitySpawnEggItem nbtEggItem) {
            if (player.isCreative()) {
                if (NBTEntitySpawnEggItem.hasStoredEntityData(itemstack)) {
                    CompoundTag tag = itemstack.getTag();
                    this.storedEntityData = tag.getCompound(NBTEntitySpawnEggItem.ENTITY_TAG_KEY);
                    this.storedEntityId = tag.getString(NBTEntitySpawnEggItem.ENTITY_ID_KEY);
                    this.hasStoredEntity = true;

                    if (!level.isClientSide()) {
                        player.displayClientMessage(
                                Component.translatable("goetyawaken.ancient_rune_altar.entity_stored"), true);
                    }

                    setChanged();
                    return InteractionResult.SUCCESS;
                } else if (this.hasStoredEntity) {
                    this.storedEntityData = null;
                    this.storedEntityId = null;
                    this.hasStoredEntity = false;

                    if (!level.isClientSide()) {
                        player.displayClientMessage(
                                Component.translatable("goetyawaken.ancient_rune_altar.entity_cleared"), true);
                    }

                    setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!this.hasStoredEntity || level.isClientSide()) {
            return;
        }
        AABB detectionArea = new AABB(pos).inflate(8.0D);
        var players = level.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class, detectionArea);

        for (net.minecraft.world.entity.player.Player player : players) {
            if (!player.isCreative() && !player.isSpectator()) {
                if (canPlayerSeeBlock(player, level, pos)) {
                    activate(pos, level);
                    return;
                }
            }
        }
    }

    private boolean canPlayerSeeBlock(Player player, Level level, BlockPos pos) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        ClipContext context = new ClipContext(eyePos, blockCenter,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        HitResult hitResult = level.clip(context);

        if (hitResult.getType() == HitResult.Type.MISS) {
            return true;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
            return hitPos.equals(pos);
        }

        return false;
    }

    public void activate(BlockPos pos, Level level) {
        if (!(level instanceof ServerLevel serverLevel) || !this.hasStoredEntity) {
            return;
        }

        Entity spawnedEntity = null;

        if (this.storedEntityId != null) {
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES
                    .getValue(net.minecraft.resources.ResourceLocation.tryParse(this.storedEntityId));
            if (entityType != null) {
                Entity entity = entityType.create(serverLevel);
                if (entity != null) {
                    if (this.storedEntityData != null) {
                        CompoundTag entityData = this.storedEntityData.copy();
                        entityData.remove("UUID");
                        entityData.remove("UUIDMost");
                        entityData.remove("UUIDLeast");
                        entity.load(entityData);
                    }
                    entity.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0F, 0.0F);
                    if (entity instanceof IAncientGlint glint) {
                        glint.setAncientGlint(true);
                        glint.setGlintTextureType("ancient");
                    }

                    if (entity instanceof LivingEntity livingEntity) {
                        this.addToTeam(serverLevel, livingEntity);
                    }

                    SummonCircleBoss summonCircle = new SummonCircleBoss(serverLevel, entity.position(), entity);
                    serverLevel.addFreshEntity(summonCircle);
                    spawnedEntity = entity;
                }
            }
        }

        if (spawnedEntity != null) {
            activateSurroundingServantAltars(serverLevel, pos, spawnedEntity);
        }

        serverLevel.destroyBlock(this.worldPosition, false);
    }

    private void activateSurroundingServantAltars(ServerLevel level, BlockPos centerPos, Entity masterEntity) {
        for (int x = -16; x <= 16; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -16; z <= 16; z++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    if (level.getBlockEntity(checkPos) instanceof AncientServantAltarBlockEntity servantAltar) {
                        servantAltar.setTeamName(this.teamName);
                        servantAltar.startActivation(masterEntity);
                    }
                }
            }
        }
    }

    private void addToTeam(ServerLevel level, LivingEntity entity) {
        if (this.teamName != null) {
            var scoreboard = level.getScoreboard();
            var team = scoreboard.getPlayerTeam(this.teamName);
            if (team == null) {
                team = scoreboard.addPlayerTeam(this.teamName);
                team.setAllowFriendlyFire(false);
            }
            scoreboard.addPlayerToTeam(entity.getScoreboardName(), team);
        }
    }

    public Boolean hasStoredEntity() {
        return this.hasStoredEntity;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void dropContents() {
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.hasStoredEntity && this.storedEntityData != null && this.storedEntityId != null) {
            tag.putBoolean("HasStoredEntity", true);
            tag.put("StoredEntityData", this.storedEntityData.copy());
            tag.putString("StoredEntityId", this.storedEntityId);
        } else {
            tag.putBoolean("HasStoredEntity", false);
        }
        if (this.teamName != null) {
            tag.putString("TeamName", this.teamName);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.getBoolean("HasStoredEntity")) {
            this.hasStoredEntity = true;
            this.storedEntityData = tag.getCompound("StoredEntityData").copy();
            this.storedEntityId = tag.getString("StoredEntityId");
        } else {
            this.hasStoredEntity = false;
        }
        if (tag.contains("TeamName")) {
            this.teamName = tag.getString("TeamName");
        } else {
            this.teamName = "ancient_altar_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
    }
}