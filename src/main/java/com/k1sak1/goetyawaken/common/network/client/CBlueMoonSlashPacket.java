// package com.k1sak1.goetyawaken.common.network.client;

// import com.k1sak1.goetyawaken.common.compat.tetra.BlueMoonEffect;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.server.level.ServerLevel;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.sounds.SoundSource;
// import net.minecraft.world.entity.ai.attributes.Attributes;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.phys.Vec3;
// import net.minecraftforge.network.NetworkEvent;

// import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
// import com.Polarice3.Goety.init.ModSounds;
// import com.Polarice3.Goety.utils.MathHelper;

// import java.util.function.Supplier;

// public class CBlueMoonSlashPacket {

// public static void encode(CBlueMoonSlashPacket packet, FriendlyByteBuf
// buffer) {
// }

// public static CBlueMoonSlashPacket decode(FriendlyByteBuf buffer) {
// return new CBlueMoonSlashPacket();
// }

// public static void consume(CBlueMoonSlashPacket packet,
// Supplier<NetworkEvent.Context> ctx) {
// ctx.get().enqueueWork(() -> {
// ServerPlayer playerEntity = ctx.get().getSender();

// if (playerEntity != null && !playerEntity.isSpectator()) {
// fireVoidSlashOnServer(playerEntity.serverLevel(), playerEntity);
// }
// });
// ctx.get().setPacketHandled(true);
// }

// public static void fireVoidSlashOnServer(ServerLevel level, Player player) {

// if (level.isClientSide) {
// return;
// }

// if (player.getAttackStrengthScale(0.5f) <= 0.9f) {
// return;
// }

// level.playSound(null, player.getX(), player.getY(), player.getZ(),
// ModSounds.OBSIDIAN_CLAYMORE_SWING.get(),
// SoundSource.PLAYERS, 2.0F, player.getVoicePitch());

// ItemStack itemStack = player.getMainHandItem();

// if (!(itemStack.getItem() instanceof
// se.mickelus.tetra.items.modular.ModularItem modularItem)) {
// return;
// }

// int level2 = modularItem.getEffectLevel(itemStack, BlueMoonEffect.BLUE_MOON);
// if (level2 <= 0) {
// return;
// }

// float damageMultiplier = 1.0f + (level2 - 1) * 0.5f;

// float baseDamage = (float)
// player.getAttributeValue(Attributes.ATTACK_DAMAGE);

// float slashDamage = baseDamage * 0.5f * damageMultiplier;

// float efficiency = modularItem.getEffectEfficiency(itemStack,
// BlueMoonEffect.BLUE_MOON);
// float radiusBonus = (efficiency - 1.0f) * 0.3f;

// Vec3 viewVector = player.getViewVector(1.0F);

// VoidSlash slash = new VoidSlash(level, player);
// slash.setPos(
// player.getX() + viewVector.x / 2,
// player.getEyeY() - 0.2,
// player.getZ() + viewVector.z / 2);

// slash.setDamage(slashDamage);
// slash.setMaxLifeSpan(MathHelper.secondsToTicks(1.5F));
// slash.setRadius(slash.getRadius() + radiusBonus);
// slash.setMaxRadius(slash.getMaxRadius() + radiusBonus);
// slash.slash(viewVector, 0.7F);
// slash.setVoidLevel(Math.min(level2, 3));

// level.addFreshEntity(slash);
// }
// }
