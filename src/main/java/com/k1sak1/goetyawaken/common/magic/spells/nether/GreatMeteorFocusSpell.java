package com.k1sak1.goetyawaken.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.k1sak1.goetyawaken.common.entities.projectiles.GiantGhastFireball;
import com.k1sak1.goetyawaken.common.entities.projectiles.GiantHellBlast;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
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

public class GreatMeteorFocusSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.GREAT_METEOR_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.GREAT_METEOR_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.GREAT_METEOR_FOCUS_COOLDOWN.get();
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
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += spellStat.getVelocity();
        }
        if (this.rightStaff(staff)) {
            potency += 2;
        }
        double baseSpeed = 1.5 + 0.05 * velocity;
        float extraDamage = (float) (potency * Config.GREAT_METEOR_POTENCY_DAMAGE.get());
        boolean hasUnholySet = CuriosFinder.hasUnholySet(caster);

        if (hasUnholySet) {
            Vec3 lookVec = caster.getViewVector(1.0F);
            Vec3 motion = lookVec.scale(baseSpeed);

            GiantHellBlast giantHellBlast = new GiantHellBlast(
                    caster.getX(), caster.getY() + caster.getEyeHeight() - 0.2, caster.getZ(),
                    motion.x, motion.y, motion.z, worldIn);
            giantHellBlast.setOwner(caster);
            giantHellBlast.setExtraDamage(extraDamage);
            worldIn.addFreshEntity(giantHellBlast);
            this.playSound(worldIn, caster, ModSounds.GIANT_GHAST_FIREBALL_SHOOT.get(), 1.0F, 1.0F);
            ServerParticleUtil.addParticlesAroundSelf(worldIn, ModParticleTypes.BIG_FIRE.get(), giantHellBlast);
        } else {
            Vec3 lookVec = caster.getViewVector(1.0F);
            Vec3 motion = lookVec.scale(baseSpeed);

            GiantGhastFireball giantGhastFireball = new GiantGhastFireball(
                    caster.getX(), caster.getY() + caster.getEyeHeight() - 0.2, caster.getZ(),
                    motion.x, motion.y, motion.z, worldIn);
            giantGhastFireball.setOwner(caster);
            giantGhastFireball.setExtraDamage(extraDamage);
            if (baseSpeed > 1.2) {
                float velocityBonus = (float) (baseSpeed - 1.2);
                giantGhastFireball.setBoltSpeed((int) velocityBonus);
            }

            worldIn.addFreshEntity(giantGhastFireball);
            this.playSound(worldIn, caster, ModSounds.GIANT_GHAST_FIREBALL_SHOOT.get(), 1.0F, 1.0F);
            ServerParticleUtil.addParticlesAroundSelf(worldIn, ModParticleTypes.BIG_FIRE.get(), giantGhastFireball);
        }
    }
}