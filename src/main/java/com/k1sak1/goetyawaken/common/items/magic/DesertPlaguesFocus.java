package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.k1sak1.goetyawaken.common.magic.spells.wind.DesertPlaguesSpell;

public class DesertPlaguesFocus extends MagicFocus {
    public DesertPlaguesFocus() {
        super(new DesertPlaguesSpell());
    }
}