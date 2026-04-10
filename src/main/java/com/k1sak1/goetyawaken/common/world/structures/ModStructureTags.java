package com.k1sak1.goetyawaken.common.world.structures;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface ModStructureTags {
    TagKey<Structure> OMINOUS_CASTLE = create("explorer_maps/ominous_castle");
    TagKey<Structure> CHAOS_PRISON = create("explorer_maps/chaos_prison");
    TagKey<Structure> MIRAGE = create("explorer_maps/mirage");
    TagKey<Structure> GLACIAL_GHOST_HOUSE = create("explorer_maps/glacial_ghost_house");

    private static TagKey<Structure> create(String p_215896_) {
        return TagKey.create(Registries.STRUCTURE, GoetyAwaken.location(p_215896_));
    }
}