package com.k1sak1.goetyawaken.common.magic.spells;

import com.k1sak1.goetyawaken.common.compat.touhoulittlemaid.TouhouLittleMaidLoaded;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.utils.WandUtil;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

public class FairyFocusSpell extends com.Polarice3.Goety.common.magic.SummonSpell {

    @Override
    public int defaultSoulCost() {
        return Config.FAIRY_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.FAIRY_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.FAIRY_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public int SummonDownDuration() {
        return Config.fairyFocusCooldown;
    }

    @Override
    public java.util.List<Enchantment> acceptedEnchantments() {
        java.util.List<Enchantment> list = new java.util.ArrayList<>();
        list.add(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get());
        list.add(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public java.util.function.Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof MaidFairyServant;
    }

    @Override
    public int summonLimit() {
        return Config.maidFairyServantLimit;
    }

    @Override
    public boolean conditionsMet(net.minecraft.server.level.ServerLevel worldIn, LivingEntity caster) {
        if (!TouhouLittleMaidLoaded.TOUHOULITTLEMAID.isLoaded()) {
            return false;
        }
        return super.conditionsMet(worldIn, caster);
    }

    @Override
    public void SpellResult(net.minecraft.server.level.ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(),
                    caster);
            duration += WandUtil.getLevels(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(),
                    caster) + 1;
        }
        if (!isShifting(caster)) {
            boolean isDarkWand = false;
            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(staff.getItem());
            if (registryName != null) {
                isDarkWand = registryName.getPath().equals("dark_wand");
            }

            int fairyCount = 1;
            if (!isDarkWand) {
                fairyCount = 2;
            }

            for (int i = 0; i < fairyCount; ++i) {
                MaidFairyServant summonedentity = new MaidFairyServant(ModEntityType.MAID_FAIRY_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, summonedentity, potency);
                if (potency > 0) {
                    summonedentity.addEffect(new MobEffectInstance(
                            ModEffects.ENCHANTMENT_SHARPNESS.get(),
                            MobEffectInstance.INFINITE_DURATION,
                            potency - 1, false, false, false));
                }
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(caster, summonedentity);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, com.Polarice3.Goety.init.ModSounds.SUMMON_SPELL.get());
        }
    }
}