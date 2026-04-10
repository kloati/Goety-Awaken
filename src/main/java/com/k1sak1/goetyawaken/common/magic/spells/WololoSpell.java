package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.EntityHitResult;
import java.util.ArrayList;
import java.util.List;

public class WololoSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.WOLOLO_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.WOLOLO_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.WOLOLO_FOCUS_COOLDOWN.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return net.minecraft.sounds.SoundEvents.EVOKER_PREPARE_WOLOLO;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        double radius = spellStat.getRadius();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        if (radius > 0) {
            boolean isSneaking = (caster instanceof Player player) && player.isShiftKeyDown();
            for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class,
                    caster.getBoundingBox().inflate(8.0D * radius))) {
                if (livingEntity instanceof Sheep sheep) {
                    changeSheepColor(sheep, isSneaking);
                    worldIn.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            sheep.getX(), sheep.getY() + sheep.getBbHeight() / 2, sheep.getZ(),
                            5, 0.5, 0.5, 0.5, 0.0);
                }
            }
        }
        int effectiveRange = 4 + (range * 4);
        EntityHitResult entityHitResult = this.entityResult(worldIn, caster, effectiveRange, 0.5);
        if (entityHitResult != null && entityHitResult.getEntity() instanceof Sheep targetSheep) {
            boolean isSneaking = (caster instanceof Player player) && player.isShiftKeyDown();
            changeSheepColor(targetSheep, isSneaking);
            worldIn.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    targetSheep.getX(), targetSheep.getY() + targetSheep.getBbHeight() / 2, targetSheep.getZ(),
                    5, 0.5, 0.5, 0.5, 0.0);
        }
        this.playSound(worldIn, caster, 1.0F, 1.0F);
    }

    private void changeSheepColor(Sheep sheep, boolean isSneaking) {
        if (sheep.isSheared() || !sheep.getColor().equals(DyeColor.WHITE)) {
            sheep.setColor(DyeColor.WHITE);
        }
        if (isSneaking) {
            sheep.setColor(DyeColor.BLUE);
        } else {
            sheep.setColor(DyeColor.RED);
        }
    }
}