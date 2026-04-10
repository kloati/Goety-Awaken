package com.k1sak1.goetyawaken.data;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

        public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                        @Nullable ExistingFileHelper existingFileHelper) {
                super(output, Registries.ITEM, lookupProvider, (item) -> {
                        return item.builtInRegistryHolder().key();
                }, GoetyAwaken.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(Provider p_256380_) {

                var dauntlessTag = this.tag(ModTags.Items.DAUNTLESS_GLOVE_BOOST)
                                .add(ModItems.CLAYMORE.get())
                                .add(ModItems.OBSIDIAN_CLAYMORE.get())
                                .add(ModItems.STARLESS_NIGHT.get())
                                .addOptional(new ResourceLocation("goety", "blade_of_ender"))
                                .addOptional(new ResourceLocation("dungeons_gear", "claymore"))
                                .addOptional(new ResourceLocation("mcdw", "sword_claymore"))
                                .addOptional(new ResourceLocation("mcdw", "sword_the_starless_night"))
                                .addOptional(new ResourceLocation("irons_spellbooks", "claymore"))
                                .addOptional(new ResourceLocation("irons_spellbooks", "boreal_blade"))
                                .addOptional(new ResourceLocation("irons_spellbooks", "spellbreaker"))
                                .addOptional(new ResourceLocation("irons_spellbooks", "legionnaire_flamberge"))
                                .addOptional(new ResourceLocation("irons_spellbooks", "keeper_flamberge"))
                                .addOptional(new ResourceLocation("simplyswords", "iron_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "gold_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "diamond_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "netherite_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "runic_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "watcher_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "brimstone_claymore"))
                                .addOptional(new ResourceLocation("simplyswords", "waking_lichblade"))
                                .addOptional(new ResourceLocation("simplyswords", "awakened_lichblade"))
                                .addOptional(new ResourceLocation("legendary_monsters", "soul_great_sword"))
                                .addOptional(new ResourceLocation("legendary_monsters", "the_great_frost"))
                                .addOptional(new ResourceLocation("legendary_monsters", "chorus_blade"))
                                .addOptional(new ResourceLocation("twilightforest", "giant_sword"))
                                .addOptional(new ResourceLocation("savageandravage", "cleaver_of_beheading"))
                                .addOptional(new ResourceLocation("radiation_zone_reborn", "dustorm_greatsword"))
                                .addOptional(new ResourceLocation("tconstruct", "cleaver"))
                                .addOptional(new ResourceLocation("born_in_chaos_v1", "darkwarblade"))
                                .addOptional(new ResourceLocation("manametalmod", "true_ancient_thulium_sword"))
                                .addOptional(new ResourceLocation("aoa3", "runic_greatblade"))
                                .addOptional(new ResourceLocation("enigmaticlegacy", "etherium_sword"))
                                .addOptional(new ResourceLocation("cataclysm", "the_incinerator"))
                                .addOptional(new ResourceLocation("goetydelight", "starless_night"))
                                .addOptional(new ResourceLocation("spore", "greatsword"))
                                .addOptional(new ResourceLocation("species", "spectralibur"))
                                .addOptional(new ResourceLocation("armamentarium", "dragonslayer"));

                var assassinTag = this.tag(ModTags.Items.ASSASSIN_GLOVE_BOOST)
                                .add(ModItems.TRUTHSEEKER.get())
                                .addOptional(new ResourceLocation("goety", "fanged_dagger"))
                                .addOptional(new ResourceLocation("goety", "hungry_dagger"))
                                .addOptional(new ResourceLocation("cataclysm", "athame"))
                                .addOptional(new ResourceLocation("irons_spellbooks", "amethyst_rapier"))
                                .addOptional(new ResourceLocation("torchesbecomesunlight", "winter_pass"))
                                .addOptional(new ResourceLocation("alexscaves", "desolate_dagger"))
                                .addOptional(new ResourceLocation("graveyard", "bone_dagger"))
                                .addOptional(new ResourceLocation("mahoutsukai", "dagger"))
                                .addOptional(new ResourceLocation("epicfight", "iron_dagger"))
                                .addOptional(new ResourceLocation("epicfight", "golden_dagger"))
                                .addOptional(new ResourceLocation("born_in_chaos_v1", "intoxicating_dagger"))
                                .addOptional(new ResourceLocation("epicfight", "netherite_dagger"))
                                .addOptional(new ResourceLocation("epicfight", "diamond_dagger"));

        }

}