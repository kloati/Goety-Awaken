package com.k1sak1.goetyawaken.common.commands;

import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantEventHandler;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public class MobEnchantCommand {

    private static final SimpleCommandExceptionType ERROR_NO_TARGETS = new SimpleCommandExceptionType(
            Component.translatable("commands.goetyawaken.mobenchant.no_targets"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("goetyawaken")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("mobenchant")
                        .then(Commands.argument("enchant_type", MobEnchantTypeArgument.mobEnchantType())
                                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                        .then(Commands.argument("targets", EntityArgument.entities())
                                                .executes(context1 -> applyMobEnchant(
                                                        context1.getSource(),
                                                        MobEnchantTypeArgument.getMobEnchantType(context1,
                                                                "enchant_type"),
                                                        IntegerArgumentType.getInteger(context1, "level"),
                                                        EntityArgument.getEntities(context1, "targets"))))
                                        .executes(context1 -> applyMobEnchantToSelected(
                                                context1.getSource(),
                                                MobEnchantTypeArgument.getMobEnchantType(context1, "enchant_type"),
                                                IntegerArgumentType.getInteger(context1, "level"),
                                                context1))))
                        .then(Commands.literal("list")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(context1 -> listMobEnchants(
                                                context1.getSource(),
                                                EntityArgument.getEntities(context1, "targets"))))
                                .executes(context1 -> listMobEnchantsToSelected(
                                        context1.getSource(),
                                        context1)))
                        .then(Commands.literal("clear")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(context1 -> clearMobEnchants(
                                                context1.getSource(),
                                                EntityArgument.getEntities(context1, "targets"))))
                                .executes(context1 -> clearMobEnchantsToSelected(
                                        context1.getSource(),
                                        context1)))));
    }

    private static int applyMobEnchant(CommandSourceStack source, MobEnchantType enchantType, int level,
            Collection<? extends Entity> targets) throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_TARGETS.create();
        }

        int clampedLevel = Math.min(level, enchantType.getMaxLevel());

        int[] successCount = { 0 };
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity living) {
                MobEnchantEventHandler.applyEnchantment(living, enchantType, clampedLevel);
                if (living instanceof IAncientGlint glint && !glint.hasAncientGlint()) {
                    glint.setAncientGlint(true);
                    glint.setGlintTextureType("enchant");
                }

                successCount[0]++;
            }
        }

        if (successCount[0] == 0) {
            throw ERROR_NO_TARGETS.create();
        }

        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable(
                    "commands.goetyawaken.mobenchant.success.single",
                    enchantType.getName(),
                    clampedLevel,
                    targets.iterator().next().getDisplayName()),
                    true);
        } else {
            source.sendSuccess(() -> Component.translatable(
                    "commands.goetyawaken.mobenchant.success.multiple",
                    enchantType.getName(),
                    clampedLevel,
                    successCount[0]),
                    true);
        }

        return successCount[0];
    }

    private static int applyMobEnchantToSelected(CommandSourceStack source, MobEnchantType enchantType, int level,
            CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = source.getEntity();
        if (entity == null) {
            throw ERROR_NO_TARGETS.create();
        }

        if (entity instanceof LivingEntity living) {
            int clampedLevel = Math.min(level, enchantType.getMaxLevel());
            MobEnchantEventHandler.applyEnchantment(living, enchantType, clampedLevel);
            if (living instanceof IAncientGlint glint && !glint.hasAncientGlint()) {
                glint.setAncientGlint(true);
                glint.setGlintTextureType("enchant");
            }

            source.sendSuccess(() -> Component.translatable(
                    "commands.goetyawaken.mobenchant.success.single",
                    enchantType.getName(),
                    clampedLevel,
                    entity.getDisplayName()),
                    true);
            return 1;
        }

        throw ERROR_NO_TARGETS.create();
    }

    private static int listMobEnchants(CommandSourceStack source, Collection<? extends Entity> targets)
            throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_TARGETS.create();
        }

        int count = 0;
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity living) {
                source.sendSuccess(() -> Component.literal("=== " + entity.getDisplayName().getString() + " ==="),
                        false);

                boolean hasEnchant = false;
                for (MobEnchantType type : MobEnchantType.values()) {
                    int level = MobEnchantEventHandler.getEnchantmentLevel(living, type);
                    if (level > 0) {
                        source.sendSuccess(() -> Component.literal("  " + type.getName() + ": " + level), false);
                        hasEnchant = true;
                    }
                }

                if (!hasEnchant) {
                    source.sendSuccess(() -> Component.literal("  No enchantments"), false);
                }

                count++;
            }
        }

        if (count == 0) {
            throw ERROR_NO_TARGETS.create();
        }

        return count;
    }

    private static int listMobEnchantsToSelected(CommandSourceStack source, CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        Entity entity = source.getEntity();
        if (entity == null || !(entity instanceof LivingEntity living)) {
            throw ERROR_NO_TARGETS.create();
        }

        source.sendSuccess(() -> Component.literal("=== " + entity.getDisplayName().getString() + " ==="), false);

        boolean hasEnchant = false;
        for (MobEnchantType type : MobEnchantType.values()) {
            int level = MobEnchantEventHandler.getEnchantmentLevel(living, type);
            if (level > 0) {
                source.sendSuccess(() -> Component.literal("  " + type.getName() + ": " + level), false);
                hasEnchant = true;
            }
        }

        if (!hasEnchant) {
            source.sendSuccess(() -> Component.literal("  No enchantments"), false);
        }

        return 1;
    }

    private static int clearMobEnchants(CommandSourceStack source, Collection<? extends Entity> targets)
            throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_TARGETS.create();
        }

        int[] successCount = { 0 };
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity living) {
                MobEnchantEventHandler.clearEnchantments(living);
                successCount[0]++;
            }
        }

        if (successCount[0] == 0) {
            throw ERROR_NO_TARGETS.create();
        }

        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable(
                    "commands.goetyawaken.mobenchant.clear.success.single",
                    targets.iterator().next().getDisplayName()),
                    true);
        } else {
            source.sendSuccess(() -> Component.translatable(
                    "commands.goetyawaken.mobenchant.clear.success.multiple",
                    successCount[0]),
                    true);
        }

        return successCount[0];
    }

    private static int clearMobEnchantsToSelected(CommandSourceStack source, CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        Entity entity = source.getEntity();
        if (entity == null || !(entity instanceof LivingEntity living)) {
            throw ERROR_NO_TARGETS.create();
        }

        MobEnchantEventHandler.clearEnchantments(living);
        source.sendSuccess(() -> Component.translatable(
                "commands.goetyawaken.mobenchant.clear.success.single",
                entity.getDisplayName()),
                true);

        return 1;
    }
}
