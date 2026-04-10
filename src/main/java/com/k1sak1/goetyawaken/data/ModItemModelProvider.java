package com.k1sak1.goetyawaken.data;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GoetyAwaken.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (Item item : ForgeRegistries.ITEMS) {
            if (ForgeRegistries.ITEMS.getKey(item) != null) {
                ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(item);
                if (resourceLocation != null) {
                    if (item instanceof SpawnEggItem && resourceLocation.getNamespace().equals(GoetyAwaken.MODID)) {
                        if (!item.equals(ModItems.PALE_GOLEM_SPAWN_EGG.get())) {
                            getBuilder(resourceLocation.getPath())
                                    .parent(getExistingFile(new ResourceLocation("item/template_spawn_egg")));
                        }
                    }
                }
            }
        }
    }
}