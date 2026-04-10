package com.k1sak1.goetyawaken.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.entities.projectiles.DeathRay;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DeathRaySpell extends Spell {
    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(18);
    }

    @Override
    public int defaultSoulCost() {
        return Config.DEATH_RAY_SPELL_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.DEATH_RAY_SPELL_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.DEATH_RAY_SPELL_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            range += WandUtil.getRangeLevel(caster) * 6;
        }
        if (rightStaff(staff)) {
            range += 8;
            potency += 2;
        }
        Vec3 lookVec = caster.getViewVector(1.0F);
        Vec3 startPos = caster.getEyePosition();
        Vec3 endPos = startPos.add(lookVec.scale(range));
        DeathRay deathRay = new DeathRay(worldIn, startPos, endPos, caster);
        float extraDamage = (float) (Config.deathRayBaseDamage + potency * Config.deathRayPotencyDamage)
                * WandUtil.damageMultiply();
        deathRay.setExtraDamage(extraDamage);
        worldIn.addFreshEntity(deathRay);
        this.playSound(worldIn, caster, 1.0F, 1.0F);
    }
}