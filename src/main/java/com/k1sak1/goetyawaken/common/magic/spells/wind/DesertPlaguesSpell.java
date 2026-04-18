package com.k1sak1.goetyawaken.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.projectiles.DesertPlaguesCloud;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class DesertPlaguesSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(200).setRadius(3.0D);
    }

    @Override
    public int defaultSoulCost() {
        return Config.DESERT_PLAGUES_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.DESERT_PLAGUES_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.DESERT_PLAGUES_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int duration = spellStat.getDuration();
        double radius = spellStat.getRadius();
        float potency = (float) (spellStat.getPotency() * Config.desertPlaguesPotencyDamage)
                * WandUtil.damageMultiply();

        if (WandUtil.enchantedFocus(caster)) {
            range += 2 * WandUtil.getRangeLevel(caster);
            duration += duration * WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            potency += WandUtil.getPotencyLevel(caster) * Config.desertPlaguesPotencyDamage * WandUtil.damageMultiply();
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }

        if (rightStaff(staff)) {
            range += 1;
            potency += 2.0F * WandUtil.damageMultiply();
            radius += 1.0D;
            duration += 1;
        }

        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, radius);
        LivingEntity target = this.getTarget(caster, range);

        if (target != null) {
            DesertPlaguesCloud cloud = new DesertPlaguesCloud(worldIn, caster, target);
            cloud.setExtraDamage(potency / 2);
            cloud.setRadius((float) radius);
            cloud.setLifeSpan(duration);
            cloud.setStaff(!staff.isEmpty());
            worldIn.addFreshEntity(cloud);
        } else if (rayTraceResult instanceof BlockHitResult) {
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            DesertPlaguesCloud cloud = new DesertPlaguesCloud(worldIn, caster, null);
            cloud.setExtraDamage(potency / 2);
            cloud.setRadius((float) radius);
            cloud.setLifeSpan(duration);
            cloud.setStaff(!staff.isEmpty());
            cloud.setPos(blockPos.getX() + 0.5F, blockPos.getY() + 4, blockPos.getZ() + 0.5F);
            worldIn.addFreshEntity(cloud);
        }

        this.playSound(worldIn, caster, net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_IMPACT, 0.5F, 1.0F);
    }
}