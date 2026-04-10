package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.commands.MobEnchantTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModArgumentTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister
            .create(Registries.COMMAND_ARGUMENT_TYPE, GoetyAwaken.MODID);

    public static final RegistryObject<SingletonArgumentInfo<MobEnchantTypeArgument>> MOB_ENCHANT_TYPE = COMMAND_ARGUMENT_TYPES
            .register("mob_enchant_type",
                    () -> ArgumentTypeInfos.registerByClass(
                            MobEnchantTypeArgument.class,
                            SingletonArgumentInfo.contextFree(MobEnchantTypeArgument::mobEnchantType)));
}
