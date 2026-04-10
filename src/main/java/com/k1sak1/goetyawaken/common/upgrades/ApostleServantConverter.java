package com.k1sak1.goetyawaken.common.upgrades;

import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ApostleServantConverter {

    public static void convertToApostle(LivingEntity servant, int titleNumber) {
        if (!(servant.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 position = servant.position();
        float yRot = servant.getYRot();
        float xRot = servant.getXRot();
        Object owner = null;
        if (servant instanceof com.Polarice3.Goety.common.entities.neutral.Owned owned) {
            owner = owned.getTrueOwner();
        }
        ItemStack[] armorEquipment = new ItemStack[4];
        if (servant instanceof net.minecraft.world.entity.Mob mob) {
            if (!mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                mob.spawnAtLocation(mob.getItemBySlot(EquipmentSlot.MAINHAND));
                mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            if (!mob.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
                mob.spawnAtLocation(mob.getItemBySlot(EquipmentSlot.OFFHAND));
                mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            armorEquipment[0] = mob.getItemBySlot(EquipmentSlot.HEAD).copy();
            armorEquipment[1] = mob.getItemBySlot(EquipmentSlot.CHEST).copy();
            armorEquipment[2] = mob.getItemBySlot(EquipmentSlot.LEGS).copy();
            armorEquipment[3] = mob.getItemBySlot(EquipmentSlot.FEET).copy();
        }
        float currentHealth = servant.getHealth();
        float maxHealth = (float) servant.getAttributeValue(Attributes.MAX_HEALTH);
        ApostleServant apostle = new ApostleServant(
                com.k1sak1.goetyawaken.common.entities.ModEntityType.APOSTLE_SERVANT.get(), serverLevel);
        apostle.moveTo(position.x, position.y, position.z, yRot, xRot);
        if (owner instanceof net.minecraft.world.entity.player.Player player) {
            apostle.setTrueOwner(player);
        }
        apostle.setTitleNumber(titleNumber);
        apostle.TitleEffect(titleNumber);
        apostle.setApostleUpgraded(true);
        float healthRatio = currentHealth / maxHealth;
        apostle.setHealth(apostle.getMaxHealth() * healthRatio);
        if (apostle instanceof net.minecraft.world.entity.Mob) {
            net.minecraft.world.entity.Mob apostleMob = (net.minecraft.world.entity.Mob) apostle;
            apostleMob.setItemSlot(EquipmentSlot.HEAD, armorEquipment[0]);
            apostleMob.setItemSlot(EquipmentSlot.CHEST, armorEquipment[1]);
            apostleMob.setItemSlot(EquipmentSlot.LEGS, armorEquipment[2]);
            apostleMob.setItemSlot(EquipmentSlot.FEET, armorEquipment[3]);
        }

        int random = serverLevel.getRandom().nextInt(18);
        Component nameComponent = net.minecraft.network.chat.Component.translatable("name.goety.apostle." + random);
        String trimmedTitle = net.minecraft.network.chat.Component.translatable("title.goety." + titleNumber)
                .getString().trim();
        Component titleComponent = net.minecraft.network.chat.Component.literal(trimmedTitle);
        apostle.setCustomName(net.minecraft.network.chat.Component
                .translatable(nameComponent.getString() + " " + titleComponent.getString()));
        apostle.setCustomNameVisible(true);
        serverLevel.addFreshEntity(apostle);
        String apostleName = apostle.getCustomName() != null ? apostle.getCustomName().getString()
                : apostle.getName().getString();
        if (serverLevel.getServer() != null) {
            Component messageComponent = net.minecraft.network.chat.Component.translatable(
                    "message.goetyawaken.apostle.completed_challenge",
                    apostleName,
                    net.minecraft.network.chat.Component.literal("[")
                            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE)
                            .append(titleComponent.copy().withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE,
                                    net.minecraft.ChatFormatting.BOLD))
                            .append(net.minecraft.network.chat.Component.literal("]")
                                    .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE)));
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(messageComponent, false);
        }

        if (owner instanceof net.minecraft.world.entity.player.Player player) {
            Component messageComponent = net.minecraft.network.chat.Component.translatable(
                    "message.goetyawaken.apostle.completed_challenge",
                    apostleName,
                    net.minecraft.network.chat.Component.literal("[")
                            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE)
                            .append(titleComponent.copy().withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE,
                                    net.minecraft.ChatFormatting.BOLD))
                            .append(net.minecraft.network.chat.Component.literal("]")
                                    .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE)))
                    .withStyle(net.minecraft.ChatFormatting.GOLD);
            player.displayClientMessage(messageComponent, true);

            serverLevel.playSound(null, apostle.getX(), apostle.getY(), apostle.getZ(),
                    net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, 1.0F);
        }

        servant.discard();
    }
}