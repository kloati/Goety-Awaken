package com.k1sak1.goetyawaken.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.projectiles.TrackingFireball;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

public class ChasingFlameFocusSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.CHASING_FLAME_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.CHASING_FLAME_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.CHASING_FLAME_FOCUS_COOLDOWN.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.GIANT_GHAST_FIREBALL_SHOOT.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public SpellStat defaultStats() {
        return new SpellStat(0, 0, 32, 0.0, 0, 0.0F);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += spellStat.getVelocity();
        }

        double baseSpeed = 0.9 + 0.1 * velocity;
        float extraDamage = (float) (potency * Config.CHASING_FLAME_POTENCY_DAMAGE.get());
        LivingEntity target = this.getTarget(caster);

        if (this.rightStaff(staff)) {
            launchTrackingFireball(worldIn, caster, target, extraDamage, baseSpeed, 0.0);
            launchTrackingFireball(worldIn, caster, target, extraDamage, baseSpeed, Math.toRadians(-15));
            launchTrackingFireball(worldIn, caster, target, extraDamage, baseSpeed, Math.toRadians(15));
        } else {
            launchTrackingFireball(worldIn, caster, target, extraDamage, baseSpeed, 0.0);
        }

        this.playSound(worldIn, caster, ModSounds.GIANT_GHAST_FIREBALL_SHOOT.get(), 1.0F, 1.0F);
    }

    private void launchTrackingFireball(ServerLevel worldIn, LivingEntity caster, LivingEntity target,
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
        TrackingFireball fireball = new TrackingFireball(ModEntityType.TRACKING_FIREBALL.get(), worldIn);
        fireball.setOwner(caster);
        fireball.moveTo(spawnPos.x, spawnPos.y, spawnPos.z);
        fireball.setDeltaMovement(motion);
        fireball.setExtraDamage(extraDamage);
        if (target != null) {
            fireball.setTarget(target);
        }

        if (speed > 1.5) {
            float velocityBonus = (float) (speed - 1.5);
            fireball.setBoltSpeed((int) velocityBonus);
        }
        worldIn.addFreshEntity(fireball);
    }

}