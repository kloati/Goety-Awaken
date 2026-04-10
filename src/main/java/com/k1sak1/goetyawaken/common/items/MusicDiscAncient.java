package com.k1sak1.goetyawaken.common.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import java.util.function.Supplier;

public class MusicDiscAncient extends RecordItem {
    public MusicDiscAncient(int comparatorValue, Supplier<SoundEvent> soundSupplier, Properties properties,
            int lengthInTicks) {
        super(comparatorValue, soundSupplier, properties, lengthInTicks);
    }

    public static RecordItem create(Supplier<SoundEvent> soundSupplier) {
        Properties properties = (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON);
        return new MusicDiscAncient(15, soundSupplier, properties,
                70 * 20);
    }
}
