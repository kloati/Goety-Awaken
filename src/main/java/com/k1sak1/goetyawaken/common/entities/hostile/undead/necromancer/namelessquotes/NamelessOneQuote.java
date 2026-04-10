package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NamelessOneQuote {
	private static final Random RANDOM = new Random();
	private static final List<NamelessOneQuote> ALL_QUOTES = new ArrayList<>();

	// 生成语录
	public static final NamelessOneQuote SPAWN_1 = new NamelessOneQuote("spawn_1")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote SPAWN_2 = new NamelessOneQuote("spawn_2")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote SPAWN_3 = new NamelessOneQuote("spawn_3")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote SPAWN_4 = new NamelessOneQuote("spawn_4")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote SPAWN_5 = new NamelessOneQuote("spawn_5")
			.addSubtitles(new NamelessOneSubtitles(13.0));
	public static final NamelessOneQuote SPAWN_6 = new NamelessOneQuote("spawn_6")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote SPAWN_7 = new NamelessOneQuote("spawn_7")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote SPAWN_8 = new NamelessOneQuote("spawn_8")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote SPAWN_9 = new NamelessOneQuote("spawn_9")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote SPAWN_10 = new NamelessOneQuote("spawn_10")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote SPAWN_11 = new NamelessOneQuote("spawn_11")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote SPAWN_12 = new NamelessOneQuote("spawn_12")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote SPAWN_13 = new NamelessOneQuote("spawn_13")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote SPAWN_14 = new NamelessOneQuote("spawn_14")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote SPAWN_15 = new NamelessOneQuote("spawn_15")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote SPAWN_16 = new NamelessOneQuote("spawn_16")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote SPAWN_17 = new NamelessOneQuote("spawn_17")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote SPAWN_18 = new NamelessOneQuote("spawn_18")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote SPAWN_19 = new NamelessOneQuote("spawn_19")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote SPAWN_20 = new NamelessOneQuote("spawn_20")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote SPAWN_21 = new NamelessOneQuote("spawn_21")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote SPAWN_22 = new NamelessOneQuote("spawn_22")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote SPAWN_23 = new NamelessOneQuote("spawn_23")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote SPAWN_24 = new NamelessOneQuote("spawn_24")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote SPAWN_25 = new NamelessOneQuote("spawn_25")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote SPAWN_26 = new NamelessOneQuote("spawn_26")
			.addSubtitles(new NamelessOneSubtitles(12.0));

	// 死亡语录
	public static final NamelessOneQuote DEATH_1 = new NamelessOneQuote("death_1")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote DEATH_2 = new NamelessOneQuote("death_2")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote DEATH_3 = new NamelessOneQuote("death_3")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DEATH_4 = new NamelessOneQuote("death_4")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote DEATH_5 = new NamelessOneQuote("death_5")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DEATH_6 = new NamelessOneQuote("death_6")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote DEATH_7 = new NamelessOneQuote("death_7")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DEATH_8 = new NamelessOneQuote("death_8")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote DEATH_9 = new NamelessOneQuote("death_9")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote DEATH_10 = new NamelessOneQuote("death_10")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DEATH_11 = new NamelessOneQuote("death_11")
			.addSubtitles(new NamelessOneSubtitles(10.5));

	// 闲聊语录
	public static final NamelessOneQuote CHAT_1 = new NamelessOneQuote("chat_1")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_2 = new NamelessOneQuote("chat_2")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_3 = new NamelessOneQuote("chat_3")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_4 = new NamelessOneQuote("chat_4")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_5 = new NamelessOneQuote("chat_5")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote CHAT_6 = new NamelessOneQuote("chat_6")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_7 = new NamelessOneQuote("chat_7")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote CHAT_8 = new NamelessOneQuote("chat_8")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_9 = new NamelessOneQuote("chat_9")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_10 = new NamelessOneQuote("chat_10")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_11 = new NamelessOneQuote("chat_11")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_12 = new NamelessOneQuote("chat_12")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote CHAT_13 = new NamelessOneQuote("chat_13")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_14 = new NamelessOneQuote("chat_14")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote CHAT_15 = new NamelessOneQuote("chat_15")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_16 = new NamelessOneQuote("chat_16")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_17 = new NamelessOneQuote("chat_17")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_18 = new NamelessOneQuote("chat_18")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_19 = new NamelessOneQuote("chat_19")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_20 = new NamelessOneQuote("chat_20")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_21 = new NamelessOneQuote("chat_21")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_22 = new NamelessOneQuote("chat_22")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_23 = new NamelessOneQuote("chat_23")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_24 = new NamelessOneQuote("chat_24")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_25 = new NamelessOneQuote("chat_25")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_26 = new NamelessOneQuote("chat_26")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote CHAT_27 = new NamelessOneQuote("chat_27")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_28 = new NamelessOneQuote("chat_28")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote CHAT_29 = new NamelessOneQuote("chat_29")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_30 = new NamelessOneQuote("chat_30")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_31 = new NamelessOneQuote("chat_31")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_32 = new NamelessOneQuote("chat_32")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_33 = new NamelessOneQuote("chat_33")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_34 = new NamelessOneQuote("chat_34")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_35 = new NamelessOneQuote("chat_35")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_36 = new NamelessOneQuote("chat_36")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_37 = new NamelessOneQuote("chat_37")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_38 = new NamelessOneQuote("chat_38")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_39 = new NamelessOneQuote("chat_39")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_40 = new NamelessOneQuote("chat_40")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_41 = new NamelessOneQuote("chat_41")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_42 = new NamelessOneQuote("chat_42")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_43 = new NamelessOneQuote("chat_43")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_44 = new NamelessOneQuote("chat_44")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote CHAT_45 = new NamelessOneQuote("chat_45")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_46 = new NamelessOneQuote("chat_46")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote CHAT_47 = new NamelessOneQuote("chat_47")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_48 = new NamelessOneQuote("chat_48")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_49 = new NamelessOneQuote("chat_49")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_50 = new NamelessOneQuote("chat_50")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_51 = new NamelessOneQuote("chat_51")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_52 = new NamelessOneQuote("chat_52")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_53 = new NamelessOneQuote("chat_53")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_54 = new NamelessOneQuote("chat_54")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_55 = new NamelessOneQuote("chat_55")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_56 = new NamelessOneQuote("chat_56")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_57 = new NamelessOneQuote("chat_57")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_58 = new NamelessOneQuote("chat_58")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_59 = new NamelessOneQuote("chat_59")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_60 = new NamelessOneQuote("chat_60")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_61 = new NamelessOneQuote("chat_61")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_62 = new NamelessOneQuote("chat_62")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_63 = new NamelessOneQuote("chat_63")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_64 = new NamelessOneQuote("chat_64")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_65 = new NamelessOneQuote("chat_65")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_66 = new NamelessOneQuote("chat_66")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_67 = new NamelessOneQuote("chat_67")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_68 = new NamelessOneQuote("chat_68")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_69 = new NamelessOneQuote("chat_69")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_70 = new NamelessOneQuote("chat_70")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_71 = new NamelessOneQuote("chat_71")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_72 = new NamelessOneQuote("chat_72")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_73 = new NamelessOneQuote("chat_73")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_74 = new NamelessOneQuote("chat_74")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_75 = new NamelessOneQuote("chat_75")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_76 = new NamelessOneQuote("chat_76")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote CHAT_77 = new NamelessOneQuote("chat_77")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_78 = new NamelessOneQuote("chat_78")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote CHAT_79 = new NamelessOneQuote("chat_79")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_80 = new NamelessOneQuote("chat_80")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_81 = new NamelessOneQuote("chat_81")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_82 = new NamelessOneQuote("chat_82")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_83 = new NamelessOneQuote("chat_83")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_84 = new NamelessOneQuote("chat_84")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_85 = new NamelessOneQuote("chat_85")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_86 = new NamelessOneQuote("chat_86")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_87 = new NamelessOneQuote("chat_87")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_88 = new NamelessOneQuote("chat_88")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_89 = new NamelessOneQuote("chat_89")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_90 = new NamelessOneQuote("chat_90")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_91 = new NamelessOneQuote("chat_91")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_92 = new NamelessOneQuote("chat_92")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_93 = new NamelessOneQuote("chat_93")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_94 = new NamelessOneQuote("chat_94")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_95 = new NamelessOneQuote("chat_95")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_96 = new NamelessOneQuote("chat_96")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote CHAT_97 = new NamelessOneQuote("chat_97")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote CHAT_98 = new NamelessOneQuote("chat_98")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote CHAT_99 = new NamelessOneQuote("chat_99")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote CHAT_100 = new NamelessOneQuote("chat_100")
			.addSubtitles(new NamelessOneSubtitles(10.5));

	// 玩家死亡语录
	public static final NamelessOneQuote PLAYER_DEATH_1 = new NamelessOneQuote("player_death_1")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_2 = new NamelessOneQuote("player_death_2")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote PLAYER_DEATH_3 = new NamelessOneQuote("player_death_3")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_4 = new NamelessOneQuote("player_death_4")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_5 = new NamelessOneQuote("player_death_5")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_6 = new NamelessOneQuote("player_death_6")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote PLAYER_DEATH_7 = new NamelessOneQuote("player_death_7")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_8 = new NamelessOneQuote("player_death_8")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote PLAYER_DEATH_9 = new NamelessOneQuote("player_death_9")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_10 = new NamelessOneQuote("player_death_10")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_11 = new NamelessOneQuote("player_death_11")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_12 = new NamelessOneQuote("player_death_12")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_13 = new NamelessOneQuote("player_death_13")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote PLAYER_DEATH_14 = new NamelessOneQuote("player_death_14")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_15 = new NamelessOneQuote("player_death_15")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_16 = new NamelessOneQuote("player_death_16")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote PLAYER_DEATH_17 = new NamelessOneQuote("player_death_17")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote PLAYER_DEATH_18 = new NamelessOneQuote("player_death_18")
			.addSubtitles(new NamelessOneSubtitles(8.0));
	public static final NamelessOneQuote PLAYER_DEATH_19 = new NamelessOneQuote("player_death_19")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_20 = new NamelessOneQuote("player_death_20")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_21 = new NamelessOneQuote("player_death_21")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_22 = new NamelessOneQuote("player_death_22")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote PLAYER_DEATH_23 = new NamelessOneQuote("player_death_23")
			.addSubtitles(new NamelessOneSubtitles(8.5));
	public static final NamelessOneQuote PLAYER_DEATH_24 = new NamelessOneQuote("player_death_24")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote PLAYER_DEATH_25 = new NamelessOneQuote("player_death_25")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_26 = new NamelessOneQuote("player_death_26")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_27 = new NamelessOneQuote("player_death_27")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_28 = new NamelessOneQuote("player_death_28")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote PLAYER_DEATH_29 = new NamelessOneQuote("player_death_29")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_30 = new NamelessOneQuote("player_death_30")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_31 = new NamelessOneQuote("player_death_31")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote PLAYER_DEATH_32 = new NamelessOneQuote("player_death_32")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_33 = new NamelessOneQuote("player_death_33")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_34 = new NamelessOneQuote("player_death_34")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_35 = new NamelessOneQuote("player_death_35")
			.addSubtitles(new NamelessOneSubtitles(12.0));
	public static final NamelessOneQuote PLAYER_DEATH_36 = new NamelessOneQuote("player_death_36")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote PLAYER_DEATH_37 = new NamelessOneQuote("player_death_37")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_38 = new NamelessOneQuote("player_death_38")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_39 = new NamelessOneQuote("player_death_39")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_40 = new NamelessOneQuote("player_death_40")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote PLAYER_DEATH_41 = new NamelessOneQuote("player_death_41")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_42 = new NamelessOneQuote("player_death_42")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote PLAYER_DEATH_43 = new NamelessOneQuote("player_death_43")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_44 = new NamelessOneQuote("player_death_44")
			.addSubtitles(new NamelessOneSubtitles(12.5));
	public static final NamelessOneQuote PLAYER_DEATH_45 = new NamelessOneQuote("player_death_45")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote PLAYER_DEATH_46 = new NamelessOneQuote("player_death_46")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote PLAYER_DEATH_47 = new NamelessOneQuote("player_death_47")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote PLAYER_DEATH_48 = new NamelessOneQuote("player_death_48")
			.addSubtitles(new NamelessOneSubtitles(8.5));
	public static final NamelessOneQuote PLAYER_DEATH_49 = new NamelessOneQuote("player_death_49")
			.addSubtitles(new NamelessOneSubtitles(11.0));

	// 为玩家召唤仆从语录
	public static final NamelessOneQuote SUMMON_SERVANT_1 = new NamelessOneQuote("summon_servant_1")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote SUMMON_SERVANT_2 = new NamelessOneQuote("summon_servant_2")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote SUMMON_SERVANT_3 = new NamelessOneQuote("summon_servant_3")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote SUMMON_SERVANT_4 = new NamelessOneQuote("summon_servant_4")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote SUMMON_SERVANT_5 = new NamelessOneQuote("summon_servant_5")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote SUMMON_SERVANT_6 = new NamelessOneQuote("summon_servant_6")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote SUMMON_SERVANT_7 = new NamelessOneQuote("summon_servant_7")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote SUMMON_SERVANT_8 = new NamelessOneQuote("summon_servant_8")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote SUMMON_SERVANT_9 = new NamelessOneQuote("summon_servant_9")
			.addSubtitles(new NamelessOneSubtitles(12.0));

	// 发现敌对生物语录
	public static final NamelessOneQuote DISCOVER_ENEMY_1 = new NamelessOneQuote("discover_enemy_1")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_2 = new NamelessOneQuote("discover_enemy_2")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_3 = new NamelessOneQuote("discover_enemy_3")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_4 = new NamelessOneQuote("discover_enemy_4")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_5 = new NamelessOneQuote("discover_enemy_5")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_6 = new NamelessOneQuote("discover_enemy_6")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_7 = new NamelessOneQuote("discover_enemy_7")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_8 = new NamelessOneQuote("discover_enemy_8")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_9 = new NamelessOneQuote("discover_enemy_9")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_10 = new NamelessOneQuote("discover_enemy_10")
			.addSubtitles(new NamelessOneSubtitles(11.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_11 = new NamelessOneQuote("discover_enemy_11")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_12 = new NamelessOneQuote("discover_enemy_12")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_13 = new NamelessOneQuote("discover_enemy_13")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_14 = new NamelessOneQuote("discover_enemy_14")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_15 = new NamelessOneQuote("discover_enemy_15")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_16 = new NamelessOneQuote("discover_enemy_16")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_17 = new NamelessOneQuote("discover_enemy_17")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_18 = new NamelessOneQuote("discover_enemy_18")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_19 = new NamelessOneQuote("discover_enemy_19")
			.addSubtitles(new NamelessOneSubtitles(11.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_20 = new NamelessOneQuote("discover_enemy_20")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_21 = new NamelessOneQuote("discover_enemy_21")
			.addSubtitles(new NamelessOneSubtitles(8.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_22 = new NamelessOneQuote("discover_enemy_22")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_23 = new NamelessOneQuote("discover_enemy_23")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote DISCOVER_ENEMY_24 = new NamelessOneQuote("discover_enemy_24")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote DISCOVER_ENEMY_25 = new NamelessOneQuote("discover_enemy_25")
			.addSubtitles(new NamelessOneSubtitles(10.5));

	// 击杀敌对生物语录
	public static final NamelessOneQuote KILL_ENEMY_1 = new NamelessOneQuote("kill_enemy_1")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote KILL_ENEMY_2 = new NamelessOneQuote("kill_enemy_2")
			.addSubtitles(new NamelessOneSubtitles(8.5));
	public static final NamelessOneQuote KILL_ENEMY_3 = new NamelessOneQuote("kill_enemy_3")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote KILL_ENEMY_4 = new NamelessOneQuote("kill_enemy_4")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote KILL_ENEMY_5 = new NamelessOneQuote("kill_enemy_5")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote KILL_ENEMY_6 = new NamelessOneQuote("kill_enemy_6")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote KILL_ENEMY_7 = new NamelessOneQuote("kill_enemy_7")
			.addSubtitles(new NamelessOneSubtitles(8.5));
	public static final NamelessOneQuote KILL_ENEMY_8 = new NamelessOneQuote("kill_enemy_8")
			.addSubtitles(new NamelessOneSubtitles(9.5));
	public static final NamelessOneQuote KILL_ENEMY_9 = new NamelessOneQuote("kill_enemy_9")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote KILL_ENEMY_10 = new NamelessOneQuote("kill_enemy_10")
			.addSubtitles(new NamelessOneSubtitles(10.0));

	// 击杀玩家语录
	public static final NamelessOneQuote KILL_PLAYER_1 = new NamelessOneQuote("kill_player_1")
			.addSubtitles(new NamelessOneSubtitles(8.5));
	public static final NamelessOneQuote KILL_PLAYER_2 = new NamelessOneQuote("kill_player_2")
			.addSubtitles(new NamelessOneSubtitles(9.0));

	// 击杀特定生物语录
	public static final NamelessOneQuote KILL_ENDER_DRAGON_1 = new NamelessOneQuote("kill_ender_dragon_1")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote KILL_ENDER_DRAGON_2 = new NamelessOneQuote("kill_ender_dragon_2")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote KILL_ENDER_DRAGON_3 = new NamelessOneQuote("kill_ender_dragon_3")
			.addSubtitles(new NamelessOneSubtitles(11.0));

	public static final NamelessOneQuote KILL_WITHER_1 = new NamelessOneQuote("kill_wither_1")
			.addSubtitles(new NamelessOneSubtitles(9.0));
	public static final NamelessOneQuote KILL_WITHER_2 = new NamelessOneQuote("kill_wither_2")
			.addSubtitles(new NamelessOneSubtitles(9.5));

	public static final NamelessOneQuote KILL_WARDEN_1 = new NamelessOneQuote("kill_warden_1")
			.addSubtitles(new NamelessOneSubtitles(10.5));
	public static final NamelessOneQuote KILL_WARDEN_2 = new NamelessOneQuote("kill_warden_2")
			.addSubtitles(new NamelessOneSubtitles(11.0));

	public static final NamelessOneQuote KILL_ELDER_GUARDIAN_1 = new NamelessOneQuote("kill_elder_guardian_1")
			.addSubtitles(new NamelessOneSubtitles(10.0));
	public static final NamelessOneQuote KILL_ELDER_GUARDIAN_2 = new NamelessOneQuote("kill_elder_guardian_2")
			.addSubtitles(new NamelessOneSubtitles(9.5));

	public static final List<NamelessOneQuote> SPAWN_QUOTES = ImmutableList.of(
			SPAWN_1, SPAWN_2, SPAWN_3, SPAWN_4, SPAWN_5, SPAWN_6, SPAWN_7, SPAWN_8,
			SPAWN_9, SPAWN_10, SPAWN_11, SPAWN_12, SPAWN_13, SPAWN_14, SPAWN_15, SPAWN_16,
			SPAWN_17, SPAWN_18, SPAWN_19, SPAWN_20, SPAWN_21, SPAWN_22, SPAWN_23, SPAWN_24,
			SPAWN_25, SPAWN_26);

	public static final List<NamelessOneQuote> DEATH_QUOTES = ImmutableList.of(
			DEATH_1, DEATH_2, DEATH_3, DEATH_4, DEATH_5, DEATH_6, DEATH_7, DEATH_8, DEATH_9, DEATH_10, DEATH_11);

	public static final List<NamelessOneQuote> CHAT_QUOTES = ImmutableList.of(
			CHAT_1, CHAT_2, CHAT_3, CHAT_4, CHAT_5, CHAT_6, CHAT_7, CHAT_8, CHAT_9, CHAT_10,
			CHAT_11, CHAT_12, CHAT_13, CHAT_14, CHAT_15, CHAT_16, CHAT_17, CHAT_18, CHAT_19, CHAT_20,
			CHAT_21, CHAT_22, CHAT_23, CHAT_24, CHAT_25, CHAT_26, CHAT_27, CHAT_28, CHAT_29, CHAT_30,
			CHAT_31, CHAT_32, CHAT_33, CHAT_34, CHAT_35, CHAT_36, CHAT_37, CHAT_38, CHAT_39, CHAT_40,
			CHAT_41, CHAT_42, CHAT_43, CHAT_44, CHAT_45, CHAT_46, CHAT_47, CHAT_48, CHAT_49, CHAT_50,
			CHAT_51, CHAT_52, CHAT_53, CHAT_54, CHAT_55, CHAT_56, CHAT_57, CHAT_58, CHAT_59, CHAT_60,
			CHAT_61, CHAT_62, CHAT_63, CHAT_64, CHAT_65, CHAT_66, CHAT_67, CHAT_68, CHAT_69, CHAT_70,
			CHAT_71, CHAT_72, CHAT_73, CHAT_74, CHAT_75, CHAT_76, CHAT_77, CHAT_78, CHAT_79, CHAT_80,
			CHAT_81, CHAT_82, CHAT_83, CHAT_84, CHAT_85, CHAT_86, CHAT_87, CHAT_88, CHAT_89, CHAT_90,
			CHAT_91, CHAT_92, CHAT_93, CHAT_94, CHAT_95, CHAT_96, CHAT_97, CHAT_98, CHAT_99, CHAT_100);

	public static final List<NamelessOneQuote> PLAYER_DEATH_QUOTES = ImmutableList.of(
			PLAYER_DEATH_1, PLAYER_DEATH_2, PLAYER_DEATH_3, PLAYER_DEATH_4, PLAYER_DEATH_5,
			PLAYER_DEATH_6, PLAYER_DEATH_7, PLAYER_DEATH_8, PLAYER_DEATH_9, PLAYER_DEATH_10,
			PLAYER_DEATH_11, PLAYER_DEATH_12, PLAYER_DEATH_13, PLAYER_DEATH_14, PLAYER_DEATH_15,
			PLAYER_DEATH_16, PLAYER_DEATH_17, PLAYER_DEATH_18, PLAYER_DEATH_19, PLAYER_DEATH_20,
			PLAYER_DEATH_21, PLAYER_DEATH_22, PLAYER_DEATH_23, PLAYER_DEATH_24, PLAYER_DEATH_25,
			PLAYER_DEATH_26, PLAYER_DEATH_27, PLAYER_DEATH_28, PLAYER_DEATH_29, PLAYER_DEATH_30,
			PLAYER_DEATH_31, PLAYER_DEATH_32, PLAYER_DEATH_33, PLAYER_DEATH_34, PLAYER_DEATH_35,
			PLAYER_DEATH_36, PLAYER_DEATH_37, PLAYER_DEATH_38, PLAYER_DEATH_39, PLAYER_DEATH_40,
			PLAYER_DEATH_41, PLAYER_DEATH_42, PLAYER_DEATH_43, PLAYER_DEATH_44, PLAYER_DEATH_45,
			PLAYER_DEATH_46, PLAYER_DEATH_47, PLAYER_DEATH_48, PLAYER_DEATH_49);

	public static final List<NamelessOneQuote> SUMMON_SERVANT_QUOTES = ImmutableList.of(
			SUMMON_SERVANT_1, SUMMON_SERVANT_2, SUMMON_SERVANT_3, SUMMON_SERVANT_4, SUMMON_SERVANT_5,
			SUMMON_SERVANT_6, SUMMON_SERVANT_7, SUMMON_SERVANT_8, SUMMON_SERVANT_9);

	public static final List<NamelessOneQuote> DISCOVER_ENEMY_QUOTES = ImmutableList.of(
			DISCOVER_ENEMY_1, DISCOVER_ENEMY_2, DISCOVER_ENEMY_3, DISCOVER_ENEMY_4, DISCOVER_ENEMY_5, DISCOVER_ENEMY_6,
			DISCOVER_ENEMY_7, DISCOVER_ENEMY_8, DISCOVER_ENEMY_9, DISCOVER_ENEMY_10, DISCOVER_ENEMY_11,
			DISCOVER_ENEMY_12,
			DISCOVER_ENEMY_13, DISCOVER_ENEMY_14, DISCOVER_ENEMY_15, DISCOVER_ENEMY_16, DISCOVER_ENEMY_17,
			DISCOVER_ENEMY_18,
			DISCOVER_ENEMY_19, DISCOVER_ENEMY_20, DISCOVER_ENEMY_21, DISCOVER_ENEMY_22, DISCOVER_ENEMY_23,
			DISCOVER_ENEMY_24,
			DISCOVER_ENEMY_25);

	public static final List<NamelessOneQuote> KILL_ENEMY_QUOTES = ImmutableList.of(
			KILL_ENEMY_1, KILL_ENEMY_2, KILL_ENEMY_3, KILL_ENEMY_4, KILL_ENEMY_5, KILL_ENEMY_6, KILL_ENEMY_7,
			KILL_ENEMY_8, KILL_ENEMY_9, KILL_ENEMY_10);

	public static final List<NamelessOneQuote> KILL_PLAYER_QUOTES = ImmutableList.of(
			KILL_PLAYER_1, KILL_PLAYER_2);

	public static final List<NamelessOneQuote> KILL_ENDER_DRAGON_QUOTES = ImmutableList.of(
			KILL_ENDER_DRAGON_1, KILL_ENDER_DRAGON_2, KILL_ENDER_DRAGON_3);

	public static final List<NamelessOneQuote> KILL_WITHER_QUOTES = ImmutableList.of(
			KILL_WITHER_1, KILL_WITHER_2);

	public static final List<NamelessOneQuote> KILL_WARDEN_QUOTES = ImmutableList.of(
			KILL_WARDEN_1, KILL_WARDEN_2);

	public static final List<NamelessOneQuote> KILL_ELDER_GUARDIAN_QUOTES = ImmutableList.of(
			KILL_ELDER_GUARDIAN_1, KILL_ELDER_GUARDIAN_2);

	private static NamelessOneQuote lastQuote = null;

	private final SoundEvent sound;
	private final String name;
	private NamelessOneSubtitles subtitles;
	private final int id;

	public NamelessOneQuote(String name) {
		this.name = name;
		this.sound = null;
		this.id = ALL_QUOTES.size();

		ALL_QUOTES.add(this);
	}

	public NamelessOneQuote addSubtitles(NamelessOneSubtitles subtitles) {
		Preconditions.checkArgument(this.subtitles == null, "Subtitles already added!");
		subtitles.setQuote(this);
		this.subtitles = subtitles;
		return this;
	}

	public void play(ServerPlayer player, int delayTicks) {
		ModNetwork.channel.send(PacketDistributor.PLAYER.with(() -> player),
				new SNamelessOneQuotePacket(this, delayTicks));
		lastQuote = this;
	}

	public void play(ServerPlayer player) {
		this.play(player, 1);
	}

	@OnlyIn(Dist.CLIENT)
	public void play() {
		this.play(1);
	}

	@OnlyIn(Dist.CLIENT)
	public void play(int delayTicks) {
		NamelessOneQuoteHandler.INSTANCE.playQuote(this, delayTicks);
	}

	public NamelessOneSubtitles getSubtitles() {
		return this.subtitles;
	}

	public int getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public static NamelessOneQuote getByID(int id) {
		return ALL_QUOTES.get(id);
	}

	public static List<NamelessOneQuote> getAllQuotes() {
		return Collections.unmodifiableList(ALL_QUOTES);
	}

	public static NamelessOneQuote getRandom(List<NamelessOneQuote> list) {
		NamelessOneQuote quote = null;

		if (list.isEmpty()) {
			return null;
		}

		do {
			quote = list.get(RANDOM.nextInt(list.size()));
		} while (quote == lastQuote && list.size() > 1);

		return quote;
	}
}