package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.PoisonQuill;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wild.PoisonDartSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.api.PoisonQuillAccessor;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PoisonDartSpell.class, remap = false)
public class PoisonDartSpellMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat, CallbackInfo ci) {
        if (!staff.is(ModItems.POTATO_STAFF.get())) {
            return;
        }
        ci.cancel();

        PoisonDartSpell spell = (PoisonDartSpell) (Object) this;

        float velocity = spellStat.getVelocity();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        PoisonQuill poisonQuill = new PoisonQuill(worldIn, caster);
        ((PoisonQuillAccessor) poisonQuill).goetyawaken$setPoisonPotatoMode(true);
        poisonQuill.setSpear(spell.rightStaff(staff), potency + 1);
        poisonQuill.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        poisonQuill.setOwner(caster);
        poisonQuill.setExtraDamage(potency);
        poisonQuill.setDuration(duration);
        if (caster.isUnderWater()) {
            poisonQuill.setAqua(true);
        }
        worldIn.addFreshEntity(poisonQuill);
        spell.playSound(worldIn, caster, ModSounds.POISON_QUILL_VINE_SHOOT.get(), 1.0F,
                spell.projPitch(worldIn.getRandom()));
    }
}
