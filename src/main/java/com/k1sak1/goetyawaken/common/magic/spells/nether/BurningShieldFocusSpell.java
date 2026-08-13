package com.k1sak1.goetyawaken.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.BurningShield;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BurningShieldFocusSpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return Config.BURNING_SHIELD_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.BURNING_SHIELD_FOCUS_CAST_DURATION.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILDFIRE_LOOP.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.BURNING_SHIELD_FOCUS_COOLDOWN.get();
    }

    @Override
    public int SummonDownDuration() {
        return Config.BURNING_SHIELD_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof BurningShield;
    }

    @Override
    public int summonLimit() {
        return Config.BURNING_SHIELD_LIMIT.get();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int burning = spellStat.getBurning();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            radius += (double) WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        if (!isShifting(caster)) {
            int count = 2;
            if (rightStaff(staff)) {
                count = 4;
            }
            for (int i = 0; i < count; ++i) {
                BurningShield shield = new BurningShield(
                        ModEntityType.BURNING_SHIELD.get(), worldIn);
                shield.setTrueOwner(caster);
                Vec3 spawnPos = getShieldSpawnPosition(caster, i, count);
                shield.moveTo(spawnPos.x, spawnPos.y, spawnPos.z);
                shield.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                shield.setPersistenceRequired();
                shield.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                if (potency > 0) {
                    var healthAttr = shield.getAttribute(Attributes.MAX_HEALTH);
                    if (healthAttr != null) {
                        healthAttr.setBaseValue(healthAttr.getBaseValue() + potency);
                        shield.setHealth(shield.getMaxHealth());
                    }
                }
                if (burning > 0) {
                    var armorAttr = shield.getAttribute(Attributes.ARMOR);
                    if (armorAttr != null) {
                        armorAttr.setBaseValue(armorAttr.getBaseValue() + burning * 2.0D);
                    }
                    shield.setBurningLevel(burning);
                }
                if (radius > 0) {
                    shield.setExplosionRadiusLevel(radius);
                }
                worldIn.addFreshEntity(shield);
                this.summonParticles(worldIn, caster, staff, shield);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL_FIERY.get());
        }
    }

    private Vec3 getShieldSpawnPosition(LivingEntity caster, int index, int total) {
        float yaw = (float) Math.toRadians(caster.getYRot());
        double lookX = -Math.sin(yaw);
        double lookZ = Math.cos(yaw);
        double rightX = Math.cos(yaw);
        double rightZ = Math.sin(yaw);
        double offsetX;
        double offsetZ;
        if (total == 2) {
            if (index == 0) {
                offsetX = rightX * BurningShield.DEFAULT_ORBIT_RADIUS;
                offsetZ = rightZ * BurningShield.DEFAULT_ORBIT_RADIUS;
            } else {
                offsetX = -rightX * BurningShield.DEFAULT_ORBIT_RADIUS;
                offsetZ = -rightZ * BurningShield.DEFAULT_ORBIT_RADIUS;
            }
        } else {
            switch (index) {
                case 0:
                    offsetX = rightX * BurningShield.DEFAULT_ORBIT_RADIUS;
                    offsetZ = rightZ * BurningShield.DEFAULT_ORBIT_RADIUS;
                    break;
                case 1:
                    offsetX = -rightX * BurningShield.DEFAULT_ORBIT_RADIUS;
                    offsetZ = -rightZ * BurningShield.DEFAULT_ORBIT_RADIUS;
                    break;
                case 2:
                    offsetX = lookX * BurningShield.DEFAULT_ORBIT_RADIUS;
                    offsetZ = lookZ * BurningShield.DEFAULT_ORBIT_RADIUS;
                    break;
                case 3:
                default:
                    offsetX = -lookX * BurningShield.DEFAULT_ORBIT_RADIUS;
                    offsetZ = -lookZ * BurningShield.DEFAULT_ORBIT_RADIUS;
                    break;
            }
        }

        return new Vec3(
                caster.getX() + offsetX,
                caster.getY() + caster.getEyeHeight() * 0.3,
                caster.getZ() + offsetZ);
    }
}
