package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ThreatBanner extends ReviveServantItem {

    public ThreatBanner() {
        super(new Properties()
                .stacksTo(1)
                .setNoRepair()
                .rarity(net.minecraft.world.item.Rarity.UNCOMMON));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player,
            @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        Level level = player.getCommandSenderWorld();
        if (!(target instanceof com.Polarice3.Goety.common.entities.ally.illager.MountaineerServant mountaineerServant)) {
            return super.interactLivingEntity(stack, player, target, hand);
        }

        if (mountaineerServant.getTrueOwner() != player) {
            return super.interactLivingEntity(stack, player, target, hand);
        }

        Entity entity = getSummon(stack, level);

        if (entity != null) {
            if (entity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.RampartCaptain rampartCaptain) {
                if (rampartCaptain.getTrueOwner() == player) {
                    if (!player.level().isClientSide) {
                        java.util.Map<EquipmentSlot, ItemStack> savedEquipment = new java.util.EnumMap<>(
                                EquipmentSlot.class);
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            ItemStack equipment = mountaineerServant.getItemBySlot(slot);
                            if (!equipment.isEmpty()) {
                                savedEquipment.put(slot, equipment.copy());
                            }
                        }

                        rampartCaptain.setHealth(rampartCaptain.getMaxHealth());
                        rampartCaptain.setPos(target.getX(), target.getY(), target.getZ());
                        rampartCaptain.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,
                                player.position());
                        if (level.addFreshEntity(rampartCaptain)) {
                            for (java.util.Map.Entry<EquipmentSlot, ItemStack> entry : savedEquipment.entrySet()) {
                                EquipmentSlot slot = entry.getKey();
                                ItemStack equipment = entry.getValue();
                                if (slot == EquipmentSlot.MAINHAND) {
                                    if (!isUnenchantedIronIceAxe(equipment)) {
                                        rampartCaptain.setItemSlot(slot, equipment);
                                    }
                                } else {
                                    rampartCaptain.setItemSlot(slot, equipment);
                                }
                            }

                            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(
                                            serverLevel, net.minecraft.core.particles.ParticleTypes.CLOUD,
                                            rampartCaptain);
                                    com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(
                                            serverLevel, net.minecraft.core.particles.ParticleTypes.MYCELIUM,
                                            rampartCaptain);
                                }
                            }
                            rampartCaptain.playSound(com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_MUMBLE.get(),
                                    1.0F, 1.0F);
                            target.discard();
                            player.swing(hand);
                            if (!player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }
                        }

                        return InteractionResult.CONSUME;
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }

    private boolean isUnenchantedIronIceAxe(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof com.Polarice3.Goety.common.items.equipment.IceAxeItem iceAxe) {
            if (iceAxe.getTier() == net.minecraft.world.item.Tiers.IRON) {
                return !stack.isEnchanted();
            }
        }
        return false;
    }
}
