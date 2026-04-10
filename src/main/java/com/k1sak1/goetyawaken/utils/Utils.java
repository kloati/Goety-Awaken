package com.k1sak1.goetyawaken.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Utils {
    public static float findRelativeGroundLevel(Level level, Vec3 start, int maxSteps) {
        if (level.getBlockState(BlockPos.containing(start)).isSuffocating(level, BlockPos.containing(start))) {
            for (int i = 0; i < maxSteps; i++) {
                start = start.add(0, 1, 0);
                BlockPos pos = BlockPos.containing(start);
                if (!level.getBlockState(pos).isSuffocating(level, pos)) {
                    return pos.getY();
                }
            }
        }
        return (float) level.clip(new ClipContext(start, start.add(0, -maxSteps, 0), ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, null)).getLocation().y;
    }
}