package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.Polarice3.Goety.common.entities.projectiles.PoisonBolt;
import com.Polarice3.Goety.common.entities.projectiles.SoulBolt;
import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.common.entities.projectiles.WitherBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.SoulBoltSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.api.PoisonBoltAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoulBoltSpell.class, remap = false)
public class SoulBoltSpellMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat, CallbackInfo ci) {
        if (!staff.is(com.k1sak1.goetyawaken.common.items.ModItems.POTATO_STAFF.get())) {
            return;
        }
        ci.cancel();

        SoulBoltSpell spell = (SoulBoltSpell) (Object) this;

        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        Vec3 vector3d = caster.getViewVector(1.0F);
        SpellHurtingProjectile soulBolt = new SoulBolt(
                caster.getX() + vector3d.x / 2,
                caster.getEyeY() - 0.2,
                caster.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z, worldIn);
        if (spell.typeStaff(staff, SpellType.WILD)) {
            soulBolt = new PoisonBolt(
                    caster.getX() + vector3d.x / 2,
                    caster.getEyeY() - 0.2,
                    caster.getZ() + vector3d.z / 2,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z, worldIn);
            ((PoisonBoltAccessor) soulBolt).goetyawaken$setPoisonPotatoMode(true);
            SoundUtil.playSoulBolt(caster);
        } else if (spell.typeStaff(staff, SpellType.NETHER) && CuriosFinder.hasNetherSet(caster)) {
            soulBolt = new WitherBolt(
                    caster.getX() + vector3d.x / 2,
                    caster.getEyeY() - 0.2,
                    caster.getZ() + vector3d.z / 2,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z, worldIn);
            spell.playSound(worldIn, caster, SoundEvents.WITHER_SHOOT, 0.5F, 0.25F);
            spell.playSound(worldIn, caster, ModSounds.HELL_BOLT_SHOOT.get());
        } else if (staff.is(ModItems.NAMELESS_STAFF.get())) {
            soulBolt = new NecroBolt(
                    caster.getX() + vector3d.x / 2,
                    caster.getEyeY() - 0.2,
                    caster.getZ() + vector3d.z / 2,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z, worldIn);
            SoundUtil.playNecroBolt(caster);
        } else {
            SoundUtil.playSoulBolt(caster);
        }
        if (soulBolt instanceof SoulBolt soulBolt1) {
            soulBolt1.setNecro(spell.typeStaff(staff, SpellType.NECROMANCY));
        }
        soulBolt.setExtraDamage(potency);
        soulBolt.setBoltSpeed((int) velocity);
        soulBolt.setOwner(caster);
        worldIn.addFreshEntity(soulBolt);
    }
}
