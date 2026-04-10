package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.entities.ally.illager.EnviokerServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FakeAppointmentEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }

        if (event.getTarget() instanceof EnviokerServant envioker) {
            Player player = event.getEntity();
            InteractionHand hand = event.getHand();
            ItemStack itemstack = player.getItemInHand(hand);
            if (itemstack != null && itemstack.getItem() == ModItems.FAKE_APPOINTMENT.get()
                    && envioker.getTrueOwner() == player
                    && !envioker.hasFakeAppointment()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                envioker.setHasFakeAppointment(true);
                envioker.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Vizier killedVizer) {
            Entity killer = event.getSource().getEntity();
            if (killer instanceof EnviokerServant envioker && envioker.hasFakeAppointment()) {
                convertEnviokerToVizerServant(envioker, killedVizer);
            }
        }
    }

    private static void convertEnviokerToVizerServant(EnviokerServant envioker, Vizier killedVizer) {
        LivingEntity owner = envioker.getTrueOwner();
        if (!(owner instanceof ServerPlayer player)) {
            return;
        }
        Entity newEntity = MobUtil.convertTo(envioker, ModEntityType.VIZIER_SERVANT.get(), true, player);

        if (newEntity instanceof VizierServant vizerServant) {
            copyVizerProperties(vizerServant, envioker);
            vizerServant.setTrueOwner(player);
            if (envioker.level() instanceof ServerLevel serverLevel) {
                vizerServant.finalizeSpawn(serverLevel,
                        serverLevel.getCurrentDifficultyAt(vizerServant.blockPosition()),
                        net.minecraft.world.entity.MobSpawnType.CONVERSION,
                        null, null);
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(envioker, vizerServant);
                grantUsurpationAdvancement(player);
            }
        }
    }

    private static void copyVizerProperties(VizierServant vizerServant, EnviokerServant originalEnvioker) {
        float healthRatio = originalEnvioker.getHealth() / originalEnvioker.getMaxHealth();
        vizerServant.setHealth(vizerServant.getMaxHealth() * healthRatio);
        for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            net.minecraft.world.item.ItemStack itemStack = originalEnvioker.getItemBySlot(slot);
            if (!itemStack.isEmpty()) {
                vizerServant.setItemSlot(slot, itemStack.copy());
            }
        }
        for (int i = 0; i < originalEnvioker.getInventory().getContainerSize()
                && i < vizerServant.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack itemStack = originalEnvioker.getInventory().getItem(i);
            if (!itemStack.isEmpty()) {
                vizerServant.getInventory().setItem(i, itemStack.copy());
            }
        }
    }

    private static void grantUsurpationAdvancement(ServerPlayer player) {
        net.minecraft.advancements.Advancement advancement = player.getServer().getAdvancements().getAdvancement(
                new net.minecraft.resources.ResourceLocation("goetyawaken", "usurpation"));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "envioker_becomes_vizier");
        }
    }
}