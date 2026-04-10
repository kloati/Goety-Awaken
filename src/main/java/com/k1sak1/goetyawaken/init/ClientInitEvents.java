package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitEvents {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.SOUL_SAPPHIRE.get(),
                    new ResourceLocation("goetyawaken", "texture_variant"),
                    (stack, world, living, seed) -> {
                        int variant = (stack.getCount() + (living != null ? living.getId() : 0) + (int) seed) % 4;
                        return variant * 0.33F;
                    });
            ItemProperties.register(ModItems.DARK_NETHERITE_BOW.get(), new ResourceLocation("pull"),
                    (stack, world, living, seed) -> {
                        if (living == null) {
                            return 0.0F;
                        } else {
                            return living.getUseItem() != stack ? 0.0F
                                    : (float) (stack.getUseDuration() - living.getUseItemRemainingTicks()) / 30;
                        }
                    });
            ItemProperties.register(ModItems.DARK_NETHERITE_BOW.get(), new ResourceLocation("pulling"),
                    (stack, world, living,
                            seed) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F
                                    : 0.0F);

            ItemProperties.register(ModItems.HARP_CROSSBOW.get(), new ResourceLocation("pull"),
                    (stack, world, living, seed) -> {
                        if (living == null) {
                            return 0.0F;
                        } else {
                            return CrossbowItem.isCharged(stack) ? 0.0F
                                    : (float) (stack.getUseDuration() - living.getUseItemRemainingTicks())
                                            / (float) CrossbowItem.getChargeDuration(stack);
                        }
                    });
            ItemProperties.register(ModItems.HARP_CROSSBOW.get(), new ResourceLocation("pulling"),
                    (stack, world, living, seed) -> {
                        return living != null && living.isUsingItem() && living.getUseItem() == stack
                                && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
                    });
            ItemProperties.register(ModItems.HARP_CROSSBOW.get(), new ResourceLocation("charged"),
                    (stack, world, living, seed) -> {
                        return CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
                    });
            ItemProperties.register(ModItems.HARP_CROSSBOW.get(), new ResourceLocation("firework"),
                    (stack, world, living, seed) -> {
                        return CrossbowItem.isCharged(stack)
                                && CrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
                    });
        });
    }
}