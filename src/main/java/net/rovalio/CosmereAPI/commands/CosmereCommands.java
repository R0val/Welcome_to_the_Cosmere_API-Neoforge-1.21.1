package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.rovalio.CosmereAPI.commands.executor.OnboardingCommandExecutor;
import net.rovalio.CosmereAPI.onboarding.OnboardingRegistryAccess;
import net.rovalio.CosmereAPI.player.*;

import static net.rovalio.CosmereAPI.commands.executor.OnboardingCommandExecutor.*;
import static net.rovalio.CosmereAPI.commands.executor.PlayerStateCommandExecutor.*;
import static net.rovalio.CosmereAPI.commands.executor.RegistryCommandExecutor.showRegistryContents;
import static net.rovalio.CosmereAPI.commands.executor.TornPagesCommandExecutor.showHeldTornPages;

public final class CosmereCommands {

    private CosmereCommands() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("cosmere")

                .then(Commands.literal("show")

                        .then(
                                ConnectionCommandTree
                                        .createShowBranch()
                        )

                        .then(
                                InvestedArtCommandTree
                                        .createShowBranch()
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

                        .then(
                                ConnectionCommandTree
                                        .createConfigBranch()
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

                        .then(
                                InvestedArtCommandTree
                                        .createConfigBranch()
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
                        )


                        .then(Commands.literal("reset")
                                .then(Commands.literal("stats")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(context ->
                                                        resetValues(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "targets")
                                                        )
                                                )
                                        )
                                )

                                .then(Commands.literal("player")

                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.literal("confirm")
                                                        .executes(context ->
                                                                resetPlayer(
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
