package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ModProjectileTargetedEntity;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChipRainFocusSpell extends ChargingSpell {
    private Entity cachedTarget;

    @Override
    public int defaultSoulCost() {
        return Config.CHIP_RAIN_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastUp() {
        return Config.CHIP_RAIN_FOCUS_CAST_UP.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.CHIP_RAIN_FOCUS_COOLDOWN.get();
    }

    @Override
    public int Cooldown() {
        return Config.CHIP_RAIN_FOCUS_COOLDOWN.get();
    }

    @Override
    public int Cooldown(LivingEntity caster, ItemStack staff, int shots) {
        return 5;
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        return 100;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return MYFSounds.dameFortunaChipsStart.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        cachedTarget = this.getTarget(caster);
        if (cachedTarget == null) {
            int range = spellStat.getRange();
            if (WandUtil.enchantedFocus(caster)) {
                range += WandUtil.getRangeLevel(caster);
            }
            HitResult hitResult = this.rayTrace(worldIn, caster, range, spellStat.getRadius());
            if (hitResult instanceof BlockHitResult blockHitResult) {
                Marker marker = new Marker(EntityType.MARKER, worldIn);
                marker.setPos(blockHitResult.getBlockPos().getX() + 0.5,
                        blockHitResult.getBlockPos().getY() + 0.5,
                        blockHitResult.getBlockPos().getZ() + 0.5);
                worldIn.addFreshEntity(marker);
                cachedTarget = marker;
            }
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime,
            SpellStat spellStat) {
        cachedTarget = null;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
        }
        double extraDamage = potency * Config.CHIP_RAIN_FOCUS_POTENCY_DAMAGE.get();
        LivingEntity lookedTarget = this.getTarget(caster);
        if (lookedTarget != null && lookedTarget.isAlive()
                && !MobUtil.areAllies(caster, lookedTarget)) {
            cachedTarget = lookedTarget;
        } else if (cachedTarget == null || !cachedTarget.isAlive()
                || cachedTarget instanceof Marker) {
            int range = spellStat.getRange();
            if (WandUtil.enchantedFocus(caster)) {
                range += WandUtil.getRangeLevel(caster);
            }
            HitResult hitResult = this.rayTrace(worldIn, caster, range, spellStat.getRadius());
            if (hitResult instanceof BlockHitResult blockHitResult) {
                Marker marker = new Marker(EntityType.MARKER, worldIn);
                marker.setPos(blockHitResult.getBlockPos().getX() + 0.5,
                        blockHitResult.getBlockPos().getY() + 0.5,
                        blockHitResult.getBlockPos().getZ() + 0.5);
                worldIn.addFreshEntity(marker);
                cachedTarget = marker;
            } else {
                return;
            }
        }

        Entity target = cachedTarget;
        if (target == null || !target.isAlive()) {
            return;
        }

        double cx = caster.getX();
        double cy = caster.getY() + 1.0;
        double cz = caster.getZ();

        Vec3 perp = caster.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
        perp = perp.yRot(worldIn.random.nextFloat() * Mth.TWO_PI);

        ModProjectileTargetedEntity proj = new ModProjectileTargetedEntity(worldIn, caster);
        proj.setOwner(caster);
        proj.setPos(cx, cy, cz);
        proj.setExtraDamage((float) extraDamage);
        proj.setUp(25, 15, target, 0.75,
                cx + perp.x, cy, cz + perp.z);
        worldIn.addFreshEntity(proj);
    }
}
