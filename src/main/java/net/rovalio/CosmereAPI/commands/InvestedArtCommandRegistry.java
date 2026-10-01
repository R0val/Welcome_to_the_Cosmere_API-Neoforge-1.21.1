package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.util.CopyOnWriteRegistry;

import java.util.Map;
import java.util.Objects;

public final class InvestedArtCommandRegistry {

    private static final CopyOnWriteRegistry<
            ResourceLocation,
            CommandExtension
            > EXTENSIONS =
            new CopyOnWriteRegistry<>();

    private static final CopyOnWriteRegistry<
            ResourceLocation,
            BranchExtension
            > ACTION_EXTENSIONS =
            new CopyOnWriteRegistry<>();

    private static final CopyOnWriteRegistry<
            ResourceLocation,
            BranchExtension
            > INFO_EXTENSIONS =
            new CopyOnWriteRegistry<>();

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

    @FunctionalInterface
    public interface BranchExtension {

        void configure(
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

        if (!EXTENSIONS.putIfAbsent(commandId, extension)) {
            throw new IllegalStateException(
                    "An Invested Art command extension "
                            + "is already registered for: "
                            + commandId
            );
        }
    }

    public static void registerAction(
            ResourceLocation scopeId,
            BranchExtension extension
    ) {
        registerBranchExtension(
                ACTION_EXTENSIONS,
                scopeId,
                extension,
                "action"
        );
    }

    public static void registerInfo(
            ResourceLocation scopeId,
            BranchExtension extension
    ) {
        registerBranchExtension(
                INFO_EXTENSIONS,
                scopeId,
                extension,
                "info"
        );
    }

    private static void registerBranchExtension(
            CopyOnWriteRegistry<ResourceLocation, BranchExtension> registry,
            ResourceLocation scopeId,
            BranchExtension extension,
            String extensionType
    ) {
        Objects.requireNonNull(
                scopeId,
                "Invested Art command scope ID cannot be null"
        );

        Objects.requireNonNull(
                extension,
                "Invested Art "
                        + extensionType
                        + " extension cannot be null"
        );

        if (!registry.putIfAbsent(scopeId, extension)) {
            throw new IllegalStateException(
                    "An Invested Art "
                            + extensionType
                            + " extension is already registered for: "
                            + scopeId
            );
        }
    }

    public static boolean hasExtension(
            ResourceLocation commandId
    ) {
        return EXTENSIONS.containsKey(commandId);
    }

    public static boolean hasActionExtension(
            ResourceLocation scopeId
    ) {
        return ACTION_EXTENSIONS.containsKey(scopeId);
    }

    public static boolean hasInfoExtension(
            ResourceLocation scopeId
    ) {
        return INFO_EXTENSIONS.containsKey(scopeId);
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
                > entry : EXTENSIONS.entries().entrySet()) {

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
                > entry : EXTENSIONS.entries().entrySet()) {

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

    static void appendActionBranches(
            ArgumentBuilder<
                    CommandSourceStack,
                    ?
                    > parent
    ) {
        appendExtensionBranches(
                parent,
                ACTION_EXTENSIONS
        );
    }

    static void appendInfoBranches(
            ArgumentBuilder<
                    CommandSourceStack,
                    ?
                    > parent
    ) {
        appendExtensionBranches(
                parent,
                INFO_EXTENSIONS
        );
    }

    private static void appendExtensionBranches(
            ArgumentBuilder<
                    CommandSourceStack,
                    ?
                    > parent,
            CopyOnWriteRegistry<ResourceLocation, BranchExtension> extensions
    ) {
        for (Map.Entry<
                ResourceLocation,
                BranchExtension
                > entry : extensions.entries().entrySet()) {

            LiteralArgumentBuilder<CommandSourceStack>
                    branch =
                    Commands.literal(
                            entry.getKey().toString()
                    );

            entry.getValue()
                    .configure(branch);

            parent.then(branch);
        }
    }
}
