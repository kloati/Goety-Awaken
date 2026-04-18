package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModShulkerBullet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ShulkerMissileSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return Config.SHULKER_MISSILE_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.SHULKER_MISSILE_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.SHULKER_MISSILE_FOCUS_COOLDOWN.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.CAST_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int range = spellStat.getRange();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
        }
        int missiles = this.rightStaff(staff) ? 5 : 1;
        int speedLevel = 0;
        if (WandUtil.enchantedFocus(caster)) {
            speedLevel = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        float damage = (float) (Config.shulkerMissileBaseDamage + potency * Config.shulkerMissilePotencyDamage)
                * WandUtil.damageMultiply();
        if (this.rightStaff(staff)) {
            this.launchMissilesAtNearbyTargets(worldIn, caster, staff, damage, duration, speedLevel, range);
        } else {
            LivingEntity target = this.getTarget(caster, range);
            this.launchMissilesAtTarget(worldIn, caster, staff, target, damage, duration, speedLevel, 1);
        }

        this.playSound(worldIn, caster, 1.0F, 1.0F);
    }

    private void launchMissilesAtTarget(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            LivingEntity target, float damage, int duration, int speedLevel, int count) {
        ItemStack focus = IWand.getFocus(staff);

        for (int i = 0; i < count; i++) {
            Vec3 spawnPos = caster.position().add(0, caster.getEyeHeight() - 0.2, 0);
            ModShulkerBullet missile;
            if (target != null) {
                missile = new ModShulkerBullet(worldIn, caster, target, net.minecraft.core.Direction.Axis.Y);
            } else {
                missile = new ModShulkerBullet(ModEntityType.MOD_SHULKER_BULLET.get(), worldIn);
                Vec3 lookVec = caster.getViewVector(1.0F);
                missile.setDeltaMovement(lookVec.scale(0.5));
            }

            missile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            missile.setOwner(caster);
            missile.setCustomDamage(damage);
            if (speedLevel > 0) {
                double baseSpeed = 0.5;
                double speedIncrease = speedLevel * 0.1;
                missile.setFlightSpeed(baseSpeed + speedIncrease);
            }
            if (focus.hasTag() && focus.getTag().contains("PotionEffect")) {
                CompoundTag potionTag = focus.getTag().getCompound("PotionEffect");
                String effectName = potionTag.getString("EffectName");
                int effectDuration = potionTag.getInt("Duration");
                int effectAmplifier = potionTag.getInt("Amplifier");
                net.minecraft.world.effect.MobEffect effect = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS
                        .getValue(new net.minecraft.resources.ResourceLocation(effectName));
                boolean isInstantaneous = (effect != null && effect.isInstantenous());
                if (!isInstantaneous) {
                    effectDuration += duration * 20;
                }

                missile.setEffectType(effectName);
                missile.setEffectDuration(effectDuration);
                missile.setEffectAmplifier(effectAmplifier);
            } else {
                missile.setEffectType("minecraft:levitation");
                missile.setEffectDuration(200 + (duration * 20));
                missile.setEffectAmplifier(0);
            }

            worldIn.addFreshEntity(missile);
        }
    }

    private void launchMissilesAtNearbyTargets(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            float damage, int duration, int speedLevel, int range) {
        int i = (int) caster.getX();
        int j = (int) caster.getY();
        int k = (int) caster.getZ();
        int launchedMissiles = 0;
        int maxMissiles = 5;
        int maxTargets = 5;
        int targetsHit = 0;
        List<LivingEntity> nearbyEntities = worldIn.getEntitiesOfClass(LivingEntity.class,
                (new AABB(i, j, k, i, j - 4, k)).inflate(range));

        if (!nearbyEntities.isEmpty()) {
            for (LivingEntity entity : nearbyEntities) {
                if (launchedMissiles >= maxMissiles || targetsHit >= maxTargets) {
                    break;
                }
                if (entity != caster && !MobUtil.areAllies(entity, caster) && entity.isAlive() && !entity.isSpectator()
                        && entity.isPickable()) {
                    this.launchMissilesAtTarget(worldIn, caster, staff, entity, damage, duration, speedLevel, 1);
                    targetsHit++;
                    launchedMissiles++;
                }
            }
        }
        while (launchedMissiles < maxMissiles) {
            if (targetsHit > 0 && !nearbyEntities.isEmpty()) {
                LivingEntity firstTarget = null;
                for (LivingEntity entity : nearbyEntities) {
                    if (entity != caster && !MobUtil.areAllies(entity, caster) && entity.isAlive()
                            && !entity.isSpectator() && entity.isPickable()) {
                        firstTarget = entity;
                        break;
                    }
                }
                if (firstTarget != null) {
                    this.launchMissilesAtTarget(worldIn, caster, staff, firstTarget, damage, duration, speedLevel, 1);
                } else {
                    this.launchMissilesAtTarget(worldIn, caster, staff, null, damage, duration, speedLevel, 1);
                }
            } else {
                this.launchMissilesAtTarget(worldIn, caster, staff, null, damage, duration, speedLevel, 1);
            }
            launchedMissiles++;
        }
    }
}