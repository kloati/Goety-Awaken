package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.common.items.revive.SoulJar;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class ParchedNecromancerSoulJar extends SoulJar {
    public static final String TAG_PARCHED = "Parched";

    public ParchedNecromancerSoulJar() {
        super();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            LivingEntity livingEntity = getNecromancer(stack, worldIn);
            if (livingEntity != null) {
                if (livingEntity.getType() == ModEntityType.PARCHED_NECROMANCER_SERVANT.get()) {
                    if (!isParched(stack)) {
                        setParched(stack);
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
            InteractionHand hand) {
        Level level = player.getCommandSenderWorld();
        if (level instanceof ServerLevel serverLevel) {
            if (isParched(stack)) {
                if (target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.ZOMBIE_SERVANT.get() ||
                        target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.HUSK_SERVANT.get()
                        ||
                        target.getType() == com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT
                                .get()
                        ||
                        target.getType() == com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED
                                .get()) {

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.ParchedNecromancerServant parchedNecromancer = (com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.ParchedNecromancerServant) getNecromancer(
                            stack, level);
                    if (parchedNecromancer == null) {
                        parchedNecromancer = new com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.ParchedNecromancerServant(
                                ModEntityType.PARCHED_NECROMANCER_SERVANT.get(), level);
                        parchedNecromancer.setConfigurableAttributes();
                        parchedNecromancer.setNecroLevel(0);
                        parchedNecromancer
                                .addSummon(com.Polarice3.Goety.common.entities.ModEntityType.HUSK_SERVANT.get());
                        parchedNecromancer
                                .addSummon(com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT.get());
                        parchedNecromancer
                                .addSummon(com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get());
                        parchedNecromancer
                                .addSummon(com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get());

                        if (stack.hasTag()) {
                            CompoundTag tag = stack.getTag();
                            if (tag.getBoolean("HasHuskServants")) {
                                parchedNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.HUSK_SERVANT.get());
                            }
                            if (tag.getBoolean("HasParchedServants")) {
                                parchedNecromancer.addSummon(
                                        com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT.get());
                            }
                            if (tag.getBoolean("HasVanguardServants")) {
                                parchedNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get());
                            }
                            if (tag.getBoolean("HasBlackguardServants")) {
                                parchedNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get());
                            }
                            if (tag.getBoolean("HasWraithServants")) {
                                parchedNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get());
                            }
                            if (tag.getBoolean("HasReaperServants")) {
                                parchedNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get());
                            }
                            if (tag.getBoolean("HasPhantomServants")) {
                                parchedNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get());
                            }
                        }
                    }

                    parchedNecromancer.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(),
                            target.getXRot());
                    parchedNecromancer.setTrueOwner(player);
                    parchedNecromancer.setPersistenceRequired();
                    parchedNecromancer.setHealth(parchedNecromancer.getMaxHealth());
                    if (!level.isClientSide) {
                        level.addFreshEntity(parchedNecromancer);
                        if (level instanceof ServerLevel particleLevel) {
                            for (int i = 0; i < 8; ++i) {
                                com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(particleLevel,
                                        net.minecraft.core.particles.ParticleTypes.SCULK_SOUL, parchedNecromancer);
                                com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(particleLevel,
                                        net.minecraft.core.particles.ParticleTypes.POOF, parchedNecromancer);
                            }
                        }
                        parchedNecromancer.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                        parchedNecromancer.playSound(com.Polarice3.Goety.init.ModSounds.NECROMANCER_LAUGH.get(), 2.0F,
                                0.5F);
                        target.discard();
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);

    }

    public static boolean isParched(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return stack.getItem() instanceof ParchedNecromancerSoulJar && compoundtag != null
                && compoundtag.contains(TAG_PARCHED);
    }

    public static void setParched(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean(TAG_PARCHED, true);
    }
}