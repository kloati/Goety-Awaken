package com.k1sak1.goetyawaken.common.items.block;

import com.k1sak1.goetyawaken.common.entities.deco.OminousPainting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class OminousPaintingItem extends Item {

    public OminousPaintingItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        BlockPos placePos = clickedPos.relative(facing);
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (player != null && !mayPlace(player, facing, itemStack, placePos)) {
            return InteractionResult.FAIL;
        } else {
            Level level = context.getLevel();
            OminousPainting painting;
            Optional<OminousPainting> optional = OminousPainting.createOminousRandom(level, placePos, facing);
            if (optional.isEmpty()) {
                return InteractionResult.CONSUME;
            }

            painting = optional.get();

            CompoundTag compoundTag = itemStack.getTag();
            if (compoundTag != null) {
                net.minecraft.world.entity.EntityType.updateCustomEntityTag(level, player, painting, compoundTag);
            }

            if (painting.survives()) {
                if (!level.isClientSide) {
                    painting.playPlacementSound();
                    level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE,
                            painting.position());
                    level.addFreshEntity(painting);
                }

                itemStack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.CONSUME;
            }
        }
    }

    protected boolean mayPlace(Player player, Direction facing, ItemStack itemStack, BlockPos pos) {
        return !facing.getAxis().isVertical() && player.mayUseItemAt(pos, facing, itemStack);
    }
}