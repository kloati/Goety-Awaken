package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.google.common.collect.Sets;
import net.minecraft.client.model.geom.ModelLayerLocation;

import java.util.Set;

public class ModBlockLayer {
    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
    public static final ModelLayerLocation NAMELESS_CHEST = register("nameless_chest");
    public static final ModelLayerLocation MOOSHROOM_MONSTROSITY_HEAD = register("mooshroom_monstrosity_head");

    private static ModelLayerLocation register(String p_171294_) {
        return register(p_171294_, "main");
    }

    private static ModelLayerLocation register(String p_171296_, String p_171297_) {
        ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
        if (!ALL_MODELS.add(modellayerlocation)) {
            throw new IllegalStateException("Duplicate registration for " + modellayerlocation);
        } else {
            return modellayerlocation;
        }
    }

    private static ModelLayerLocation createLocation(String p_171301_, String p_171302_) {
        return new ModelLayerLocation(GoetyAwaken.location(p_171301_), p_171302_);
    }

}
