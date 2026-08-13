package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ally.SpriteMob;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.storm.SpriteSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.entities.ally.Sprites;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteSpell.class)
public class SpriteSpellMixin {

    @Inject(method = "summonPredicate", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectCustomSummonPredicate(CallbackInfoReturnable<Predicate<LivingEntity>> cir) {
        cir.setReturnValue(livingEntity -> livingEntity instanceof SpriteMob || livingEntity instanceof Sprites);
    }

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCustomSpriteSummon(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat, CallbackInfo ci) {
        if (rightStaff(staff)) {
            return;
        }
        int variant = getVariantByStaff(staff);
        if (variant == -1) {
            return;
        }
        ci.cancel();
        ((SpriteSpell) (Object) this).commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!((SpriteSpell) (Object) this).isShifting(caster)) {
            int i = 2;
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1,
                        -2 + caster.getRandom().nextInt(5));
                Sprites spriteMob = new Sprites(ModEntityType.SPRITES.get(), worldIn);
                spriteMob.setTrueOwner(caster);
                spriteMob.moveTo(blockpos, caster.getYRot(), 0.0F);
                spriteMob.setVariant(variant);
                spriteMob.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                spriteMob.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(blockpos),
                        MobSpawnType.MOB_SUMMONED, null, null);
                ((SpriteSpell) (Object) this).buffSummon(caster, spriteMob, potency);
                ((SpriteSpell) (Object) this).SummonSap(caster, spriteMob);
                ((SpriteSpell) (Object) this).setTarget(caster, spriteMob);
                if (worldIn.addFreshEntity(spriteMob)) {
                    ((SpriteSpell) (Object) this).uponSummon(worldIn, caster, staff, spriteMob);
                }
                ((SpriteSpell) (Object) this).summonAdvancement(caster, spriteMob);
            }
            ((SpriteSpell) (Object) this).SummonDown(caster);
            ((SpriteSpell) (Object) this).playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }

    private int getVariantByStaff(ItemStack staff) {
        if (staff.isEmpty()) {
            return -1;
        }

        if (staff.is(ModItems.FROST_STAFF.get())) {
            return 1;
        } else if (staff.is(ModItems.NETHER_STAFF.get())) {
            return 2;
        } else if (staff.is(ModItems.ABYSS_STAFF.get())) {
            return 3;
        } else if (staff.is(ModItems.NECRO_STAFF.get()) || staff.is(ModItems.NAMELESS_STAFF.get())) {
            return 4;
        } else if (staff.is(ModItems.GEO_STAFF.get())) {
            return 5;
        } else if (staff.is(ModItems.WILD_STAFF.get())) {
            return 6;
        } else if (staff.is(ModItems.VOID_STAFF.get())) {
            return 7;
        }

        return -1;
    }

    private boolean rightStaff(ItemStack staff) {
        if (staff.isEmpty()) {
            return false;
        }
        return staff.is(ModItems.STORM_STAFF.get());
    }
}
