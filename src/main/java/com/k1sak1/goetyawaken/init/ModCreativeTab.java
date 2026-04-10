package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
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
                    .withSearchBar()
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().forEach(i -> {
                            if (i.isPresent()) {
                                String itemName = i.getKey().location().getPath();
                                if (itemName.equals("maid_fairy_servant_spawn_egg") || itemName.equals("fairy_focus")) {
                                    if (com.k1sak1.goetyawaken.common.compat.touhoulittlemaid.TouhouLittleMaidLoaded.TOUHOULITTLEMAID
                                            .isLoaded()) {
                                        output.accept(i.get());
                                    }
                                } else {
                                    output.accept(i.get());
                                }
                            }
                        });
                    }).build());
}