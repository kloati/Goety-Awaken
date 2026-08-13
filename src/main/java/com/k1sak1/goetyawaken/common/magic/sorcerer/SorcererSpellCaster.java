package com.k1sak1.goetyawaken.common.magic.sorcerer;

import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public interface SorcererSpellCaster {

    Mob self();

    int getSorcererLevel();

    boolean shouldReduceCastTime();

    boolean isCastingSpell2();

    void setIsCastingSpell(int id);

    boolean isCharging();

    void setCharging(boolean charging);

    boolean isShoot();

    void setShoot(boolean shoot);

    String getCurrentSpellName();

    void setCurrentSpellName(String name);

    SorcererSpellData getSpellData();

    static void castSpell(LivingEntity caster, SorcererSpellEntry entry, SpellStat spellStat) {
        Spell spell = entry.getSpell();
        if (spell == null || caster.level().isClientSide) return;
        int level;
        if (caster instanceof SorcererSpellCaster sc) {
            level = sc.getSorcererLevel();
        } else {
            level = 0;
        }
        spell.SpellResult((ServerLevel) caster.level(), caster, entry.resolveUpgradeStaff(level), spellStat);
    }

    static void castSpell(LivingEntity caster, SorcererSpellEntry entry, SpellStat spellStat, ItemStack wandStack) {
        Spell spell = entry.getSpell();
        if (spell == null || caster.level().isClientSide) return;
        ItemStack staff = wandStack.isEmpty() ? entry.resolveUpgradeStaff(caster instanceof SorcererSpellCaster sc ? sc.getSorcererLevel() : 0) : wandStack;
        spell.SpellResult((ServerLevel) caster.level(), caster, staff, spellStat);
    }

    static boolean isSpellStillValid(SorcererSpellEntry entry) {
        if (entry == null) return false;
        return SorcererSpellConfig.getSpellEntries().stream()
                .anyMatch(e -> e.getFocusRegistryName().equals(entry.getFocusRegistryName()));
    }
}
