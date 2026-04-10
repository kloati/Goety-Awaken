package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RuntimeTagRegistrar {

    private static final Set<TagKey<Item>> REGISTERED_TAGS = new HashSet<>();

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        ensureTagsRegistered();
    }

    private static void ensureTagsRegistered() {
        if (!REGISTERED_TAGS.contains(ModTags.Items.DAUNTLESS_GLOVE_BOOST)) {
            registerTagContents(ModTags.Items.DAUNTLESS_GLOVE_BOOST);
            REGISTERED_TAGS.add(ModTags.Items.DAUNTLESS_GLOVE_BOOST);
        }
        if (!REGISTERED_TAGS.contains(ModTags.Items.ASSASSIN_GLOVE_BOOST)) {
            registerTagContents(ModTags.Items.ASSASSIN_GLOVE_BOOST);
            REGISTERED_TAGS.add(ModTags.Items.ASSASSIN_GLOVE_BOOST);
        }
    }

    private static void registerTagContents(TagKey<Item> tag) {
        var tagOptional = BuiltInRegistries.ITEM.getTag(tag);
        if (tagOptional.isPresent()) {
            var tagContents = tagOptional.get();
        } else {

        }
    }
}