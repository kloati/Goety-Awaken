package com.k1sak1.goetyawaken.common.items.integration;

import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShriekWormServant;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class SculkSpore extends Item {

    public SculkSpore() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.sidedSuccess(true);
        }

        Block block = level.getBlockState(clickedPos).getBlock();

        if (!isSculkBlock(block)) {
            return InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        if (ModIntegrationRegistry.SHRIEK_WORM_SERVANT != null) {
            ShriekWormServant template = new ShriekWormServant(ModIntegrationRegistry.SHRIEK_WORM_SERVANT.get(),
                    serverLevel);
            int limit = template.getSummonLimit(player);
            int count = 0;
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof ShriekWormServant servant && servant.getTrueOwner() == player
                        && servant.isAlive()) {
                    ++count;
                }
            }
            if (count >= limit) {
                player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.PASS;
        }

        BlockPos spawnPos = clickedPos.above();
        ShriekWormServant servant = new ShriekWormServant(ModIntegrationRegistry.SHRIEK_WORM_SERVANT.get(),
                serverLevel);
        servant.setTrueOwner(player);
        servant.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), 0.0F);
        servant.setPersistenceRequired();
        servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.TRIGGERED, null, null);
        if (serverLevel.addFreshEntity(servant)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private boolean isSculkBlock(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        if (blockId == null)
            return false;

        String namespace = blockId.getNamespace();
        String path = blockId.getPath();

        if ("minecraft".equals(namespace)) {
            return path.equals("sculk") || path.equals("sculk_catalyst")
                    || path.equals("sculk_shrieker") || path.equals("sculk_sensor")
                    || path.equals("sculk_vein");
        }

        if ("deeperdarker".equals(namespace)) {
            return path.equals("sculk_stone") || path.equals("echo_soil");
        }

        return false;
    }
}
