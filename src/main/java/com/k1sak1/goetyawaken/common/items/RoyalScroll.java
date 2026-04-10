package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.research.ResearchList;
import com.Polarice3.Goety.common.items.research.Scroll;
import net.minecraft.network.chat.Component;

public class RoyalScroll extends Scroll {
    public RoyalScroll() {
        super(ResearchList.ROYAL);
    }

    @Override
    public Component researchGet() {
        return Component.translatable("info.goetyawaken.research.royal");
    }
}