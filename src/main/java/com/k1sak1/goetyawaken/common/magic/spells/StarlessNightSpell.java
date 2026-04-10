package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.void_spells.EndWalkSpell;
import com.Polarice3.Goety.common.magic.spells.wind.RazorWindSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class StarlessNightSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return 16;
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public int defaultSpellCooldown() {
        return 60;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (this.typeStaff(staff, SpellType.VOID)) {
            RazorWindSpell razorWindSpell = new RazorWindSpell();
            razorWindSpell.SpellResult(worldIn, caster, staff, spellStat);
        } else {
            EndWalkSpell endWalkSpell = new EndWalkSpell();
            endWalkSpell.SpellResult(worldIn, caster, staff, spellStat);
        }
    }
}