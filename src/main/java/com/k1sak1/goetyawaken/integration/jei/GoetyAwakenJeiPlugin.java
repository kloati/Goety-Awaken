package com.k1sak1.goetyawaken.integration.jei;

import com.Polarice3.Goety.compat.jei.JeiRecipeTypes;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class GoetyAwakenJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(GoetyAwaken.MODID, "jei_plugin");
    private static IJeiRuntime runtime;

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new GridRecipeTransferHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.DARK_MENDER.get(), JeiRecipeTypes.CURSED_INFUSER);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static void syncSearchText(String text) {
        if (runtime != null && runtime.getIngredientFilter() != null) {
            runtime.getIngredientFilter().setFilterText(text);
        }
    }
}
