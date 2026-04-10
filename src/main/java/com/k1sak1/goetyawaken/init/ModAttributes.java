package com.k1sak1.goetyawaken.init;

import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.k1sak1.goetyawaken.common.entities.ally.SilverfishServant;
import com.k1sak1.goetyawaken.common.entities.ally.CreeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.SpiderCreeder;
import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import com.k1sak1.goetyawaken.common.entities.ally.EndermiteServant;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.k1sak1.goetyawaken.common.entities.ally.WightServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TormentorServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.EnviokerServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.PreacherServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.MinisterServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.IllusionerServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ArchIllusionerServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierCloneServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import com.k1sak1.goetyawaken.common.entities.ally.IceCreeperServant;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRoyalguard;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.SkullLordServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoneLordServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex;
import com.k1sak1.goetyawaken.common.entities.ally.undead.tower_wraith.AbstractTowerWraith;
import com.k1sak1.goetyawaken.common.entities.ally.ender.EndersentServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.ZombieDarkguard;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.SkeletonVanguard;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.SunkenSkeleton;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import com.k1sak1.goetyawaken.common.entities.ally.CaerbannogRabbitServant;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.VindicatorChef;
import com.k1sak1.goetyawaken.common.entities.ally.EnderKeeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import com.k1sak1.goetyawaken.common.entities.ally.ObsidianMonolithServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractParchedNecromancer;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileGnasher;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileTropicalSlime;
import com.k1sak1.goetyawaken.common.entities.hostile.IceCreeper;
import com.k1sak1.goetyawaken.common.entities.hostile.MiniGhastHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.JungleZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.FrozenZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileSnapper;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.ArchIllusioner;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileSpiderCreeder;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.TowerWitch;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TowerWitchServant;
import com.k1sak1.goetyawaken.common.entities.ally.CorruptedSlime;

import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
                event.put(ModEntityType.PALE_GOLEM_SERVANT.get(), PaleGolemServant.setCustomAttributes().build());
                event.put(ModEntityType.SILVERFISH_SERVANT.get(), SilverfishServant.setCustomAttributes().build());
                event.put(ModEntityType.CREEPER_SERVANT.get(), CreeperServant.setCustomAttributes().build());
                event.put(ModEntityType.SPIDER_CREEDER.get(), SpiderCreeder.setCustomAttributes().build());
                event.put(ModEntityType.ICE_CREEPER_SERVANT.get(), IceCreeperServant.setCustomAttributes().build());
                event.put(ModEntityType.ICE_CREEPER.get(), IceCreeper.setCustomAttributes().build());
                event.put(ModEntityType.ENDERMAN_SERVANT.get(), EndermanServant.setCustomAttributes().build());
                event.put(ModEntityType.SHULKER_SERVANT.get(), ShulkerServant.setCustomAttributes().build());
                event.put(ModEntityType.ENDERMITE_SERVANT.get(), EndermiteServant.setCustomAttributes().build());
                event.put(ModEntityType.WITHER_SERVANT.get(), WitherServant.setCustomAttributes().build());
                event.put(ModEntityType.WARDEN_SERVANT.get(), WardenServant.setCustomAttributes().build());
                event.put(ModEntityType.WIGHT_SERVANT.get(), WightServant.setCustomAttributes().build());
                event.put(ModEntityType.TORMENTOR_SERVANT.get(), TormentorServant.setCustomAttributes().build());
                event.put(ModEntityType.ENVIOKER_SERVANT.get(), EnviokerServant.setCustomAttributes().build());
                event.put(ModEntityType.PREACHER_SERVANT.get(), PreacherServant.setCustomAttributes().build());
                event.put(ModEntityType.CRONE_SERVANT.get(), CroneServant.setCustomAttributes().build());
                event.put(ModEntityType.MINISTER_SERVANT.get(), MinisterServant.setCustomAttributes().build());
                event.put(ModEntityType.SORCERER_SERVANT.get(), SorcererServant.setCustomAttributes().build());
                event.put(ModEntityType.ILLUSIONER_SERVANT.get(), IllusionerServant.setCustomAttributes().build());
                event.put(ModEntityType.ARCH_ILLUSIONER_SERVANT.get(),
                                ArchIllusionerServant.setCustomAttributes().build());
                event.put(ModEntityType.ENDERSENT_SERVANT.get(), EndersentServant.setCustomAttributes().build());
                event.put(ModEntityType.VIZIER_CLONE_SERVANT.get(), VizierCloneServant.setCustomAttributes().build());
                event.put(ModEntityType.VIZIER_SERVANT.get(), VizierServant.setCustomAttributes().build());
                event.put(ModEntityType.ROYALGUARD_SERVANT.get(), RoyalguardServant.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_ROYALGUARD.get(), HostileRoyalguard.setCustomAttributes().build());
                event.put(ModEntityType.ZOMBIE_DARKGUARD.get(), ZombieDarkguard.setCustomAttributes().build());
                event.put(ModEntityType.SKELETON_VANGUARD.get(), SkeletonVanguard.setCustomAttributes().build());
                event.put(ModEntityType.SUNKEN_SKELETON.get(), SunkenSkeleton.setCustomAttributes().build());
                event.put(ModEntityType.PARCHED.get(), Stray.createAttributes().build());
                event.put(ModEntityType.BOULDERING_ZOMBIE.get(), Zombie.createAttributes().build());
                event.put(ModEntityType.PARCHED_NECROMANCER.get(),
                                AbstractParchedNecromancer.setCustomAttributes().build());
                event.put(ModEntityType.PARCHED_NECROMANCER_SERVANT.get(),
                                AbstractParchedNecromancer.setCustomAttributes().build());

                event.put(ModEntityType.NAMELESS_ONE.get(), AbstractNamelessOne.setCustomAttributes().build());
                event.put(ModEntityType.NAMELESS_ONE_SERVANT.get(),
                                AbstractNamelessOne.setCustomAttributes().build());

                event.put(ModEntityType.HOSTILE_WILDFIRE.get(), Wildfire.setCustomAttributes().build());
                event.put(ModEntityType.WRAITH_NECROMANCER.get(), WraithNecromancer.setCustomAttributes().build());
                event.put(ModEntityType.WRAITH_NECROMANCER_SERVANT.get(),
                                WraithNecromancerServant.setCustomAttributes().build());
                event.put(ModEntityType.MUSHROOM_MONSTROSITY.get(), MushroomMonstrosity.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_MUSHROOM_MONSTROSITY.get(),
                                MushroomMonstrosity.setCustomAttributes().build());
                event.put(ModEntityType.ANGRY_MOOSHROOM.get(), AngryMooshroom.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_ANGRY_MOOSHROOM.get(), AngryMooshroom.setCustomAttributes().build());
                event.put(ModEntityType.CAERBANNOG_RABBIT_SERVANT.get(),
                                CaerbannogRabbitServant.setCustomAttributes().build());
                event.put(ModEntityType.VINDICATOR_CHEF.get(), VindicatorChef.setCustomAttributes().build());
                event.put(ModEntityType.ENDER_KEEPER_SERVANT.get(), EnderKeeperServant.setCustomAttributes().build());
                event.put(ModEntityType.APOSTLE_SERVANT.get(), ApostleServant.setCustomAttributes().build());
                event.put(ModEntityType.OBSIDIAN_MONOLITH_SERVANT.get(),
                                ObsidianMonolithServant.setCustomAttributes().build());
                event.put(ModEntityType.PARCHED_SERVANT.get(), ParchedServant.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_GNASHER.get(), HostileGnasher.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_TROPICAL_SLIME.get(),
                                HostileTropicalSlime.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_MINI_GHAST.get(), MiniGhastHostile.setCustomAttributes().build());
                event.put(ModEntityType.JUNGLE_ZOMBIE.get(), JungleZombie.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_SNAPPER.get(), HostileSnapper.setCustomAttributes().build());
                event.put(ModEntityType.SKULL_LORD_SERVANT.get(), SkullLordServant.setCustomAttributes().build());
                event.put(ModEntityType.BONE_LORD_SERVANT.get(), BoneLordServant.setCustomAttributes().build());
                event.put(ModEntityType.BOUND_SORCERER.get(), BoundSorcerer.setCustomAttributes().build());
                event.put(ModEntityType.VANGUARD_CHAMPION.get(), VanguardChampion.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_VANGUARD_CHAMPION.get(),
                                VanguardChampion.setCustomAttributes().build());
                event.put(ModEntityType.ARCH_ILLUSIONER.get(), ArchIllusioner.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_DROWNED_NECROMANCER.get(),
                                DrownedNecromancer.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_SPIDER_CREEDER.get(),
                                HostileSpiderCreeder.setCustomAttributes().build());
                event.put(ModEntityType.FROZEN_ZOMBIE.get(), FrozenZombie.setCustomAttributes().build());
                event.put(ModEntityType.SCARLET_VEX.get(), ScarletVex.setCustomAttributes().build());
                event.put(ModEntityType.TOWER_WRAITH_SERVANT.get(), AbstractTowerWraith.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_TOWER_WRAITH.get(), AbstractTowerWraith.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_TWILIGHT_GOAT.get(),
                                com.Polarice3.Goety.common.entities.ally.TwilightGoat.setCustomAttributes().build());
                event.put(ModEntityType.TOWER_WITCH.get(), TowerWitch.setCustomAttributes().build());
                event.put(ModEntityType.TOWER_WITCH_SERVANT.get(), TowerWitchServant.setCustomAttributes().build());
                event.put(ModEntityType.CORRUPTED_SLIME.get(), CorruptedSlime.setCustomAttributes().build());
        }
}