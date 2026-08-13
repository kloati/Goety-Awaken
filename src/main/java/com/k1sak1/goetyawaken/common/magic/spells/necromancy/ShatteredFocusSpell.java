package com.k1sak1.goetyawaken.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.compat.ModLoadedUtil;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShatteredServant;
import com.kyanite.deeperdarker.world.otherside.OthersideDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ShatteredFocusSpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return Config.SHATTERED_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.SHATTERED_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.SHATTERED_FOCUS_COOLDOWN.get();
    }

    @Override
    public int SummonDownDuration() {
        return Config.shatteredFocusCooldown;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof ShatteredServant;
    }

    @Override
    public int summonLimit() {
        return Config.SHATTERED_SERVANT_LIMIT.get();
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        if (!ModLoadedUtil.isModLoaded(ModLoadedUtil.DEEPER_DARKER)) {
            return false;
        }
        return super.conditionsMet(worldIn, caster);
    }

    private boolean isSculkBiome(ServerLevel worldIn, LivingEntity caster) {
        BlockPos blockPos = caster.blockPosition();
        return worldIn.getBiome(blockPos).is(Biomes.DEEP_DARK)
                || worldIn.dimension() == OthersideDimension.OTHERSIDE_LEVEL;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!isShifting(caster)) {
            boolean upgraded = isSculkBiome(worldIn, caster);

            int shatteredCount = 1;

            if (rightStaff(staff)) {
                shatteredCount += 1;
            }
            if (staff.is(ModItems.NAMELESS_STAFF.get())) {
                shatteredCount += 2;
            }

            for (int i = 0; i < shatteredCount; ++i) {
                ShatteredServant servant = new ShatteredServant(
                        ModIntegrationRegistry.SHATTERED_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), servant, worldIn);
                servant.setTrueOwner(caster);
                servant.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(servant);
                if (!CuriosFinder.hasNecroCrown(caster)) {
                    servant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                }
                servant.setPersistenceRequired();
                servant.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                if (upgraded) {
                    servant.setUpgraded(true);
                }
                this.buffSummon(caster, servant, potency);
                this.SummonSap(caster, servant);
                this.setTarget(caster, servant);
                if (worldIn.addFreshEntity(servant)) {
                    this.uponSummon(worldIn, caster, staff, servant);
                }
                this.summonAdvancement(caster, servant);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
