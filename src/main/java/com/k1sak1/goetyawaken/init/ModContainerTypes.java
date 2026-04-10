package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister
            .create(ForgeRegistries.MENU_TYPES, GoetyAwaken.MODID);

    public static final RegistryObject<MenuType<EnderAccessLecternContainer>> ENDER_ACCESS_LECTERN = CONTAINER_TYPES
            .register("ender_access_lectern",
                    () -> IForgeMenuType.create(EnderAccessLecternContainer::new));
}
