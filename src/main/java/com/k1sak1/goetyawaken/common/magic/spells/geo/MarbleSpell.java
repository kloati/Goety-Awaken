package com.k1sak1.goetyawaken.common.magic.spells.geo;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class MarbleSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(16);
    }

    @Override
    public int defaultSoulCost() {
        return 51;
    }

    @Override
    public int defaultCastDuration() {
        return 51;
    }

    @Override
    public int defaultSpellCooldown() {
        return 52;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.WATER_AMBIENT;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
        }
        HitResult hitResult = this.rayTrace(worldIn, caster, range, 1.0F);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            this.PlaceMarble(worldIn, caster, blockHitResult, range);
        }
    }

    private void PlaceMarble(ServerLevel world, LivingEntity caster, BlockHitResult hitResult,
            int range) {
        BlockPos targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        if (world.isEmptyBlock(targetPos)) {
            BlockState blockState;
            if (world.random.nextFloat() < 0.1F) {
                blockState = net.minecraft.world.level.block.Blocks.WATER.defaultBlockState();
            } else {
                blockState = ModBlocks.SILT_MARBLE_HEAVY_BLOCK.get().defaultBlockState();
            }
            world.setBlock(targetPos, blockState, 3);
            world.playSound(null, targetPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F,
                    1.0F);
            for (int i = 0; i < 20; i++) {
                double dx = world.random.nextGaussian() * 0.5D;
                double dy = world.random.nextGaussian() * 0.5D;
                double dz = world.random.nextGaussian() * 0.5D;
                world.sendParticles(net.minecraft.core.particles.ParticleTypes.FALLING_WATER,
                        targetPos.getX() + 0.5D + dx * 2,
                        targetPos.getY() + 0.5D + dy * 2,
                        targetPos.getZ() + 0.5D + dz * 2,
                        1, dx * 0.5, dy * 0.5, dz * 0.5, 0.1D);
            }
        }
    }
}
