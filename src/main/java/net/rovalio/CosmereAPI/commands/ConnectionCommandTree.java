package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.player.ConnectionType;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static net.rovalio.CosmereAPI.commands.executor.ConnectionCommandExecutor.eraseConnection;
import static net.rovalio.CosmereAPI.commands.executor.ConnectionCommandExecutor.getConnection;
import static net.rovalio.CosmereAPI.commands.executor.ConnectionCommandExecutor.listConnections;
import static net.rovalio.CosmereAPI.commands.executor.ConnectionCommandExecutor.setConnection;

public final class ConnectionCommandTree {

    private ConnectionCommandTree() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack>
    createShowBranch() {

        return Commands.literal("connection")

                .then(
                        Commands.literal("list")
                                .then(
                                        createListTypeBranch()
                                )
                )

                .then(
                        Commands.literal("get")
                                .then(
                                        createGetTypeBranch()
                                )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack>
    createConfigBranch() {

        return Commands.literal("connection")

                .then(
                        Commands.literal("set")
                                .then(
                                        createSetTypeBranch()
                                )
                )

                .then(
                        Commands.literal("erase")
                                .then(
                                        createEraseTypeBranch()
                                )
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createListTypeBranch() {

        var branch =
                Commands.literal("type");

        for (ConnectionType connectionType :
                ConnectionType.values()) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "targets",
                                                    EntityArgument.players()
                                            )

                                            .executes(context ->
                                                    listConnections(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(
                                                                    context,
                                                                    "targets"
                                                            ),
                                                            connectionType
                                                    )
                                            )
                            )
            );
        }

        return branch;
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createGetTypeBranch() {

        var branch =
                Commands.literal("type");

        for (ConnectionType connectionType :
                ConnectionType.values()) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "targets",
                                                    EntityArgument.players()
                                            )

                                            .then(
                                                    Commands.argument(
                                                                    "target",
                                                                    ResourceLocationArgument.id()
                                                            )

                                                            .executes(context ->
                                                                    getConnection(
                                                                            context.getSource(),
                                                                            EntityArgument.getPlayers(
                                                                                    context,
                                                                                    "targets"
                                                                            ),
                                                                            connectionType,
                                                                            ResourceLocationArgument.getId(
                                                                                    context,
                                                                                    "target"
                                                                            )
                                                                    )
                                                            )
                                            )
                            )
            );
        }

        return branch;
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createSetTypeBranch() {

        var branch =
                Commands.literal("type");

        List<ConnectionType> supportedTypes =
                List.of(
                        ConnectionType.PLANET,
                        ConnectionType.LOCATION,
                        ConnectionType.SHARD
                );

        for (ConnectionType connectionType :
                supportedTypes) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "targets",
                                                    EntityArgument.players()
                                            )

                                            .then(
                                                    Commands.argument(
                                                                    "target",
                                                                    ResourceLocationArgument.id()
                                                            )

                                                            .suggests(
                                                                    (context, builder) ->
                                                                            SharedSuggestionProvider
                                                                                    .suggestResource(
                                                                                            getConnectionTargets(
                                                                                                    connectionType
                                                                                            ),
                                                                                            builder
                                                                                    )
                                                            )

                                                            .then(
                                                                    Commands.argument(
                                                                                    "strength",
                                                                                    IntegerArgumentType.integer(
                                                                                            1,
                                                                                            100
                                                                                    )
                                                                            )

                                                                            .executes(context ->
                                                                                    setConnection(
                                                                                            context.getSource(),
                                                                                            EntityArgument.getPlayers(
                                                                                                    context,
                                                                                                    "targets"
                                                                                            ),
                                                                                            connectionType,
                                                                                            ResourceLocationArgument.getId(
                                                                                                    context,
                                                                                                    "target"
                                                                                            ),
                                                                                            IntegerArgumentType.getInteger(
                                                                                                    context,
                                                                                                    "strength"
                                                                                            )
                                                                                    )
                                                                            )
                                                            )
                                            )
                            )
            );
        }

        return branch;
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createEraseTypeBranch() {

        var branch =
                Commands.literal("type");

        /*
         * Erase incluye todos los tipos para permitir
         * eliminar referencias reservadas o huérfanas.
         */
        for (ConnectionType connectionType :
                ConnectionType.values()) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "targets",
                                                    EntityArgument.players()
                                            )

                                            .then(
                                                    Commands.argument(
                                                                    "target",
                                                                    ResourceLocationArgument.id()
                                                            )

                                                            .executes(context ->
                                                                    eraseConnection(
                                                                            context.getSource(),
                                                                            EntityArgument.getPlayers(
                                                                                    context,
                                                                                    "targets"
                                                                            ),
                                                                            connectionType,
                                                                            ResourceLocationArgument.getId(
                                                                                    context,
                                                                                    "target"
                                                                            )
                                                                    )
                                                            )
                                            )
                            )
            );
        }

        return branch;
    }

    private static String getTypeName(
            ConnectionType type
    ) {
        return type.name()
                .toLowerCase(Locale.ROOT);
    }

    private static Iterable<ResourceLocation>
    getConnectionTargets(
            ConnectionType type
    ) {
        return switch (type) {

            case PLANET ->
                    CosmereRegistries.PLANET_REGISTRY
                            .keySet();

            case LOCATION ->
                    CosmereRegistries.LOCATION_REGISTRY
                            .keySet();

            case SHARD ->
                    CosmereRegistries.SHARD_REGISTRY
                            .keySet();

            default ->
                    Set.of();
        };
    }
}