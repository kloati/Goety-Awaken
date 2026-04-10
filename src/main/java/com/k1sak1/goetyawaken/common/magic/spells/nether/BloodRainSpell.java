package com.k1sak1.goetyawaken.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.HellCloud;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class BloodRainSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(100).setRadius(2.0D);
    }

    public int defaultSoulCost() {
        return Config.BLOOD_RAIN_FOCUS_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return Config.BLOOD_RAIN_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.BLOOD_RAIN_FOCUS_COOLDOWN.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.APOSTLE_PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
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

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int duration = spellStat.getDuration();
        double radius = spellStat.getRadius();
        float potency = 0;
        boolean hasUnholySet = CuriosFinder.hasUnholySet(caster);

        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
            duration *= WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
            potency += (WandUtil.getPotencyLevel(caster) * Config.bloodRainPotencyDamage + Config.bloodRainBaseDamage
                    - 1) * WandUtil.damageMultiply();
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }

        if (rightStaff(staff)) {
            radius += 2.0D;
        }

        if (hasUnholySet) {
            potency += 4.0F;
        }

        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, radius);
        LivingEntity target = this.getTarget(caster, range);
        if (target != null) {
            if (target instanceof LivingEntity) {
                HellCloud hellCloud = new HellCloud(worldIn, caster, target);
                hellCloud.setExtraDamage(potency);
                hellCloud.setRadius((float) radius);
                hellCloud.setLifeSpan(duration);
                hellCloud.setStaff(rightStaff(staff));
                worldIn.addFreshEntity(hellCloud);
            }
            this.playSound(worldIn, caster, SoundEvents.PLAYER_HURT_ON_FIRE);
        } else if (rayTraceResult instanceof BlockHitResult) {
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            HellCloud hellCloud = new HellCloud(worldIn, caster, null);
            hellCloud.setExtraDamage(potency);
            hellCloud.setRadius((float) radius);
            hellCloud.setLifeSpan(duration);
            hellCloud.setStaff(rightStaff(staff));
            hellCloud.setPos(blockPos.getX() + 0.5F, blockPos.getY() + 4, blockPos.getZ() + 0.5F);
            worldIn.addFreshEntity(hellCloud);
            this.playSound(worldIn, caster, SoundEvents.PLAYER_HURT_ON_FIRE);
        }
    }
}