package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.CreeperStatueBlock;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileStatueCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CreeperStatueBlockEntity extends BlockEntity {
    public int tickCount;

    public CreeperStatueBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CREEPER_STATUE.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CreeperStatueBlockEntity pBlockEntity) {
        pBlockEntity.tickCount++;

        if (!pLevel.isClientSide && pState.getValue(CreeperStatueBlock.Y_OFFSET) == 0) {
            if (pBlockEntity.tickCount % 20 == 0) {
                checkForPlayerAndActivate(pLevel, pPos, pState);
            }
        }
    }

    private static void checkForPlayerAndActivate(Level pLevel, BlockPos pPos, BlockState pState) {
        AABB detectionRange = new AABB(pPos).inflate(6.0D);
        Player nearestPlayer = pLevel.getNearestPlayer(
                pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D,
                6.0D, entity -> !entity.isSpectator() && !(entity instanceof Player player && player.isCreative()));

        if (nearestPlayer != null && detectionRange.contains(nearestPlayer.position())) {
            Direction facing = pState.getValue(CreeperStatueBlock.FACING);

            BlockPos basePos = pPos;
            for (int i = 0; i < 2; i++) {
                BlockPos breakPos = basePos.above(i);
                BlockState breakState = pLevel.getBlockState(breakPos);
                if (breakState.getBlock() instanceof CreeperStatueBlock) {
                    pLevel.setBlock(breakPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            HostileStatueCreeper statueCreeper = new HostileStatueCreeper(
                    ModEntityType.HOSTILE_STATUE_CREEPER.get(), pLevel);
            double spawnX = basePos.getX() + 0.5D;
            double spawnY = basePos.getY();
            double spawnZ = basePos.getZ() + 0.5D;
            statueCreeper.setPos(spawnX, spawnY, spawnZ);
            float yRot = facing.toYRot();
            statueCreeper.setYRot(yRot);
            statueCreeper.setYBodyRot(yRot);
            statueCreeper.setYHeadRot(yRot);
            int tier = pLevel.random.nextInt(3) + 1;
            ServerLevel serverLevel = (ServerLevel) pLevel;
            statueCreeper.finalizeSpawn(serverLevel,
                    pLevel.getCurrentDifficultyAt(basePos),
                    MobSpawnType.EVENT, null, null);
            statueCreeper.setTier(tier);
            statueCreeper.setHealth(statueCreeper.getMaxHealth());
            pLevel.addFreshEntity(statueCreeper);
            pLevel.playSound(null, basePos, SoundEvents.STONE_BREAK,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
