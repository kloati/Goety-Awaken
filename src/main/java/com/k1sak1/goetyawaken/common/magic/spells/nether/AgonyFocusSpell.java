package com.k1sak1.goetyawaken.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AgonyFocusSpell extends ChargingSpell {

    private static final ThreadLocal<Integer> currentSummonedCount = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> maxShotsForCurrentCast = ThreadLocal.withInitial(() -> 0);

    @Override
    public int defaultSoulCost() {
        return Config.AGONY_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastUp() {
        return Config.AGONY_FOCUS_CAST_UP.get();
    }

    @Override
    public int Cooldown() {
        return Config.AGONY_FOCUS_COOLDOWN.get();
    }

    @Override
    public int Cooldown(LivingEntity caster, ItemStack staff, int shots) {
        boolean hasUnholySet = CuriosFinder.hasUnholySet(caster);
        boolean isRightStaff = rightStaff(staff);
        if (hasUnholySet && isRightStaff) {
            return 10;
        } else if (isRightStaff) {
            return 30;
        } else {
            return 50;
        }
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.AGONY_FOCUS_COOLDOWN.get();
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
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        return 5;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        summonDamned(worldIn, caster, staff, spellStat);
    }

    private void summonDamned(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        Damned damned = new Damned(ModEntityType.DAMNED.get(), caster.level());
        BlockPos blockPos0 = caster.blockPosition().offset(
                worldIn.random.nextIntBetweenInclusive(-3, 3), 0,
                worldIn.random.nextIntBetweenInclusive(-3, 3));
        BlockPos blockPos = BlockFinder.SummonPosition(caster, blockPos0);
        damned.moveTo(blockPos.below(2), caster.getYHeadRot(), caster.getXRot());
        damned.setTrueOwner(caster);
        damned.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(blockPos.below(2)),
                MobSpawnType.MOB_SUMMONED, null, null);
        float potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
        }
        if (potency > 0) {
            AttributeInstance attackDamage = damned.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {
                attackDamage.addPermanentModifier(new AttributeModifier(
                        UUID.randomUUID(), "Potency enchantment",
                        potency * Config.agonyFocusPotencyDamage * WandUtil.damageMultiply(),
                        AttributeModifier.Operation.ADDITION));
            }
        }
        damned.setLimitedLife(200);
        ServerParticleUtil.addParticlesAroundSelf(worldIn, ModParticleTypes.BIG_FIRE.get(), damned);
        worldIn.addFreshEntity(damned);
    }
}