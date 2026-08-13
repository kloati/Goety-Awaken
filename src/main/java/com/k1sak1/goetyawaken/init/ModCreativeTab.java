package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GoetyAwaken.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(GoetyAwaken.MODID,
            () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.BAKASMUSIC_DISC.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyawaken"))
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().forEach(i -> {
                            if (i.isPresent()) {
                                output.accept(i.get());

                            }
                        });
                        ModIntegrationRegistry.INTEGRATION_ITEMS.getEntries().forEach(i -> {
                            if (i.isPresent()) {
                                output.accept(i.get());
                            }
                        });
                    }).build());
}