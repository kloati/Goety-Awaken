package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class MushroomMonstrosityHostile extends MushroomMonstrosity implements Enemy {
    private int isStandingUp;
    private final ModServerBossInfo bossInfo;
    private int destroyBlocksTick;

    public MushroomMonstrosityHostile(EntityType<? extends MushroomMonstrosity> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
        this.setPersistenceRequired();
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
        this.xpReward = 4000;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MushroomMonstrosityHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MushroomMonstrosityArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MushroomMonstrosityArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10D)
                .add(Attributes.ATTACK_KNOCKBACK, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MushroomMonstrosityDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MushroomMonstrosityFollowRange.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1,
                (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected boolean canRide(Entity pEntity) {
        return false;
    }

    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        this.bossInfo.addPlayer(pPlayer);
        GoetyAwaken.PROXY.addBossBar(this.bossInfo.getId(), this);
        if (this.getServer() != null) {
            GoetyAwaken.network.sendTo(pPlayer,
                    new com.k1sak1.goetyawaken.common.network.server.SBossBarPacket(
                            this.bossInfo.getId(), this, false,
                            com.k1sak1.goetyawaken.common.network.server.SBossBarPacket.RENDER_TYPE_MUSHROOM));
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        this.bossInfo.removePlayer(pPlayer);
        GoetyAwaken.PROXY.removeBossBar(this.bossInfo.getId(), this);
        if (this.getServer() != null) {
            GoetyAwaken.network.sendTo(pPlayer,
                    new com.k1sak1.goetyawaken.common.network.server.SBossBarPacket(
                            this.bossInfo.getId(), this, true,
                            com.k1sak1.goetyawaken.common.network.server.SBossBarPacket.RENDER_TYPE_MUSHROOM));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setHealth(this.getMaxHealth());
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public boolean isHostile() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getMaxHealth() > 0 ? this.getHealth() / this.getMaxHealth() : 0);
    }

    @Override
    public int getDeathTime() {
        return this.deathTime;
    }

    @Override
    public float getBigGlow() {
        return super.getBigGlow();
    }

    @Override
    public float getMinorGlow() {
        return super.getMinorGlow();
    }

    @Override
    public boolean isActivating() {
        return this.hasPose(Pose.EMERGING);
    }

    @Override
    public boolean isSummoning() {
        return this.summonTick > 0;
    }

    @Override
    public boolean isBelching() {
        return this.isSpitting();
    }

    @Override
    public boolean canAnimateMove() {
        return super.canAnimateMove() && this.getCurrentAnimation() == this.getAnimationState(WALK);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_219420_) {
        super.recreateFromPacket(p_219420_);
        if (p_219420_.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        ItemStack diamondBlocks = new ItemStack(net.minecraft.world.item.Items.DIAMOND_BLOCK, 8);
        this.spawnAtLocation(diamondBlocks);
        int redstoneCount = 8 + this.getRandom().nextInt(9);
        ItemStack redstoneBlocks = new ItemStack(net.minecraft.world.item.Items.REDSTONE_BLOCK, redstoneCount);
        this.spawnAtLocation(redstoneBlocks);

        if (this.getRandom().nextFloat() < 0.5f) {
            ItemStack musicDisc = new ItemStack(
                    com.k1sak1.goetyawaken.common.items.ModItems.MUSIC_DISC_MOOSHROOM.get());
            this.spawnAtLocation(musicDisc);
        }

        int count = 8 + this.getRandom().nextInt(9);
        ItemStack circuitStack = new ItemStack(
                com.k1sak1.goetyawaken.common.items.ModItems.SUPERAGGREGATED_MYCELIAL_CIRCUIT.get(), count);
        this.spawnAtLocation(circuitStack);

        ItemStack treasureBagStack = new ItemStack(com.Polarice3.Goety.common.items.ModItems.TREASURE_POUCH.get(),
                1);
        this.spawnAtLocation(treasureBagStack);
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {

        if (this.destroyBlocksTick <= 0) {
            this.destroyBlocksTick = 20;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isActivating() || this.isSmashOldAttacking()
                || this.isSmashAttacking()
                || this.isSummoning() || this.isSummoning2() || this.isSpitting();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0
                    && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                int centerY = Mth.floor(this.getY());
                int centerX = Mth.floor(this.getX());
                int centerZ = Mth.floor(this.getZ());
                boolean flag = false;
                for (int dx = -4; dx <= 4; ++dx) {
                    for (int dz = -4; dz <= 4; ++dz) {
                        for (int dy = 1; dy <= 3; ++dy) {
                            int x = centerX + dx;
                            int y = centerY + dy;
                            int z = centerZ + dz;
                            double distanceSquared = dx * dx + dz * dz;
                            if (distanceSquared <= 16.0) {
                                BlockPos blockpos = new BlockPos(x, y, z);
                                if (blockpos.getY() > Mth.floor(this.getY())) {
                                    BlockState blockstate = this.level().getBlockState(blockpos);
                                    if (blockstate.canEntityDestroy(this.level(), blockpos, this) &&
                                            net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this,
                                                    blockpos,
                                                    blockstate)
                                            &&
                                            blockstate.getBlock() != Blocks.OBSIDIAN &&
                                            blockstate.getDestroySpeed(this.level(), blockpos) != -1.0F) {
                                        flag = this.level().destroyBlock(blockpos, true, this) || flag;
                                    }
                                }
                            }
                        }
                    }
                }

                if (flag) {
                    this.level().levelEvent((net.minecraft.world.entity.player.Player) null, 1022, this.blockPosition(),
                            0);
                }
            }
        }
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
            AABB collisionBox = this.getBoundingBox().inflate(0.5D);
            BlockPos.betweenClosedStream(collisionBox)
                    .forEach(pos -> {
                        if (pos.getY() > this.getY()) {
                            BlockState blockState = this.level().getBlockState(pos);
                            if (blockState.canEntityDestroy(this.level(), pos, this) &&
                                    net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, pos,
                                            blockState)
                                    &&
                                    blockState.getBlock() != Blocks.OBSIDIAN &&
                                    blockState.getDestroySpeed(this.level(), pos) != -1.0F) {
                                this.level().destroyBlock(pos, true, this);
                            }
                        }
                    });
        }
    }
}