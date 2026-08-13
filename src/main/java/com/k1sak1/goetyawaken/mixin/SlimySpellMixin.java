package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wild.SlimySpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SludgeServant;
import com.k1sak1.goetyawaken.utils.annotation.RequiresModPresent;
import com.kyanite.deeperdarker.world.otherside.OthersideDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@RequiresModPresent("deeperdarker")
@Mixin(SlimySpell.class)
public class SlimySpellMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat,
            CallbackInfo ci) {
        SlimySpell spell = (SlimySpell) (Object) this;

        if (spell.specialStaffs(staff)) {
            return;
        }

        BlockPos blockPos = caster.blockPosition();
        boolean isSculkBiome = worldIn.getBiome(blockPos).is(Biomes.DEEP_DARK)
                || isOthersideDimension(worldIn);

        if (!isSculkBiome) {
            return;
        }
        ci.cancel();
        spell.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!spell.isShifting(caster)) {
            int i = 1;
            if (spell.rightStaff(staff)) {
                i = 2 + worldIn.random.nextInt(2);
            }
            for (int i1 = 0; i1 < i; ++i1) {
                SludgeServant sludgeServant = new SludgeServant(ModIntegrationRegistry.SLUDGE_SERVANT.get(), worldIn);
                BlockPos summonPos = BlockFinder.SummonRadius(caster.blockPosition(), sludgeServant, worldIn);
                sludgeServant.setTrueOwner(caster);
                sludgeServant.moveTo(summonPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(sludgeServant);
                sludgeServant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                sludgeServant.setPersistenceRequired();
                sludgeServant.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                sludgeServant.setSize(2, true);
                spell.buffSummon(caster, sludgeServant, potency);
                spell.SummonSap(caster, sludgeServant);
                spell.setTarget(caster, sludgeServant);
                if (worldIn.addFreshEntity(sludgeServant)) {
                    spell.uponSummon(worldIn, caster, staff, sludgeServant);
                }
                spell.summonAdvancement(caster, sludgeServant);
            }
            spell.SummonDown(caster);
            spell.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }

    private static boolean isOthersideDimension(ServerLevel level) {
        try {
            return level.dimension() == OthersideDimension.OTHERSIDE_LEVEL;
        } catch (NoClassDefFoundError | NoSuchFieldError e) {
            return false;
        }
    }
}
