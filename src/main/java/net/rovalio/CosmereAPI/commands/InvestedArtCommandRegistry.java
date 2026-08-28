package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class InvestedArtCommandRegistry {

    private static final Map<
            ResourceLocation,
            CommandExtension
            > EXTENSIONS =
            new LinkedHashMap<>();

    private InvestedArtCommandRegistry() {
    }

    public interface CommandExtension {

        void configureGrant(
                LiteralArgumentBuilder<
                        CommandSourceStack
                        > branch
        );

        void configureRevoke(
                LiteralArgumentBuilder<
                        CommandSourceStack
                        > branch
        );
    }

    public static void register(
            ResourceLocation commandId,
            CommandExtension extension
    ) {
        Objects.requireNonNull(
                commandId,
                "Invested Art command ID cannot be null"
        );

        Objects.requireNonNull(
                extension,
                "Invested Art command extension cannot be null"
        );

        if (EXTENSIONS.containsKey(commandId)) {
            throw new IllegalStateException(
                    "An Invested Art command extension "
                            + "is already registered for: "
                            + commandId
            );
        }

        EXTENSIONS.put(
                commandId,
                extension
        );
    }

    public static boolean hasExtension(
            ResourceLocation commandId
    ) {
        return commandId != null
                && EXTENSIONS.containsKey(commandId);
    }

    static void appendGrantBranches(
            ArgumentBuilder<
                    CommandSourceStack,
                    ?
                    > parent
    ) {
        for (Map.Entry<
                ResourceLocation,
                CommandExtension
                > entry : EXTENSIONS.entrySet()) {

            LiteralArgumentBuilder<CommandSourceStack>
                    branch =
                    Commands.literal(
                            entry.getKey().toString()
                    );

            entry.getValue()
                    .configureGrant(branch);

            parent.then(branch);
        }
    }

    static void appendRevokeBranches(
            ArgumentBuilder<
                    CommandSourceStack,
                    ?
                    > parent
    ) {
        for (Map.Entry<
                ResourceLocation,
                CommandExtension
                > entry : EXTENSIONS.entrySet()) {

            LiteralArgumentBuilder<CommandSourceStack>
                    branch =
                    Commands.literal(
                            entry.getKey().toString()
                    );

            entry.getValue()
                    .configureRevoke(branch);

            parent.then(branch);
        }
    }
}