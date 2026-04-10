package com.k1sak1.goetyawaken.common.blocks.entity;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class AncientServantAltarBlockEntity extends BlockEntity {
    private CompoundTag storedEntityData;
    private String storedEntityId;
    private boolean hasStoredEntity = false;
    private Entity pendingMasterEntity;
    private java.util.UUID masterEntityUUID;
    private String teamName;
    private int activationDelay = 0;
    private boolean isActivating = false;

    public AncientServantAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_SERVANT_ALTAR.get(), pos, state);
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
                                Component.translatable("goetyawaken.ancient_servant_altar.entity_stored"), true);
                    }

                    setChanged();
                    return InteractionResult.SUCCESS;
                } else if (this.hasStoredEntity) {
                    this.storedEntityData = null;
                    this.storedEntityId = null;
                    this.hasStoredEntity = false;

                    if (!level.isClientSide()) {
                        player.displayClientMessage(
                                Component.translatable("goetyawaken.ancient_servant_altar.entity_cleared"), true);
                    }

                    setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.isActivating && !level.isClientSide()) {
            this.activationDelay++;
            if (this.activationDelay >= 80) {
                if (level instanceof ServerLevel serverLevel) {
                    Entity masterEntity = this.pendingMasterEntity;
                    if (masterEntity == null && this.masterEntityUUID != null) {
                        masterEntity = serverLevel.getEntity(this.masterEntityUUID);
                    }
                    activate(pos, serverLevel, masterEntity);
                }
                this.isActivating = false;
                this.activationDelay = 0;
                this.pendingMasterEntity = null;
                this.masterEntityUUID = null;
            }
        }
    }

    public void startActivation(Entity masterEntity) {
        if (!this.hasStoredEntity || this.isActivating) {
            return;
        }

        this.pendingMasterEntity = masterEntity;
        this.masterEntityUUID = masterEntity.getUUID();
        this.isActivating = true;
        this.activationDelay = 0;
        setChanged();
    }

    public void activate(BlockPos pos, Level level, Entity masterEntity) {
        if (!(level instanceof ServerLevel serverLevel) || !this.hasStoredEntity) {
            return;
        }

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
                    if (masterEntity != null) {
                        if (entity instanceof Owned servant) {
                            servant.setTrueOwner((LivingEntity) masterEntity);
                        }
                    }

                    if (entity instanceof IAncientGlint glint) {
                        glint.setAncientGlint(true);
                        glint.setGlintTextureType("enchant");
                    }
                    if (entity instanceof LivingEntity livingEntity) {
                        this.addToTeam(serverLevel, livingEntity);
                    }

                    SummonCircle summonCircle = new SummonCircle(serverLevel, entity.position(), entity,
                            hasStoredEntity, hasStoredEntity, (LivingEntity) masterEntity);
                    serverLevel.addFreshEntity(summonCircle);
                }
            }
        }
        serverLevel.destroyBlock(this.worldPosition, false);
    }

    public Boolean hasStoredEntity() {
        return this.hasStoredEntity;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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
        tag.putBoolean("IsActivating", this.isActivating);
        tag.putInt("ActivationDelay", this.activationDelay);
        if (this.masterEntityUUID != null) {
            tag.putUUID("MasterEntityUUID", this.masterEntityUUID);
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
        this.isActivating = tag.getBoolean("IsActivating");
        this.activationDelay = tag.getInt("ActivationDelay");
        if (tag.hasUUID("MasterEntityUUID")) {
            this.masterEntityUUID = tag.getUUID("MasterEntityUUID");
            if (this.level instanceof ServerLevel serverLevel) {
                this.pendingMasterEntity = serverLevel.getEntity(this.masterEntityUUID);
            }
        } else {
            this.masterEntityUUID = null;
            this.pendingMasterEntity = null;
        }
        if (tag.contains("TeamName")) {
            this.teamName = tag.getString("TeamName");
        }
    }
}
