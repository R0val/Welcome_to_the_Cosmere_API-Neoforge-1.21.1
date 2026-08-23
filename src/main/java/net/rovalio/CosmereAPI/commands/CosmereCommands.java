package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.commands.executor.OnboardingCommandExecutor;
import net.rovalio.CosmereAPI.onboarding.OnboardingRegistryAccess;
import net.rovalio.CosmereAPI.player.*;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

import java.util.Locale;
import java.util.Set;

import static net.rovalio.CosmereAPI.commands.executor.ConnectionCommandExecutor.*;
import static net.rovalio.CosmereAPI.commands.executor.OnboardingCommandExecutor.*;
import static net.rovalio.CosmereAPI.commands.executor.PlayerStateCommandExecutor.*;
import static net.rovalio.CosmereAPI.commands.executor.RegistryCommandExecutor.showRegistryContents;
import static net.rovalio.CosmereAPI.commands.executor.TornPagesCommandExecutor.showHeldTornPages;

public class CosmereCommands {

    private static Iterable<ResourceLocation> getConnectionTargets(
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        /// Sets values from ConnectionType Enum as usable arguments for commands
        var connectionListBranch =
                Commands.literal("type");

        var connectionGetBranch =
                Commands.literal("type");

        var connectionSetBranch =
                Commands.literal("type");

        var connectionEraseBranch =
                Commands.literal("type");

        for (ConnectionType connectionType :
                ConnectionType.values()) {

            String typeName =
                    connectionType.name()
                            .toLowerCase(Locale.ROOT);

            // Checks all Connections from every type.
            connectionListBranch.then(
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

            //Checks an specific Connections
            connectionGetBranch.then(
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

            // Erase admits every type because it needs to be able to delete reserved or orphan references
            connectionEraseBranch.then(
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

            boolean supportedType =
                    connectionType == ConnectionType.PLANET
                            || connectionType == ConnectionType.LOCATION
                            || connectionType == ConnectionType.SHARD;

            //only te three functional types appear under this
            if (supportedType) {

                connectionSetBranch.then(
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
        }

        dispatcher.register(Commands.literal("cosmere")

                .then(Commands.literal("show")

                        .then(Commands.literal("connection")

                                .then(Commands.literal("list")
                                        .then(connectionListBranch)
                                )

                                .then(Commands.literal("get")
                                        .then(connectionGetBranch)
                                )
                        )

                        .then(Commands.literal("investedarts")

                                .then(Commands.literal("list"))
                        )

                        .then(Commands.literal("origin")
                                .executes(context ->
                                        showOrigin(context.getSource())
                                )
                        )

                        .then(Commands.literal("stats")
                                .executes(context ->
                                        showStats(context.getSource())
                                )
                        )
                )

                .then(Commands.literal("config")
                        .requires(source ->
                                source.hasPermission(2)
                        )

                        .then(Commands.literal("connection")

                                .then(Commands.literal("set")
                                        .then(connectionSetBranch)
                                )

                                .then(Commands.literal("erase")
                                        .then(connectionEraseBranch)
                                )
                        )

                        .then(Commands.literal("origin")

                                .then(Commands.literal("set")

                                        .then(Commands.argument("targets", EntityArgument.players())

                                                .then(Commands.argument("planet", ResourceLocationArgument.id())

                                                        .suggests((context, builder) ->
                                                                SharedSuggestionProvider.suggestResource(
                                                                        OnboardingRegistryAccess
                                                                                .getSelectablePlanets(),
                                                                        builder
                                                                )
                                                        )

                                                        .then(Commands.argument("origin", ResourceLocationArgument.id())

                                                                .suggests((context, builder) ->
                                                                        SharedSuggestionProvider.suggestResource(
                                                                                OnboardingRegistryAccess
                                                                                        .getSelectableOriginsForPlanet(
                                                                                                ResourceLocationArgument.getId(
                                                                                                        context,
                                                                                                        "planet"
                                                                                                )
                                                                                        ),
                                                                                builder
                                                                        )
                                                                )

                                                                .executes(context ->
                                                                        changeOrigin(
                                                                                context.getSource(),
                                                                                EntityArgument.getPlayers(
                                                                                        context,
                                                                                        "targets"
                                                                                ),
                                                                                ResourceLocationArgument.getId(
                                                                                        context,
                                                                                        "planet"
                                                                                ),
                                                                                ResourceLocationArgument.getId(
                                                                                        context,
                                                                                        "origin"
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("investedarts")

                                .then(Commands.literal("grant"))

                                .then(Commands.literal("revoke"))
                        )

                        .then(Commands.literal("stats")

                                .then(Commands.literal("set")

                                        /// Changes in Spiritweb

                                        .then(Commands.literal("spiritweb")

                                                .then(Commands.literal("integrity")
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 100.0))
                                                                        .executes(context ->
                                                                                setIntegrity(
                                                                                        context.getSource(),
                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                        DoubleArgumentType.getDouble(context, "value")
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )

                                                /// Changes Investiture and then Fortune

                                                .then(Commands.literal("investiture")
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                                                                        .executes(context ->
                                                                                setInvestiture(
                                                                                        context.getSource(),
                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                        DoubleArgumentType.getDouble(context, "value")

                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )

                                                .then(Commands.literal("fortune")
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                        .executes(context ->
                                                                                setFortune(
                                                                                        context.getSource(),
                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                        DoubleArgumentType.getDouble(context, "value")
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )

                                .then(Commands.literal("reset")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(context ->
                                                        resetValues(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "targets")
                                                        )
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("onboarding")

                                .then(Commands.literal("status")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(context ->
                                                        onboardingStatus(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(
                                                                        context,
                                                                        "targets"
                                                                )
                                                        )
                                                )
                                        )
                                )

                                .then(Commands.literal("reset")
                                        .then(
                                                Commands.argument(
                                                                "targets",
                                                                EntityArgument.players()
                                                        )
                                                        .executes(context ->
                                                                OnboardingCommandExecutor
                                                                        .resetOnboarding(
                                                                                context.getSource(),
                                                                                EntityArgument.getPlayers(
                                                                                        context,
                                                                                        "targets"
                                                                                )
                                                                        )
                                                        )
                                        )
                                )
                        )
                )


                .then(Commands.literal("debug")
                        .requires(source ->
                                source.hasPermission(2)
                        )

                        .then(Commands.literal("registries")
                                .executes(context ->
                                        showRegistryContents(context.getSource())
                                )
                        )

                        .then(Commands.literal("tornpagesdata")
                                .executes(context ->
                                        showHeldTornPages(
                                                context.getSource()
                                        )
                                )
                        )
                )
        );
    }
}
