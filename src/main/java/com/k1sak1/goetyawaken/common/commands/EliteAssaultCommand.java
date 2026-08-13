package com.k1sak1.goetyawaken.common.commands;

import com.k1sak1.goetyawaken.common.events.eliteassault.EliteAssaultSpawner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistries;

public class EliteAssaultCommand {

    private static final SimpleCommandExceptionType ERROR_INVALID_BOSS = new SimpleCommandExceptionType(
            Component.translatable("commands.goetyawaken.eliteassault.invalid_boss"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("goetyawaken")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("eliteassault")
                        .then(Commands.literal("spawn")
                                .executes(context1 -> {
                                    try {
                                        return spawnEliteAssault(context1.getSource(),
                                                context1.getSource().getPlayerOrException(), null);
                                    } catch (CommandSyntaxException e) {
                                        return 0;
                                    }
                                })
                                .then(Commands.argument("targets", EntityArgument.player())
                                        .executes(context1 -> {
                                            try {
                                                return spawnEliteAssault(context1.getSource(),
                                                        EntityArgument.getPlayer(context1, "targets"), null);
                                            } catch (CommandSyntaxException e) {
                                                return 0;
                                            }
                                        })
                                        .then(Commands.argument("boss", StringArgumentType.word())
                                                .executes(context1 -> {
                                                    try {
                                                        return spawnEliteAssault(context1.getSource(),
                                                                EntityArgument.getPlayer(context1, "targets"),
                                                                StringArgumentType.getString(context1, "boss"));
                                                    } catch (CommandSyntaxException e) {
                                                        return 0;
                                                    }
                                                }))))
                        .then(Commands.literal("list_bosses")
                                .executes(context1 -> {
                                    listBosses(context1.getSource());
                                    return 1;
                                }))));
    }

    private static int spawnEliteAssault(CommandSourceStack source, ServerPlayer target, String bossName)
            throws CommandSyntaxException {
        ResourceLocation bossType = null;
        if (bossName != null) {
            bossType = new ResourceLocation(bossName);
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(bossType)) {
                throw ERROR_INVALID_BOSS.create();
            }
        }
        EliteAssaultSpawner.triggerAssault(target, bossType);
        return 1;
    }

    private static void listBosses(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Available bosses:"), false);
        for (ResourceLocation boss : EliteAssaultSpawner.getAvailableBosses()) {
            source.sendSuccess(() -> Component.literal(" - " + boss.toString()), false);
        }
    }
}