package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.k1sak1.goetyawaken.common.magic.spells.FairyFocusSpell;

public class FairyFocus extends MagicFocus {
    public FairyFocus() {
        super(new FairyFocusSpell());
    }
}