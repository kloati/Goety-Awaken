package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.compat.ModLoadedUtil;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MasqueraderServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MasqueraderServantClone;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SwampjawServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.BellringerServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.DameFortunaServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ArcherServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SkirmisherServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.LegionerServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RosalyneServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RoseSpiritServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkCentipedeServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkLeechServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShatteredServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShriekWormServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SludgeServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.StalkerServant;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class IntegrationEntityRegistration {

        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.TOUHOU_LITTLE_MAID)) {
                        event.put(ModIntegrationRegistry.MAID_FAIRY_SERVANT.get(),
                                        MaidFairyServant.createFairyAttributes().build());
                }
                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MASQUERADER)) {
                        event.put(ModIntegrationRegistry.MASQUERADER_SERVANT.get(),
                                        MasqueraderServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.MASQUERADER_SERVANT_CLONE.get(),
                                        MasqueraderServantClone.createAttributes().build());
                }
                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MEET_YOUR_FIGHT)) {
                        event.put(ModIntegrationRegistry.SWAMPJAW_SERVANT.get(),
                                        SwampjawServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.BELLRINGER_SERVANT.get(),
                                        BellringerServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.ROSALYNE_SERVANT.get(),
                                        RosalyneServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.ROSE_SPIRIT_SERVANT.get(),
                                        RoseSpiritServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.DAME_FORTUNA_SERVANT.get(),
                                        DameFortunaServant.setCustomAttributes().build());
                }
                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.TAKES_A_PILLAGE)) {
                        event.put(ModIntegrationRegistry.ARCHER_SERVANT.get(),
                                        ArcherServant.createAttributes().build());
                        event.put(ModIntegrationRegistry.SKIRMISHER_SERVANT.get(),
                                        SkirmisherServant.createAttributes().build());
                        event.put(ModIntegrationRegistry.LEGIONER_SERVANT.get(),
                                        LegionerServant.createAttributes().build());
                }
                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.DEEPER_DARKER)) {
                        event.put(ModIntegrationRegistry.SCULK_CENTIPEDE_SERVANT.get(),
                                        SculkCentipedeServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.SCULK_LEECH_SERVANT.get(),
                                        SculkLeechServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.SHATTERED_SERVANT.get(),
                                        ShatteredServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.SHRIEK_WORM_SERVANT.get(),
                                        ShriekWormServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.SLUDGE_SERVANT.get(),
                                        SludgeServant.setCustomAttributes().build());
                        event.put(ModIntegrationRegistry.STALKER_SERVANT.get(),
                                        StalkerServant.setCustomAttributes().build());
                }
        }
}