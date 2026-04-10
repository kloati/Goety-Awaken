package com.k1sak1.goetyawaken.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoetyAwakenNBTUtil {

    public static ListTag writeBlockStatePosMap(Map<BlockPos, BlockState> map) {
        ListTag list = new ListTag();

        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            CompoundTag stateCompound = net.minecraft.nbt.NbtUtils.writeBlockState(entry.getValue());
            stateCompound.put("RelativePos", net.minecraft.nbt.NbtUtils.writeBlockPos(entry.getKey()));
            list.add(stateCompound);
        }

        return list;
    }

    public static Map<BlockPos, BlockState> readBlockStatePosMap(
            HolderGetter<net.minecraft.world.level.block.Block> getter, ListTag list) {
        Map<BlockPos, BlockState> blocks = new HashMap<>();

        for (int i = 0; i < list.size(); ++i) {
            CompoundTag stateCompound = list.getCompound(i);
            BlockState state = net.minecraft.nbt.NbtUtils.readBlockState(getter, stateCompound);
            BlockPos pos = net.minecraft.nbt.NbtUtils.readBlockPos(stateCompound.getCompound("RelativePos"));
            blocks.put(pos, state);
        }

        return blocks;
    }

    public static ListTag writeCompoundList(List<CompoundTag> compounds) {
        ListTag list = new ListTag();

        for (int i = 0; i < compounds.size(); ++i) {
            list.add(compounds.get(i));
        }

        return list;
    }

    public static List<CompoundTag> readCompoundList(ListTag list) {
        List<CompoundTag> compounds = new ArrayList<>();

        for (int i = 0; i < list.size(); ++i) {
            compounds.add(list.getCompound(i));
        }

        return compounds;
    }

    public static CompoundTag writeVector2f(Vec2 vector) {
        CompoundTag compound = new CompoundTag();
        compound.putFloat("x", vector.x);
        compound.putFloat("y", vector.y);
        return compound;
    }

    public static Vec2 readVector2f(CompoundTag compound) {
        return new Vec2(compound.getFloat("x"), compound.getFloat("y"));
    }
}
