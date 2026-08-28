package net.rovalio.CosmereAPI.commands.executor;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.commands.InvestedArtCommandRegistry;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.SpiritwebData;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class InvestedArtCommandExecutor {

    private InvestedArtCommandExecutor() {
    }

    public static int listInvestedArts(
            CommandSourceStack source,
            Collection<ServerPlayer> targets
    ) {
        int listedPlayers = 0;

        for (ServerPlayer player : targets) {

            List<ResourceLocation> investedArts =
                    CosmereAttachments.get(player)
                            .getSpiritweb()
                            .getInvestedArts()
                            .stream()
                            .sorted(
                                    Comparator.comparing(
                                            ResourceLocation::toString
                                    )
                            )
                            .toList();

            source.sendSuccess(
                    () -> Component.literal(
                            "§1------ §6INVESTED ARTS: §f"
                                    + player.getGameProfile()
                                    .getName()
                                    + " §1------"
                    ),
                    false
            );

            if (investedArts.isEmpty()) {

                source.sendSuccess(
                        () -> Component.literal(
                                "§7No Invested Arts registered."
                        ),
                        false
                );

                listedPlayers++;
                continue;
            }

            for (ResourceLocation artId :
                    investedArts) {

                boolean orphaned =
                        !CosmereRegistries
                                .INVESTED_ART_REGISTRY
                                .containsKey(artId);

                String orphanedMarker =
                        orphaned
                                ? " §c[ORPHANED]"
                                : "";

                source.sendSuccess(
                        () -> Component.literal(
                                "§7- §d"
                                        + artId
                                        + orphanedMarker
                        ),
                        false
                );
            }

            listedPlayers++;
        }

        return listedPlayers;
    }

    public static int grantInvestedArt(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ResourceLocation artId
    ) {
        /*
         * A specialised branch must perform the complete
         * operation through its add-on handler.
         */
        if (InvestedArtCommandRegistry
                .hasExtension(artId)) {

            source.sendFailure(
                    Component.literal(
                            "This command is handled by an "
                                    + "add-on and requires its "
                                    + "specific syntax: "
                                    + artId
                    )
            );

            return 0;
        }

        if (!CosmereRegistries
                .INVESTED_ART_REGISTRY
                .containsKey(artId)) {

            source.sendFailure(
                    Component.literal(
                            "Unknown Invested Art: "
                                    + artId
                    )
            );

            return 0;
        }

        int modifiedPlayers = 0;

        for (ServerPlayer player : targets) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            boolean granted =
                    spiritweb.grantInvestedArt(artId);

            if (!granted) {

                source.sendFailure(
                        Component.literal(
                                "Player "
                                        + player.getGameProfile()
                                        .getName()
                                        + " already has Invested Art "
                                        + artId
                        )
                );

                continue;
            }

            source.sendSuccess(
                    () -> Component.literal(
                            "Invested Art "
                                    + artId
                                    + " granted to "
                                    + player.getGameProfile()
                                    .getName()
                    ),
                    true
            );

            modifiedPlayers++;
        }

        return modifiedPlayers;
    }

    public static int revokeInvestedArt(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ResourceLocation artId
    ) {
        if (InvestedArtCommandRegistry
                .hasExtension(artId)) {

            source.sendFailure(
                    Component.literal(
                            "This command is handled by an "
                                    + "add-on and requires its "
                                    + "specific syntax: "
                                    + artId
                    )
            );

            return 0;
        }

        int modifiedPlayers = 0;

        for (ServerPlayer player : targets) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            /*
             * Registry validation is deliberately omitted.
             * This permits orphaned Arts to be removed.
             */
            boolean revoked =
                    spiritweb.revokeInvestedArt(artId);

            if (!revoked) {

                source.sendFailure(
                        Component.literal(
                                "Player "
                                        + player.getGameProfile()
                                        .getName()
                                        + " does not have Invested Art "
                                        + artId
                        )
                );

                continue;
            }

            source.sendSuccess(
                    () -> Component.literal(
                            "Invested Art "
                                    + artId
                                    + " revoked from "
                                    + player.getGameProfile()
                                    .getName()
                    ),
                    true
            );

            modifiedPlayers++;
        }

        return modifiedPlayers;
    }
}