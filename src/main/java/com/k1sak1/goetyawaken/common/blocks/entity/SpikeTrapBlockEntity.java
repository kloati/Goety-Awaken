package com.k1sak1.goetyawaken.common.blocks.entity;

import com.Polarice3.Goety.init.ModSounds;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.blocks.SpikeTrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SpikeTrapBlockEntity extends BlockEntity {

    public static final int TOTAL_SPIKE_TIME = 25;
    public static final int DAMAGE_WINDOW_UPPER = 24;
    public static final int DAMAGE_WINDOW_LOWER = 15;

    public static final float MIN_DAMAGE = 2.0F;

    private int spikeTime;

    public final AnimationState spikeAnimationState = new AnimationState();

    public SpikeTrapBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SPIKE_TRAP_BLOCK.get(), pPos, pBlockState);
    }

    public void tryTrigger() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.spikeTime > 0) {
            return;
        }
        this.spikeTime = TOTAL_SPIKE_TIME;
        BlockState state = this.getBlockState();
        if (state.hasProperty(SpikeTrapBlock.POWERED) && !state.getValue(SpikeTrapBlock.POWERED)) {
            this.level.setBlock(this.worldPosition,
                    state.setValue(SpikeTrapBlock.POWERED, Boolean.TRUE), 3);
        }
        this.level.playSound(null, this.worldPosition, ModSounds.VANGUARD_SPEAR.get(), SoundSource.BLOCKS,
                0.8F, 0.9F + this.level.random.nextFloat() * 0.2F);
        this.setChanged();
        this.syncToClients();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, SpikeTrapBlockEntity pEntity) {
        if (pLevel.isClientSide) {
            pEntity.updateAnimationState();
            return;
        }
        if (pEntity.spikeTime > 0) {
            if (pEntity.spikeTime <= DAMAGE_WINDOW_UPPER && pEntity.spikeTime >= DAMAGE_WINDOW_LOWER) {
                pEntity.attackEntitiesAbove((ServerLevel) pLevel, pPos);
            }
            pEntity.spikeTime--;
            if (pEntity.spikeTime == 0) {
                if (pState.hasProperty(SpikeTrapBlock.POWERED) && pState.getValue(SpikeTrapBlock.POWERED)) {
                    pLevel.setBlock(pPos, pState.setValue(SpikeTrapBlock.POWERED, Boolean.FALSE), 3);
                }
                pEntity.setChanged();
                pEntity.syncToClients();
            }
        }
    }

    private void attackEntitiesAbove(ServerLevel level, BlockPos pos) {
        AABB above = new AABB(pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, above,
                e -> e.isAlive() && !(e instanceof Player p && (p.isCreative() || p.isSpectator())));
        if (targets.isEmpty()) {
            return;
        }
        DifficultyInstance difficulty = level.getCurrentDifficultyAt(pos);
        float regional = difficulty.getEffectiveDifficulty();
        float damage = Math.max(MIN_DAMAGE, 3.0F * regional);
        for (LivingEntity entity : targets) {
            entity.hurt(level.damageSources().cactus(), damage);
        }
    }

    private void updateAnimationState() {
        if (this.spikeTime > 0) {
            if (!this.spikeAnimationState.isStarted()) {
                this.spikeAnimationState.start(this.getLevel() == null ? 0 : (int) this.getLevel().getGameTime());
            }
        } else {
            if (this.spikeAnimationState.isStarted()) {
                this.spikeAnimationState.stop();
            }
        }
    }

    private void syncToClients() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public int getSpikeTime() {
        return this.spikeTime;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.spikeTime = pTag.getInt("SpikeTime");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("SpikeTime", this.spikeTime);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("SpikeTime", this.spikeTime);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.spikeTime = tag.getInt("SpikeTime");
        }
    }
}
