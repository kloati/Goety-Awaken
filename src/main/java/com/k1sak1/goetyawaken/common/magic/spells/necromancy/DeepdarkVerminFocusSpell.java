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
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkCentipedeServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkLeechServant;
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

public class DeepdarkVerminFocusSpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return Config.DEEPDARK_VERMIN_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.DEEPDARK_VERMIN_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.DEEPDARK_VERMIN_FOCUS_COOLDOWN.get();
    }

    @Override
    public int SummonDownDuration() {
        return Config.deepdarkVerminFocusCooldown;
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
        return livingEntity -> livingEntity instanceof SculkCentipedeServant
                || livingEntity instanceof SculkLeechServant;
    }

    @Override
    public int summonLimit() {
        return Math.max(Config.SCULK_CENTIPEDE_SERVANT_LIMIT.get(), Config.SCULK_LEECH_SERVANT_LIMIT.get());
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

            int centipedeCount = 1;
            int leechCount = 2 + worldIn.random.nextInt(2);

            if (rightStaff(staff)) {
                centipedeCount += 1;
                leechCount += 1;
            }
            if (staff.is(ModItems.NAMELESS_STAFF.get())) {
                centipedeCount += 2;
                leechCount += 3;
            }

            for (int i = 0; i < centipedeCount; ++i) {
                SculkCentipedeServant servant = new SculkCentipedeServant(
                        ModIntegrationRegistry.SCULK_CENTIPEDE_SERVANT.get(), worldIn);
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

            for (int i = 0; i < leechCount; ++i) {
                SculkLeechServant servant = new SculkLeechServant(
                        ModIntegrationRegistry.SCULK_LEECH_SERVANT.get(), worldIn);
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
