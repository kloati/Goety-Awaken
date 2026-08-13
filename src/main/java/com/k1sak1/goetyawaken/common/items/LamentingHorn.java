package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.entities.hostile.HostileGiantGhast;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class LamentingHorn extends Item {
    private static final List<SpawnTask> PENDING_TASKS = new ArrayList<>();

    public LamentingHorn() {
        super(new Properties()
                .stacksTo(1)
                .rarity(net.minecraft.world.item.Rarity.UNCOMMON));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.goetyawaken.lamenting_horn.desc").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isInNetherBiome(world, player.blockPosition())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.pass(itemstack);
        } else {
            if (!world.isClientSide) {
                player.displayClientMessage(Component.translatable("item.goetyawaken.lamenting_horn.not_in_nether")
                        .withStyle(ChatFormatting.RED), true);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.NOTE_BLOCK_BASEDRUM.get(), SoundSource.PLAYERS, 0.5F, 0.5F);
            }
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!worldIn.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) worldIn;
            serverWorld.sendParticles(ParticleTypes.SMOKE, livingEntityIn.getX(), livingEntityIn.getY(),
                    livingEntityIn.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (isInNetherBiome(world, player.blockPosition())) {
                if (world.isClientSide) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(),
                            SoundEvents.GOAT_HORN_PLAY, SoundSource.PLAYERS, 1.0F, 1.0F, false);
                } else {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.GIANT_GHAST_LAST_WORDS.get(), SoundSource.HOSTILE, 10.0F, 0.6F);

                    scheduleGiantGhastSpawn(world, player);

                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    private boolean isInNetherBiome(Level world, BlockPos pos) {
        if (world.isClientSide) {
            return false;
        }

        if (world instanceof ServerLevel serverLevel) {
            var biome = serverLevel.getBiome(pos);
            return biome.is(Biomes.NETHER_WASTES) ||
                    biome.is(Biomes.SOUL_SAND_VALLEY) ||
                    biome.is(Biomes.BASALT_DELTAS);
        }

        return false;
    }

    private void scheduleGiantGhastSpawn(Level world, Player player) {
        if (world instanceof ServerLevel serverLevel) {
            BlockPos spawnPos = findValidSpawnPosition(serverLevel, player);
            if (spawnPos != null) {
                synchronized (PENDING_TASKS) {
                    PENDING_TASKS.add(new SpawnTask(serverLevel, player, spawnPos, 120));
                }
                player.displayClientMessage(Component.translatable("item.goetyawaken.lamenting_horn.success")
                        .withStyle(ChatFormatting.RED), true);
            } else {
                player.displayClientMessage(Component.translatable("item.goetyawaken.lamenting_horn.no_spawn_position")
                        .withStyle(ChatFormatting.RED), true);
            }
        }
    }

    private static class SpawnTask {
        ServerLevel world;
        Player player;
        BlockPos spawnPos;
        int remainingTicks;

        SpawnTask(ServerLevel world, Player player, BlockPos spawnPos, int delayTicks) {
            this.world = world;
            this.player = player;
            this.spawnPos = spawnPos;
            this.remainingTicks = delayTicks;
        }

        void tick() {
            remainingTicks--;
            if (remainingTicks <= 0) {
                spawnHostileGiantGhast(world, player, spawnPos);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class TaskHandler {
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                synchronized (PENDING_TASKS) {
                    Iterator<SpawnTask> iterator = PENDING_TASKS.iterator();
                    while (iterator.hasNext()) {
                        SpawnTask task = iterator.next();
                        task.tick();
                        if (task.remainingTicks <= 0) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    private static void spawnHostileGiantGhast(ServerLevel world, Player player, BlockPos spawnPos) {
        HostileGiantGhast giantGhast = new HostileGiantGhast(ModEntityType.HOSTILE_GIANT_GHAST.get(), world);
        giantGhast.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                player.getYRot(), 0.0F);
        giantGhast.setTarget(player);
        world.addFreshEntity(giantGhast);
        giantGhast.playLastWordsSound();
    }

    private BlockPos findValidSpawnPosition(ServerLevel world, Player player) {
        RandomSource random = world.random;
        BlockPos playerPos = player.blockPosition();

        for (int tries = 0; tries < 50; tries++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 32 + random.nextDouble() * 32;
            int dx = (int) (Math.cos(angle) * distance);
            int dz = (int) (Math.sin(angle) * distance);
            BlockPos candidatePos = playerPos.offset(dx, 0, dz);

            for (int y = Math.min(127, world.getMaxBuildHeight()); y > world.getMinBuildHeight(); y--) {
                BlockPos checkPos = new BlockPos(candidatePos.getX(), y, candidatePos.getZ());

                if (!isAreaClear(world, checkPos, 5)) {
                    continue;
                }

                int distanceToGroundBelow = findDistanceToNearestBlock(world, checkPos, -1);
                int distanceToGroundAbove = findDistanceToNearestBlock(world, checkPos, 1);

                if (distanceToGroundBelow > 0 && distanceToGroundAbove > 0) {
                    int totalDistance = distanceToGroundBelow + distanceToGroundAbove;
                    if (totalDistance >= 8) {
                        return checkPos;
                    }
                }
            }
        }

        return null;
    }

    private int findDistanceToNearestBlock(Level world, BlockPos startPos, int directionY) {
        int maxY = directionY > 0 ? world.getMaxBuildHeight() : world.getMinBuildHeight();

        for (int y = startPos.getY() + directionY; directionY > 0 ? y <= maxY : y >= maxY; y += directionY) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            if (!world.isEmptyBlock(checkPos)) {
                return Math.abs(y - startPos.getY());
            }
        }
        return -1;
    }

    private boolean isAreaClear(Level world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = center.offset(x, y, z);
                    if (!world.isEmptyBlock(checkPos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
