package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.client.events.AncientBossBarEvent;
import com.k1sak1.goetyawaken.client.events.MasqueraderBossBarEvent;
import com.k1sak1.goetyawaken.client.events.MushroomBossBarEvent;
import com.k1sak1.goetyawaken.client.events.NamelessOneBossBarEvent;
import com.k1sak1.goetyawaken.init.ModProxy;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import net.minecraft.world.entity.Mob;

import java.util.UUID;

public class ClientProxy implements ModProxy {

    @Override
    public void addBossBar(UUID id, Mob mob) {
        if (mob instanceof MushroomMonstrosityHostile) {
            MushroomBossBarEvent.addMushroomBossBar(id, mob);
        } else if (mob instanceof NamelessOne) {
            NamelessOneBossBarEvent.addNamelessOneBossBar(id, mob);
        } else if (mob instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            AncientBossBarEvent.addAncientBossBar(id, mob);
        } else if (isMasquerader(mob)) {
            MasqueraderBossBarEvent.addMasqueraderBossBar(id, mob);
        }
    }

    @Override
    public void removeBossBar(UUID id, Mob mob) {
        if (mob instanceof MushroomMonstrosityHostile) {
            MushroomBossBarEvent.removeMushroomBossBar(id, mob);
        } else if (mob instanceof NamelessOne) {
            NamelessOneBossBarEvent.removeNamelessOneBossBar(id, mob);
        } else if (mob instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            AncientBossBarEvent.removeAncientBossBar(id, mob);
        } else if (isMasquerader(mob)) {
            MasqueraderBossBarEvent.removeMasqueraderBossBar(id, mob);
        }
    }

    private static boolean isMasquerader(Mob mob) {
        try {
            Class<?> masqClass = Class.forName("net.random_something.masquerader_mod.entity.Masquerader");
            Class<?> cloneClass = Class.forName("net.random_something.masquerader_mod.entity.MasqueraderClone");
            return masqClass.isInstance(mob) && !cloneClass.isInstance(mob);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
