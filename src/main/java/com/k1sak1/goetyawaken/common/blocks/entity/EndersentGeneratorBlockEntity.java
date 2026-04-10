package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.ender.EndersentServant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID)
public class EndersentGeneratorBlockEntity extends BlockEntity {

    private static final int CHECK_INTERVAL = 20;
    private int tickCounter = 0;

    public EndersentGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(com.k1sak1.goetyawaken.common.blocks.ModBlockEntities.ENDERSENT_GENERATOR_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EndersentGeneratorBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.tickCounter++;

            if (blockEntity.tickCounter >= CHECK_INTERVAL) {
                blockEntity.checkForPlayers(serverLevel, pos);
                blockEntity.tickCounter = 0;
            }
        }
    }

    private void checkForPlayers(ServerLevel level, BlockPos pos) {
        for (Entity entity : level.getEntitiesOfClass(Player.class,
                new net.minecraft.world.phys.AABB(pos.getX() - 8, pos.getY() - 8, pos.getZ() - 8, pos.getX() + 8,
                        pos.getY() + 8, pos.getZ() + 8))) {
            if (entity instanceof ServerPlayer player) {
                if (!player.isCreative() && !player.isSpectator()) {
                    if (pos.distToCenterSqr(player.position()) <= 64.0) {
                        level.destroyBlock(getBlockPos(), false);
                        int eyeType = level.getRandom().nextIntBetweenInclusive(1, 10);
                        EndersentServant endersent = new EndersentServant(ModEntityType.ENDERSENT_SERVANT.get(), level);
                        endersent.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                        endersent.setEyeType(eyeType);
                        endersent.isEnhanced = true;
                        endersent.setHostile(true);
                        endersent.applyEnhancedModifiers();
                        endersent.setPersistenceRequired();
                        endersent.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                                net.minecraft.world.entity.MobSpawnType.TRIGGERED, null, null);

                        com.Polarice3.Goety.common.entities.util.SummonCircleBoss summonCircle = new com.Polarice3.Goety.common.entities.util.SummonCircleBoss(
                                level, pos.above(), endersent);
                        level.addFreshEntity(summonCircle);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()
                && event.level instanceof ServerLevel serverLevel) {
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("tickCounter", tickCounter);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tickCounter = tag.getInt("tickCounter");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("tickCounter", tickCounter);
    }
}