package com.k1sak1.goetyawaken.common.items.magic;

import com.k1sak1.goetyawaken.common.magic.spells.nether.BloodRainSpell;
import com.Polarice3.Goety.common.items.magic.MagicFocus;

public class BloodRainFocus extends MagicFocus {
    public BloodRainFocus() {
        super(new BloodRainSpell());
    }
}