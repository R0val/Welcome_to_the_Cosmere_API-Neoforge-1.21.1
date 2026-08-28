package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static net.rovalio.CosmereAPI.commands.executor.InvestedArtCommandExecutor.*;

public final class InvestedArtCommandTree {

    private InvestedArtCommandTree() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack>
    createShowBranch() {

        return Commands.literal("investedarts")

                .then(
                        Commands.literal("list")

                                .then(
                                        Commands.argument(
                                                        "targets",
                                                        EntityArgument.players()
                                                )

                                                .executes(context ->
                                                        listInvestedArts(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(
                                                                        context,
                                                                        "targets"
                                                                )
                                                        )
                                                )
                                )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack>
    createConfigBranch() {

        return Commands.literal("investedarts")

                .then(
                        createGrantBranch()
                )

                .then(
                        createRevokeBranch()
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createGrantBranch() {

        var targetsBranch =
                Commands.argument(
                        "targets",
                        EntityArgument.players()
                );

        /*
         * Literal add-on branches take priority over
         * the generic ResourceLocation argument.
         */
        InvestedArtCommandRegistry
                .appendGrantBranches(targetsBranch);

        targetsBranch.then(
                Commands.argument(
                                "invested_art",
                                ResourceLocationArgument.id()
                        )

                        .suggests(
                                (context, builder) ->
                                        SharedSuggestionProvider
                                                .suggestResource(
                                                        CosmereRegistries
                                                                .INVESTED_ART_REGISTRY
                                                                .keySet(),
                                                        builder
                                                )
                        )

                        .executes(context ->
                                grantInvestedArt(
                                        context.getSource(),
                                        EntityArgument.getPlayers(
                                                context,
                                                "targets"
                                        ),
                                        ResourceLocationArgument.getId(
                                                context,
                                                "invested_art"
                                        )
                                )
                        )
        );

        return Commands.literal("grant")
                .then(targetsBranch);
    }

    private static LiteralArgumentBuilder<CommandSourceStack>
    createRevokeBranch() {

        var targetsBranch =
                Commands.argument(
                        "targets",
                        EntityArgument.players()
                );

        InvestedArtCommandRegistry
                .appendRevokeBranches(targetsBranch);

        targetsBranch.then(
                Commands.argument(
                                "invested_art",
                                ResourceLocationArgument.id()
                        )

                        .suggests(
                                (context, builder) ->
                                        SharedSuggestionProvider
                                                .suggestResource(
                                                        getRevocableArts(
                                                                context
                                                        ),
                                                        builder
                                                )
                        )

                        .executes(context ->
                                revokeInvestedArt(
                                        context.getSource(),
                                        EntityArgument.getPlayers(
                                                context,
                                                "targets"
                                        ),
                                        ResourceLocationArgument.getId(
                                                context,
                                                "invested_art"
                                        )
                                )
                        )
        );

        return Commands.literal("revoke")
                .then(targetsBranch);
    }

    private static Set<ResourceLocation> getRevocableArts(
            com.mojang.brigadier.context.CommandContext<
                    CommandSourceStack
                    > context
    ) throws CommandSyntaxException {

        Set<ResourceLocation> revocableArts =
                new TreeSet<>(
                        Comparator.comparing(
                                ResourceLocation::toString
                        )
                );

        /*
         * Registered Arts are suggested even when none
         * of the selected players currently possesses them.
         */
        revocableArts.addAll(
                CosmereRegistries
                        .INVESTED_ART_REGISTRY
                        .keySet()
        );

        /*
         * Player-owned IDs are also included, allowing
         * orphaned Arts to appear in autocomplete.
         */
        for (var player :
                EntityArgument.getPlayers(
                        context,
                        "targets"
                )) {

            revocableArts.addAll(
                    CosmereAttachments.get(player)
                            .getSpiritweb()
                            .getInvestedArts()
            );
        }

        return revocableArts;
    }
}