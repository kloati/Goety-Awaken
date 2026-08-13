package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
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

public class ChipFocusSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.CHIP_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.CHIP_FOCUS_CAST_DURATION.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return MYFSounds.dameFortunaChipsStart.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.CHIP_FOCUS_COOLDOWN.get();
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
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int range = spellStat.getRange();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            range += WandUtil.getRangeLevel(caster);
        }
        double extraDamage = potency * Config.CHIP_FOCUS_POTENCY_DAMAGE.get();

        HitResult hitResult = this.rayTrace(worldIn, caster, range, radius);
        Entity targetEntity = null;
        boolean isBlockTarget = false;
        double tx = 0, ty = 0, tz = 0;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            targetEntity = entityHitResult.getEntity();
        } else if (hitResult instanceof BlockHitResult blockHitResult) {
            tx = blockHitResult.getBlockPos().getX() + 0.5;
            ty = blockHitResult.getBlockPos().getY() + 0.5;
            tz = blockHitResult.getBlockPos().getZ() + 0.5;
            isBlockTarget = true;
        } else {
            return;
        }

        int chips = 8;
        float angle = Mth.TWO_PI / chips;
        Vec3 perp = caster.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
        Vec3 offset = perp;
        double cx = caster.getX();
        double cy = caster.getY() + 1.0;
        double cz = caster.getZ();

        if (isBlockTarget) {
            Marker marker = new Marker(EntityType.MARKER, worldIn);
            marker.setPos(tx, ty, tz);
            worldIn.addFreshEntity(marker);
            targetEntity = marker;
        }

        for (int i = 0; i < chips; i++) {
            ModProjectileTargetedEntity proj = new ModProjectileTargetedEntity(worldIn, caster);
            proj.setOwner(caster);
            proj.setPos(cx, cy, cz);
            proj.setExtraDamage((float) extraDamage);
            proj.setUp(15, 15, targetEntity, 1,
                    cx + 2 * offset.x, cy, cz + 2 * offset.z);
            worldIn.addFreshEntity(proj);
            offset = offset.yRot(angle);
        }

        worldIn.playSound(null, cx, cy, cz,
                MYFSounds.dameFortunaChipsStart.get(), caster.getSoundSource(), 2.0F, 1.0F);
    }
}
