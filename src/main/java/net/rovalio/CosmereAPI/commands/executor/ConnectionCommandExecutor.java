package net.rovalio.CosmereAPI.commands.executor;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.player.ConnectionData;
import net.rovalio.CosmereAPI.player.ConnectionTargetValidator;
import net.rovalio.CosmereAPI.player.ConnectionType;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.SpiritwebData;

import java.util.Collection;
import java.util.List;

public final class ConnectionCommandExecutor {

    private ConnectionCommandExecutor() {
    }

    public static int listConnections(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ConnectionType type
    ) {
        int listedPlayers = 0;

        for (ServerPlayer player : targets) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            List<ConnectionData> matchingConnections =
                    spiritweb.getConnections()
                            .stream()
                            .filter(connection ->
                                    connection.type() == type
                            )
                            .toList();

            source.sendSuccess(
                    () -> Component.literal(
                            "§1------ §6"
                                    + type.name()
                                    + " CONNECTIONS: §f"
                                    + player.getGameProfile().getName()
                                    + " §1------"
                    ),
                    false
            );

            if (matchingConnections.isEmpty()) {
                source.sendSuccess(
                        () -> Component.literal(
                                "§7No Connections of this type."
                        ),
                        false
                );
            } else {
                for (ConnectionData connection :
                        matchingConnections) {

                    boolean orphaned =
                            ConnectionTargetValidator
                                    .isOrphaned(connection);

                    String orphanedMarker =
                            orphaned
                                    ? " §c[ORPHANED]"
                                    : "";

                    source.sendSuccess(
                            () -> Component.literal(
                                    "§7- §f"
                                            + connection.target()
                                            + " §7| Strength: §e"
                                            + connection.strength()
                                            + orphanedMarker
                            ),
                            false
                    );
                }
            }

            listedPlayers++;
        }

        return listedPlayers;
    }

    public static int getConnection(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ConnectionType type,
            ResourceLocation targetId
    ) {
        String canonicalTarget =
                targetId.toString();

        int foundConnections = 0;

        for (ServerPlayer player : targets) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            ConnectionData connection =
                    spiritweb.getConnection(
                            type,
                            canonicalTarget
                    );

            if (connection == null) {
                source.sendFailure(
                        Component.literal(
                                "Player "
                                        + player.getGameProfile().getName()
                                        + " has no "
                                        + type.name()
                                        + " Connection to "
                                        + canonicalTarget
                        )
                );

                continue;
            }

            boolean orphaned =
                    ConnectionTargetValidator
                            .isOrphaned(connection);

            String orphanedMarker =
                    orphaned
                            ? " §c[ORPHANED]"
                            : "";

            source.sendSuccess(
                    () -> Component.literal(
                            "§f"
                                    + player.getGameProfile().getName()
                                    + " §7| "
                                    + type.name()
                                    + " -> §f"
                                    + canonicalTarget
                                    + " §7| Strength: §e"
                                    + connection.strength()
                                    + orphanedMarker
                    ),
                    false
            );

            foundConnections++;
        }

        return foundConnections;
    }

    public static int setConnection(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ConnectionType type,
            ResourceLocation targetId,
            int strength
    ) {

        if (!ConnectionTargetValidator.isValid(
                type,
                targetId
        )) {
            source.sendFailure(
                    Component.literal(
                            "Unknown or unsupported Connection target: "
                                    + type.name()
                                    + " -> "
                                    + targetId
                    )
            );

            return 0;
        }

        int modifiedPlayers = 0;

        for (ServerPlayer player : targets) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            spiritweb.setConnection(
                    type,
                    targetId.toString(),
                    strength
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "Connection "
                                    + type.name()
                                    + " -> "
                                    + targetId
                                    + " set to "
                                    + strength
                                    + " for "
                                    + player.getGameProfile().getName()
                    ),
                    true
            );

            modifiedPlayers++;
        }

        return modifiedPlayers;
    }

    public static int eraseConnection(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ConnectionType type,
            ResourceLocation targetId
    ) {
        String canonicalTarget =
                targetId.toString();

        int modifiedPlayers = 0;

        for (ServerPlayer player : targets) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            ConnectionData connection =
                    spiritweb.getConnection(
                            type,
                            canonicalTarget
                    );

            if (connection == null) {
                source.sendFailure(
                        Component.literal(
                                "Player "
                                        + player.getGameProfile().getName()
                                        + " has no "
                                        + type.name()
                                        + " Connection to "
                                        + canonicalTarget
                        )
                );

                continue;
            }

            /*
             * No se valida el registry al eliminar.
             * Esto permite borrar Conexiones huérfanas.
             */
            spiritweb.removeConnection(connection);

            source.sendSuccess(
                    () -> Component.literal(
                            "Connection "
                                    + type.name()
                                    + " -> "
                                    + canonicalTarget
                                    + " erased for "
                                    + player.getGameProfile().getName()
                    ),
                    true
            );

            modifiedPlayers++;
        }

        return modifiedPlayers;
    }
}