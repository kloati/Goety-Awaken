package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.k1sak1.goetyawaken.common.magic.spells.wild.CreeperSpell;

public class CreeperFocus extends MagicFocus {
    public CreeperFocus() {
        super(new CreeperSpell());
    }
}