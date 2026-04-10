package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.k1sak1.goetyawaken.common.magic.spells.TormentSpell;

public class TormentFocus extends MagicFocus {
    public TormentFocus() {
        super(new TormentSpell());
    }
}