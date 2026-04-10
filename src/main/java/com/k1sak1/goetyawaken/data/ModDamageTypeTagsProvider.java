package com.k1sak1.goetyawaken.data;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.utils.ModDamageSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

        public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future,
                        ExistingFileHelper helper) {
                super(output, Registries.DAMAGE_TYPE, future, GoetyAwaken.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
                this.tag(DamageTypeTags.BYPASSES_SHIELD)
                                .add(ModDamageSource.MUSHROOM_MISSILE);
                this.tag(DamageTypeTags.BYPASSES_RESISTANCE)
                                .add(ModDamageSource.MUSHROOM_MISSILE);
                this.tag(DamageTypeTags.BYPASSES_COOLDOWN)
                                .add(ModDamageSource.EXPLOSIVE_ARROW);
        }
}