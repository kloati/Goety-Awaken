package com.k1sak1.goetyawaken.common.magic.spells.geo;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.projectiles.SilverfishEggEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.EndermiteEggEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class InfestationSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.INFESTATION_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.INFESTATION_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.INFESTATION_FOCUS_COOLDOWN.get();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff,
            com.Polarice3.Goety.common.magic.SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        double radius = spellStat.getRadius();
        float velocity = spellStat.getVelocity();

        if (velocity <= 0.0F) {
            velocity = 0.6F;
        }
        if (WandUtil.enchantedFocus(entityLiving)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving) * 0.2;
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving) * 0.25F;
        }

        SpellStat modifiedSpellStat = new SpellStat(potency, duration, spellStat.getRange(), radius,
                spellStat.getBurning(), velocity);

        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(),
                SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));
        boolean isVoidStaff = staff.is(ModItems.VOID_STAFF.get());
        Vec3 lookVec = entityLiving.getLookAngle();
        boolean isGeoStaff = rightStaff(staff);

        if (isGeoStaff) {
            shootEgg(worldIn, entityLiving, lookVec, isVoidStaff, 0, modifiedSpellStat);
            shootEgg(worldIn, entityLiving, lookVec, isVoidStaff, 15, modifiedSpellStat);
            shootEgg(worldIn, entityLiving, lookVec, isVoidStaff, -15, modifiedSpellStat);
        } else {
            shootEgg(worldIn, entityLiving, lookVec, isVoidStaff, 0, modifiedSpellStat);
        }
        for (int i = 0; i < 8; ++i) {
            worldIn.sendParticles(ParticleTypes.CRIT,
                    entityLiving.getX() + lookVec.x * 1.5D,
                    entityLiving.getY() + entityLiving.getEyeHeight() + lookVec.y * 1.5D,
                    entityLiving.getZ() + lookVec.z * 1.5D,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void shootEgg(ServerLevel worldIn, LivingEntity shooter, Vec3 direction, boolean isVoidStaff,
            float angleOffset, com.Polarice3.Goety.common.magic.SpellStat spellStat) {
        float extraDamage = (float) (Config.infestationBaseDamage
                + spellStat.getPotency() * Config.infestationPotencyDamage);
        if (isVoidStaff) {
            EndermiteEggEntity eggEntity = new EndermiteEggEntity(
                    com.k1sak1.goetyawaken.common.entities.ModEntityType.ENDERMITE_EGG.get(), worldIn, shooter);

            if (spellStat != null) {
                eggEntity.setRadiusLevel((int) (spellStat.getRadius() / 0.25));
                eggEntity.setPowerLevel(spellStat.getPotency());
                eggEntity.setSpeedLevel((int) (spellStat.getVelocity() / 0.1));
                eggEntity.setDurationLevel(spellStat.getDuration());
                eggEntity.setExtraDamage(extraDamage * WandUtil.damageMultiply());
            }

            Vec3 rotatedDirection = direction;
            if (angleOffset != 0) {
                double yaw = Math.atan2(direction.z, direction.x);
                double pitch = Math.asin(direction.y);

                yaw += Math.toRadians(angleOffset);

                double cosPitch = Math.cos(pitch);
                rotatedDirection = new Vec3(
                        Math.cos(yaw) * cosPitch,
                        Math.sin(pitch),
                        Math.sin(yaw) * cosPitch);
            }
            eggEntity.shoot(rotatedDirection.x, rotatedDirection.y, rotatedDirection.z, spellStat.getVelocity(), 1.0F);
            worldIn.addFreshEntity(eggEntity);
        } else {
            SilverfishEggEntity eggEntity = new SilverfishEggEntity(
                    com.k1sak1.goetyawaken.common.entities.ModEntityType.SILVERFISH_EGG.get(), worldIn, shooter);
            if (spellStat != null) {
                eggEntity.setRadiusLevel((int) (spellStat.getRadius() / 0.25));
                eggEntity.setPowerLevel(spellStat.getPotency());
                eggEntity.setSpeedLevel((int) (spellStat.getVelocity() / 0.1));
                eggEntity.setDurationLevel(spellStat.getDuration());
                eggEntity.setExtraDamage(extraDamage * WandUtil.damageMultiply());
            }

            Vec3 rotatedDirection = direction;
            if (angleOffset != 0) {
                double yaw = Math.atan2(direction.z, direction.x);
                double pitch = Math.asin(direction.y);

                yaw += Math.toRadians(angleOffset);

                double cosPitch = Math.cos(pitch);
                rotatedDirection = new Vec3(
                        Math.cos(yaw) * cosPitch,
                        Math.sin(pitch),
                        Math.sin(yaw) * cosPitch);
            }
            eggEntity.shoot(rotatedDirection.x, rotatedDirection.y, rotatedDirection.z, spellStat.getVelocity(), 1.0F);
            worldIn.addFreshEntity(eggEntity);
        }
    }

    @Override
    public com.Polarice3.Goety.api.magic.SpellType getSpellType() {
        return com.Polarice3.Goety.api.magic.SpellType.GEOMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }
}