package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class AccessFocusSpell extends Spell {

    static final String NBT_BOUND_POS = "BoundLecternPos";
    static final String NBT_BOUND_DIMENSION = "BoundLecternDim";

    @Override
    public int defaultSoulCost() {
        return Config.ACCESS_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.ACCESS_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.ACCESS_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (!(caster instanceof ServerPlayer player)) {
            return;
        }

        ItemStack focusStack = findAccessFocusStack(player, staff);
        if (focusStack.isEmpty()) {
            return;
        }

        CompoundTag tag = focusStack.getTag();
        if (tag == null || !tag.contains(NBT_BOUND_POS)) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable(
                            "item.goetyawaken.access_focus.not_bound"),
                    true);
            return;
        }

        BlockPos pos = BlockPos.of(tag.getLong(NBT_BOUND_POS));
        String dimKey = tag.getString(NBT_BOUND_DIMENSION);
        net.minecraft.server.level.ServerLevel targetLevel = null;
        net.minecraft.server.MinecraftServer server = player.getServer();
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
                    net.minecraft.network.chat.Component.translatable(
                            "item.goetyawaken.access_focus.lectern_missing"),
                    true);
            return;
        }

        BlockEntity be = targetLevel.getBlockEntity(pos);
        if (!(be instanceof EnderAccessLecternBlockEntity lecternEntity)) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable(
                            "item.goetyawaken.access_focus.lectern_missing"),
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

    private ItemStack findAccessFocusStack(ServerPlayer player, ItemStack staff) {
        if (hasBoundPos(staff)) {
            return staff;
        }
        for (net.minecraft.world.InteractionHand hand : net.minecraft.world.InteractionHand.values()) {
            ItemStack inHand = player.getItemInHand(hand);
            if (hasBoundPos(inHand)) {
                return inHand;
            }
        }
        if (staff.getItem() instanceof IWand) {
            try {
                ItemStack wandFocus = IWand.getFocus(staff);
                if (wandFocus != null && !wandFocus.isEmpty() && hasBoundPos(wandFocus)) {
                    return wandFocus;
                }
            } catch (Exception ignored) {
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean hasBoundPos(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_BOUND_POS);
    }
}
