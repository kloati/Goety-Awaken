package com.k1sak1.goetyawaken.common.commands;

import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MobEnchantTypeArgument implements ArgumentType<MobEnchantType> {

    public static MobEnchantTypeArgument mobEnchantType() {
        return new MobEnchantTypeArgument();
    }

    public static MobEnchantType getMobEnchantType(CommandContext<?> context, String name)
            throws CommandSyntaxException {
        MobEnchantType type = context.getArgument(name, MobEnchantType.class);
        if (type == null) {
            throw new IllegalArgumentException("Invalid mob enchant type");
        }
        return type;
    }

    @Override
    public MobEnchantType parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readString();

        MobEnchantType type = MobEnchantType.byName(name);
        if (type == null) {
            reader.setCursor(cursor);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }

        return type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (MobEnchantType type : MobEnchantType.values()) {
            builder.suggest(type.getName());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        MobEnchantType[] types = MobEnchantType.values();
        String[] examples = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            examples[i] = types[i].getName();
        }
        return Arrays.asList(examples);
    }
}
