package com.k1sak1.goetyawaken.common.world.structures;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = 
        DeferredRegister.create(Registries.STRUCTURE_TYPE, GoetyAwaken.MODID);

    public static final RegistryObject<StructureType<SecretKitchenStructure>> SECRET_KITCHEN = 
        STRUCTURE_TYPE.register("secret_kitchen", () -> () -> SecretKitchenStructure.CODEC);
}
