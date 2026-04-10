package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.common.magic.spells.WeakeningSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.ParchedNecromancer;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraftforge.common.Tags;

public class AncientGong extends Item {

    public AncientGong() {
        super(new Properties()
                .stacksTo(1)
                .rarity(net.minecraft.world.item.Rarity.RARE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isInDesertBiome(world, player.blockPosition())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.pass(itemstack);
        } else {
            if (!world.isClientSide) {
                player.displayClientMessage(Component.translatable("item.goetyawaken.ancient_gong.not_in_desert")
                        .withStyle(ChatFormatting.RED), true);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.NOTE_BLOCK_BASEDRUM.get(), SoundSource.PLAYERS, 0.5F, 0.5F);
            }
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!worldIn.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) worldIn;
            serverWorld.sendParticles(ParticleTypes.SMOKE, livingEntityIn.getX(), livingEntityIn.getY(),
                    livingEntityIn.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (isInDesertBiome(world, player.blockPosition())) {
                if (world.isClientSide) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(),
                            SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F, false);
                } else {
                    spawnParchedNecromancer(world, player);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
            }
        }
        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return 25;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    private boolean isInDesertBiome(Level world, BlockPos pos) {
        if (world.isClientSide) {
            return false;
        }

        if (world instanceof ServerLevel serverLevel) {
            return serverLevel.getBiome(pos).is(Tags.Biomes.IS_DESERT);
        }

        return false;
    }

    private void spawnParchedNecromancer(Level world, Player player) {
        if (world instanceof ServerLevel serverLevel) {
            executeWeakeningSpell(serverLevel, player);
            applyEffectsToNearbyEntities(serverLevel, player);
            ParchedNecromancer parchedNecromancer = new ParchedNecromancer(
                    com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_NECROMANCER.get(),
                    serverLevel);
            parchedNecromancer.setHostile(true);
            parchedNecromancer.setTarget(player);
            BlockPos spawnPos = getRandomSpawnPositionAroundPlayer(player);
            parchedNecromancer.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    player.getYRot(), 0.0F);
            SummonCircleBoss summonCircle = new SummonCircleBoss(serverLevel,
                    parchedNecromancer.position(), parchedNecromancer);
            serverLevel.addFreshEntity(summonCircle);
            serverLevel.playSound(null, spawnPos, SoundEvents.ILLUSIONER_CAST_SPELL,
                    SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    private void applyEffectsToNearbyEntities(ServerLevel world, Player player) {
        double radius = 10.0D;
        AABB searchBox = player.getBoundingBox().inflate(radius);

        java.util.List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity.isAlive());

        for (LivingEntity entity : nearbyEntities) {
            entity.addEffect(new MobEffectInstance(
                    GoetyEffects.SAPPED.get(), 200, 7));
            entity.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS, 200, 3));
        }
    }

    private void executeWeakeningSpell(ServerLevel world, Player player) {
        WeakeningSpell weakeningSpell = new WeakeningSpell();
        SpellStat spellStat = new SpellStat(0, 0, 0, 0.0D, 0, 0.0F);
        spellStat.setPotency(3).setRadius(10.0D).setDuration(8);
        weakeningSpell.SpellResult(world, player, ItemStack.EMPTY, spellStat);
    }

    private BlockPos getRandomSpawnPositionAroundPlayer(Player player) {
        Level world = player.level();
        BlockPos playerPos = player.blockPosition();

        for (int tries = 0; tries < 10; tries++) {
            int dx = world.random.nextInt(7) - 3;
            int dz = world.random.nextInt(7) - 3;
            BlockPos candidatePos = playerPos.offset(dx, 0, dz);
            BlockPos groundPos = findGroundPosition(world, candidatePos);
            if (groundPos != null && world.isEmptyBlock(groundPos.above()) &&
                    world.isEmptyBlock(groundPos.above(2))) {
                return groundPos;
            }
        }
        return player.blockPosition().above();
    }

    private BlockPos findGroundPosition(Level world, BlockPos pos) {
        for (int y = pos.getY(); y > world.getMinBuildHeight(); y--) {
            BlockPos currentPos = new BlockPos(pos.getX(), y, pos.getZ());
            if (world.getBlockState(currentPos).isSolidRender(world, currentPos)) {
                return currentPos.above();
            }
        }
        return pos.above();
    }
}