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

public class WraithNecromancerSoulJar extends SoulJar {
    public static final String TAG_WRAITH = "Wraith";

    public WraithNecromancerSoulJar() {
        super();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            LivingEntity livingEntity = getNecromancer(stack, worldIn);
            if (livingEntity != null) {
                if (livingEntity.getType() == ModEntityType.WRAITH_NECROMANCER_SERVANT.get()) {
                    if (!isWraith(stack)) {
                        setWraith(stack);
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
            if (isWraith(stack)) {
                if (target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get() ||
                        target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.MUCK_WRAITH_SERVANT.get()
                        ||
                        target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.MUCK_WRAITH.get()
                        ||
                        target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH_SERVANT
                                .get()
                        ||
                        target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH
                                .get()
                        ||
                        target.getType() == com.Polarice3.Goety.common.entities.ModEntityType.WRAITH.get()) {

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant wraithNecromancer = (com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant) getNecromancer(
                            stack, level);
                    if (wraithNecromancer == null) {
                        wraithNecromancer = new com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant(
                                ModEntityType.WRAITH_NECROMANCER_SERVANT.get(), level);
                        wraithNecromancer.setConfigurableAttributes();
                        wraithNecromancer.setNecroLevel(0);
                        wraithNecromancer
                                .addSummon(com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get());
                        wraithNecromancer
                                .addSummon(com.Polarice3.Goety.common.entities.ModEntityType.MUCK_WRAITH_SERVANT.get());
                        wraithNecromancer.addSummon(
                                com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH_SERVANT.get());
                        wraithNecromancer
                                .addSummon(com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get());
                        if (stack.hasTag()) {
                            CompoundTag tag = stack.getTag();
                            if (tag.getBoolean("HasZombieServants")) {
                                wraithNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.ZOMBIE_SERVANT.get());
                            }
                            if (tag.getBoolean("HasSkeletonServants")) {
                                wraithNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.SKELETON_SERVANT.get());
                            }
                            if (tag.getBoolean("HasVanguardServants")) {
                                wraithNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get());
                            }
                            if (tag.getBoolean("HasBlackguardServants")) {
                                wraithNecromancer.addSummon(
                                        com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get());
                            }
                        }
                    }

                    wraithNecromancer.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(),
                            target.getXRot());
                    wraithNecromancer.setTrueOwner(player);
                    wraithNecromancer.setPersistenceRequired();
                    wraithNecromancer.setHealth(wraithNecromancer.getMaxHealth());
                    if (!level.isClientSide) {
                        level.addFreshEntity(wraithNecromancer);
                        if (level instanceof ServerLevel particleLevel) {
                            for (int i = 0; i < 8; ++i) {
                                com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(particleLevel,
                                        net.minecraft.core.particles.ParticleTypes.SCULK_SOUL, wraithNecromancer);
                                com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(particleLevel,
                                        net.minecraft.core.particles.ParticleTypes.POOF, wraithNecromancer);
                            }
                        }
                        wraithNecromancer.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                        wraithNecromancer.playSound(com.Polarice3.Goety.init.ModSounds.NECROMANCER_LAUGH.get(), 2.0F,
                                0.5F);
                        target.discard();
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);

    }

    public static boolean isWraith(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return stack.getItem() instanceof WraithNecromancerSoulJar && compoundtag != null
                && compoundtag.contains(TAG_WRAITH);
    }

    public static void setWraith(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean(TAG_WRAITH, true);
    }
}