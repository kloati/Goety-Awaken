package com.k1sak1.goetyawaken.common.world.structures;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructurePlacementTypes {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPE = 
        DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, GoetyAwaken.MODID);

    public static final RegistryObject<StructurePlacementType<SecretKitchenPlacement>> SECRET_KITCHEN_PLACEMENT = 
        STRUCTURE_PLACEMENT_TYPE.register("secret_kitchen_placement", () -> () -> SecretKitchenPlacement.CODEC);
}
