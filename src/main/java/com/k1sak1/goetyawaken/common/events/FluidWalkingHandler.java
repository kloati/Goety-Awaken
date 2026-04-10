package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.items.curios.RippleWalkItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FluidWalkingHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        LivingEntity player = event.player;

        if (RippleWalkItem.hasRippleWalkItem(player) && RippleWalkItem.shouldEnableFluidWalking(player)) {
            BlockPos blockUnderPlayer = new BlockPos((int) Math.floor(player.getX()),
                    (int) Math.floor(player.getY() - 0.2), (int) Math.floor(player.getZ()));
            FluidState fluidState = player.level().getFluidState(blockUnderPlayer);
            if (!player.level().getBlockState(blockUnderPlayer).isCollisionShapeFullBlock(player.level(),
                    blockUnderPlayer)) {
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    if (RippleWalkItem.canWalkOnFluid(player, fluidState)) {
                        player.setOnGround(true);
                        player.fallDistance = 0.0F;
                        if (player.getDeltaMovement().y() < 0) {
                            player.setDeltaMovement(
                                    player.getDeltaMovement().x(),
                                    0,
                                    player.getDeltaMovement().z());
                            if (fluidState.is(net.minecraft.tags.FluidTags.LAVA) && !player.fireImmune()
                                    && !net.minecraft.world.item.enchantment.EnchantmentHelper.hasFrostWalker(player)) {
                                player.hurt(player.damageSources().hotFloor(), 1);
                            }
                        }
                    }
                }
            }
        }
    }
}