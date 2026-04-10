package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.Goety.utils.SEHelper;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.magic.spells.AccessFocusSpell;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class AccessFocus extends MagicFocus implements ICurioItem {

    public static final String NBT_BOUND_POS = "BoundLecternPos";
    public static final String NBT_BOUND_DIMENSION = "BoundLecternDim";

    public AccessFocus() {
        super(new AccessFocusSpell());
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                unbind(stack, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EnderAccessLecternBlockEntity) {
            if (!level.isClientSide) {
                bind(stack, pos, level, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                unbind(stack, player);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            int soulCost = Config.ACCESS_FOCUS_SOUL_COST.get();
            if (soulCost > 0 && SEHelper.getSoulsAmount(serverPlayer, soulCost)) {
                SEHelper.decreaseSouls(serverPlayer, soulCost);
                openBoundLectern(stack, serverPlayer, level);
            } else if (soulCost == 0) {
                openBoundLectern(stack, serverPlayer, level);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private void bind(ItemStack stack, BlockPos pos, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong(NBT_BOUND_POS, pos.asLong());
        tag.putString(NBT_BOUND_DIMENSION, level.dimension().location().toString());
        player.displayClientMessage(
                Component.translatable("item.goetyawaken.access_focus.bound",
                        pos.getX(), pos.getY(), pos.getZ())
                        .withStyle(ChatFormatting.GREEN),
                true);
    }

    private void unbind(ItemStack stack, Player player) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_BOUND_POS)) {
            tag.remove(NBT_BOUND_POS);
            tag.remove(NBT_BOUND_DIMENSION);
            player.displayClientMessage(
                    Component.translatable("item.goetyawaken.access_focus.unbound")
                            .withStyle(ChatFormatting.YELLOW),
                    true);
        } else {
            player.displayClientMessage(
                    Component.translatable("item.goetyawaken.access_focus.not_bound")
                            .withStyle(ChatFormatting.RED),
                    true);
        }
    }

    public static void openBoundLectern(ItemStack stack, ServerPlayer player, Level level) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(NBT_BOUND_POS)) {
            player.displayClientMessage(
                    Component.translatable("item.goetyawaken.access_focus.not_bound")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        BlockPos pos = BlockPos.of(tag.getLong(NBT_BOUND_POS));
        String dimKey = tag.getString(NBT_BOUND_DIMENSION);

        net.minecraft.server.MinecraftServer server = player.getServer();
        net.minecraft.server.level.ServerLevel targetLevel = null;
        if (server != null) {
            for (net.minecraft.server.level.ServerLevel sl : server.getAllLevels()) {
                if (sl.dimension().location().toString().equals(dimKey)) {
                    targetLevel = sl;
                    break;
                }
            }
        }
        if (targetLevel == null) {
            player.displayClientMessage(
                    Component.translatable("item.goetyawaken.access_focus.lectern_missing")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        BlockEntity be = targetLevel.getBlockEntity(pos);
        if (!(be instanceof EnderAccessLecternBlockEntity lecternEntity)) {
            player.displayClientMessage(
                    Component.translatable("item.goetyawaken.access_focus.lectern_missing")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        if (!lecternEntity.hasSoulEnergy()) {
            return;
        }

        NetworkHooks.openScreen(player, lecternEntity, buf -> {
            buf.writeBlockPos(pos);
            buf.writeInt(lecternEntity.getSortingDirection());
            buf.writeInt(lecternEntity.getSortingType());
            buf.writeInt(lecternEntity.getViewType());
            buf.writeInt(lecternEntity.getSearchBoxMode());
        });
        player.awardStat(Stats.INTERACT_WITH_LECTERN);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
            TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_BOUND_POS)) {
            BlockPos pos = BlockPos.of(tag.getLong(NBT_BOUND_POS));
            String dim = tag.getString(NBT_BOUND_DIMENSION);
            tooltip.add(Component.translatable("item.goetyawaken.access_focus.tooltip.bound",
                    pos.getX(), pos.getY(), pos.getZ(), dim)
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("item.goetyawaken.access_focus.tooltip.unbound")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
