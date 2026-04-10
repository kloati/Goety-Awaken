package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.RainArrow;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.ArrowRainSpell;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.items.curios.GatlingCharmItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRainSpell.class)
public class ArrowRainSpellMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat,
            CallbackInfo ci) {
        if (caster instanceof Player player && player.isCrouching() && GatlingCharmItem.hasGatlingCharmItem(player)) {
            int potency = spellStat.getPotency();
            int burning = spellStat.getBurning();
            int range = spellStat.getRange();
            float velocity = spellStat.getVelocity();
            Vec3 lookVec = player.getLookAngle();
            Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
            if (WandUtil.enchantedFocus(caster)) {
                potency += WandUtil.getPotencyLevel(caster);
                burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
                range += WandUtil.getRangeLevel(caster);
                velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 2.0F;
            }
            for (int j = 0; j < potency + 1; ++j) {
                RainArrow arrow = new RainArrow(worldIn, caster);
                Vec3 spawnPos = playerPos.add(
                        lookVec.x,
                        0,
                        lookVec.z);
                worldIn.sendParticles(
                        new com.Polarice3.Goety.client.particles.FoggyCloudParticleOption(
                                new ColorUtil(0x4b4b4b), 1.5F, 6),
                        player.getX(), player.getY(), player.getZ(),
                        1, 0, 0, 0, 0);
                arrow.setPos(spawnPos);
                float randomness = 1.0F + worldIn.random.nextFloat() * 0.1F;
                if (worldIn.random.nextFloat() < 0.1F) {
                    randomness = worldIn.random.nextFloat() * 0.5F;
                }
                if (burning > 0) {
                    arrow.setSecondsOnFire(100 * burning);
                }
                arrow.shoot(lookVec.x, lookVec.y, lookVec.z, velocity + (1.5F * worldIn.random.nextFloat()),
                        randomness);
                arrow.setBaseDamage(
                        arrow.getBaseDamage() + com.Polarice3.Goety.config.SpellConfig.ArrowRainExtraDamage.get());
                if (worldIn.addFreshEntity(arrow)) {
                    ((ArrowRainSpell) (Object) this).playSound(worldIn, arrow, SoundEvents.ARROW_SHOOT, 2.0F,
                            1.0F / (worldIn.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                }
            }
            ci.cancel();
        }
    }
}