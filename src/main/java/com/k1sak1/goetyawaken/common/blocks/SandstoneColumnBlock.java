package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class SandstoneColumnBlock extends Block {
    public SandstoneColumnBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.SAND)
                .instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(0.8F, 4.0F)
                .sound(SoundType.STONE));
    }
}
