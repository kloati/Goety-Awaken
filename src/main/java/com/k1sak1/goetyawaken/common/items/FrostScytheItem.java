package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.ModTiers;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.network.client.CFrostScytheStrikePacket;
import com.k1sak1.goetyawaken.common.entities.projectiles.FrostScytheSlash;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.Polarice3.Goety.utils.ModUUIDUtil;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class FrostScytheItem extends DarkScytheItem implements ISoulRepair {
    private static float initialDamage = (float) Config.frostScytheDamage;

    public FrostScytheItem() {
        super(ModTiers.SPECIAL);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "FrostScythe modifier",
                            (float) Config.frostScytheDamage - 1.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "FrostScythe modifier",
                            (float) Config.frostScytheAttackSpeed - 4.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(
                    ModUUIDUtil.createUUID("item.goety.scythe.reach"),
                    "Tool modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof FrostScytheItem) {
            ModNetwork.channel.send(PacketDistributor.SERVER.noArg(), new CFrostScytheStrikePacket());
        }
    }

    public static void entityClick(Player player, Level world) {
        if (player.getMainHandItem().getItem() instanceof FrostScytheItem) {
            if (!player.level().isClientSide && !player.isSpectator()) {
                strike(world, player);
            }
        }
    }

    public static float getInitialDamage() {
        return (float) Config.frostScytheDamage;
    }

    public static void strike(Level pLevel, Player pPlayer) {
        if (pPlayer.getAttackStrengthScale(0.5F) > 0.9F) {
            pLevel.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 2.0F,
                    0.4F / (pLevel.random.nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                Vec3 vector3d = pPlayer.getViewVector(1.0F);
                FrostScytheSlash frostScytheSlash = new FrostScytheSlash(pPlayer.getMainHandItem(),
                        pLevel,
                        pPlayer.getX() + vector3d.x / 2,
                        pPlayer.getEyeY() - 0.2,
                        pPlayer.getZ() + vector3d.z / 2,
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                frostScytheSlash.setOwner(pPlayer);
                frostScytheSlash.setDamage(getInitialDamage());
                frostScytheSlash.setTotalLife(300);
                pLevel.addFreshEntity(frostScytheSlash);
            }
        }
    }
}