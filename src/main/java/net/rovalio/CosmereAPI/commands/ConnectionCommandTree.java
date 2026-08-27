package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.player.ConnectionData;
import net.rovalio.CosmereAPI.player.ConnectionType;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static net.rovalio.CosmereAPI.commands.executor.ConnectionCommandExecutor.*;

public final class ConnectionCommandTree {

    private static final List<ConnectionType>
            CONFIGURABLE_TYPES =
            List.of(
                    ConnectionType.PLANET,
                    ConnectionType.LOCATION,
                    ConnectionType.SHARD
            );

    private ConnectionCommandTree() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack>
    createShowBranch() {

        var branch =
                Commands.literal("connection");

        branch.then(
                createListBranch()
        );

        for (ConnectionType connectionType :
                ConnectionType.values()) {

            branch.then(
                    createShowConnectionBranch(
                            connectionType
                    )
            );
        }

        return branch;
    }

    public static LiteralArgumentBuilder<CommandSourceStack>
    createConfigBranch() {

        return Commands.literal("connection")

                .then(
                        createSetBranch()
                )

                .then(
                        createEraseBranch()
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createListBranch() {

        var branch =
                Commands.literal("list");

        branch.then(
                Commands.literal("all")

                        .then(
                                Commands.argument(
                                                "players",
                                                EntityArgument.players()
                                        )

                                        .executes(context ->
                                                listAllConnections(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(
                                                                context,
                                                                "players"
                                                        )
                                                )
                                        )
                        )
        );

        for (ConnectionType connectionType :
                ConnectionType.values()) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "players",
                                                    EntityArgument.players()
                                            )

                                            .executes(context ->
                                                    listConnections(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(
                                                                    context,
                                                                    "players"
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
    createShowConnectionBranch(
            ConnectionType connectionType
    ) {
        String typeName =
                getTypeName(connectionType);

        return Commands.literal(typeName)

                .then(
                        Commands.argument(
                                        "connection_target",
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
                                                        "players",
                                                        EntityArgument.players()
                                                )

                                                .executes(context ->
                                                        getConnection(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(
                                                                        context,
                                                                        "players"
                                                                ),
                                                                connectionType,
                                                                ResourceLocationArgument.getId(
                                                                        context,
                                                                        "connection_target"
                                                                )
                                                        )
                                                )
                                )
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createSetBranch() {

        var branch =
                Commands.literal("set");

        for (ConnectionType connectionType :
                CONFIGURABLE_TYPES) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "connection_target",
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
                                                                    "players",
                                                                    EntityArgument.players()
                                                            )

                                                            .then(
                                                                    Commands.argument(
                                                                                    "strength",
                                                                                    IntegerArgumentType.integer(
                                                                                            ConnectionData.MIN_STRENGTH + 1,
                                                                                            ConnectionData.MAX_STRENGTH
                                                                                    )
                                                                            )

                                                                            .executes(context ->
                                                                                    setConnection(
                                                                                            context.getSource(),
                                                                                            EntityArgument.getPlayers(
                                                                                                    context,
                                                                                                    "players"
                                                                                            ),
                                                                                            connectionType,
                                                                                            ResourceLocationArgument.getId(
                                                                                                    context,
                                                                                                    "connection_target"
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
    createEraseBranch() {

        var branch =
                Commands.literal("erase");

        /*
         * Every type is available here so reserved
         * or orphaned Connections can still be removed.
         */
        for (ConnectionType connectionType :
                ConnectionType.values()) {

            String typeName =
                    getTypeName(connectionType);

            branch.then(
                    Commands.literal(typeName)

                            .then(
                                    Commands.argument(
                                                    "connection_target",
                                                    ResourceLocationArgument.id()
                                            )

                                            /*
                                             * Suggestions only help the user.
                                             * Manually entered orphan IDs remain valid.
                                             */
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
                                                                    "players",
                                                                    EntityArgument.players()
                                                            )

                                                            .executes(context ->
                                                                    eraseConnection(
                                                                            context.getSource(),
                                                                            EntityArgument.getPlayers(
                                                                                    context,
                                                                                    "players"
                                                                            ),
                                                                            connectionType,
                                                                            ResourceLocationArgument.getId(
                                                                                    context,
                                                                                    "connection_target"
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