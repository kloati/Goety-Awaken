package com.k1sak1.goetyawaken.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireballFeastFocusSpell extends ChargingSpell {

    @Override
    public int defaultSoulCost() {
        return Config.FIREBALL_FEAST_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastUp() {
        return Config.FIREBALL_FEAST_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.FIREBALL_FEAST_FOCUS_COOLDOWN.get();
    }

    @Override
    public int Cooldown() {
        return Config.FIREBALL_FEAST_FOCUS_COOLDOWN.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.GIANT_GHAST_LAST_WORDS.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        return 100;
    }

    @Override
    public int Cooldown(LivingEntity caster, ItemStack staff, int shots) {
        return 2;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        Random random = new Random();
        boolean useLavaball = random.nextDouble() < 0.1;
        if (useLavaball) {
            com.Polarice3.Goety.common.magic.spells.nether.LavaballSpell lavaballSpell = new com.Polarice3.Goety.common.magic.spells.nether.LavaballSpell();
            lavaballSpell.SpellResult(worldIn, caster, staff, spellStat);
        } else {
            com.Polarice3.Goety.common.magic.spells.nether.FireballSpell fireballSpell = new com.Polarice3.Goety.common.magic.spells.nether.FireballSpell();
            fireballSpell.SpellResult(worldIn, caster, staff, spellStat);
        }
    }

}