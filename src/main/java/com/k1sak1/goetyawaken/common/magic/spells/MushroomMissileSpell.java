package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.projectiles.MushroomMissile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MushroomMissileSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.MUSHROOM_MISSILE_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.MUSHROOM_MISSILE_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.MUSHROOM_MISSILE_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(net.minecraft.server.level.ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat) {
        int potency = spellStat.getPotency();
        float speedLevel = spellStat.getVelocity();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            speedLevel += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        double baseSpeed = 1.0 + 0.1 * speedLevel;
        float extraDamage = (float) (potency * Config.mushroomMissilePotencyDamage) * WandUtil.damageMultiply();
        if (this.rightStaff(staff)) {
            launchMushroomMissile(worldIn, caster, extraDamage, baseSpeed, 0.0);
            launchMushroomMissile(worldIn, caster, extraDamage, baseSpeed, Math.toRadians(-30));
            launchMushroomMissile(worldIn, caster, extraDamage, baseSpeed, Math.toRadians(30));
        } else {
            launchMushroomMissile(worldIn, caster, extraDamage, baseSpeed, 0.0);
        }

        this.playSound(worldIn, caster, 1.0F, 1.0F);
    }

    private void launchMushroomMissile(net.minecraft.server.level.ServerLevel worldIn, LivingEntity caster,
            float extraDamage, double speed, double angleOffset) {
        Vec3 spawnPos = caster.position().add(0, caster.getEyeHeight() - 0.2, 0);
        Vec3 lookVec = caster.getViewVector(1.0F);
        if (angleOffset != 0.0) {
            double cosAngle = Math.cos(angleOffset);
            double sinAngle = Math.sin(angleOffset);

            double newX = lookVec.x * cosAngle - lookVec.z * sinAngle;
            double newZ = lookVec.x * sinAngle + lookVec.z * cosAngle;

            lookVec = new Vec3(newX, lookVec.y, newZ).normalize();
        }
        Vec3 motion = lookVec.scale(speed);
        MushroomMissile missile = new MushroomMissile(caster, motion.x, motion.y, motion.z, worldIn);
        missile.moveTo(spawnPos.x, spawnPos.y, spawnPos.z);
        missile.setExtraDamage(extraDamage);
        missile.setDeltaMovement(motion);
        worldIn.addFreshEntity(missile);
    }
}