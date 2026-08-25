package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class OriginConnectionRegistry {

    private static final Map<
            ResourceLocation,
            List<ConnectionData>
            > CONNECTIONS_BY_ORIGIN =
            new HashMap<>();

    private OriginConnectionRegistry() {
    }

    public static void register(
            ResourceLocation originId,
            ConnectionData... connections
    ) {
        Objects.requireNonNull(
                originId,
                "Origin ID cannot be null"
        );

        Objects.requireNonNull(
                connections,
                "Origin Connections cannot be null"
        );

        if (CONNECTIONS_BY_ORIGIN.containsKey(originId)) {
            throw new IllegalStateException(
                    "Connections are already registered for origin: "
                            + originId
            );
        }

        if (connections.length == 0) {
            throw new IllegalArgumentException(
                    "At least one Connection must be registered for origin: "
                            + originId
            );
        }

        List<ConnectionData> normalizedConnections =
                new ArrayList<>();

        for (ConnectionData connection : connections) {

            Objects.requireNonNull(
                    connection,
                    "Registered Connection cannot be null"
            );

            if (!isSupportedType(connection.type())) {
                throw new IllegalArgumentException(
                        "Unsupported Connection type for origin "
                                + originId
                                + ": "
                                + connection.type()
                );
            }

            ResourceLocation targetId =
                    ResourceLocation.tryParse(
                            connection.target()
                    );

            if (targetId == null) {
                throw new IllegalArgumentException(
                        "Invalid Connection target for origin "
                                + originId
                                + ": "
                                + connection.target()
                );
            }

            if (connection.strength()
                    <= ConnectionData.MIN_STRENGTH) {

                throw new IllegalArgumentException(
                        "Origin Connections must have a strength greater than 0"
                );
            }

            ConnectionData normalizedConnection =
                    new ConnectionData(
                            connection.type(),
                            targetId.toString(),
                            connection.strength()
                    );

            boolean duplicate =
                    normalizedConnections.stream()
                            .anyMatch(existing ->
                                    existing.hasKey(
                                            normalizedConnection.type(),
                                            normalizedConnection.target()
                                    )
                            );

            if (duplicate) {
                throw new IllegalStateException(
                        "Duplicate Connection key registered for origin "
                                + originId
                                + ": "
                                + normalizedConnection.type()
                                + " -> "
                                + normalizedConnection.target()
                );
            }

            normalizedConnections.add(
                    normalizedConnection
            );
        }

        CONNECTIONS_BY_ORIGIN.put(
                originId,
                List.copyOf(normalizedConnections)
        );
    }

    public static List<ConnectionData> getConnections(
            ResourceLocation originId
    ) {
        if (originId == null) {
            return List.of();
        }

        return CONNECTIONS_BY_ORIGIN.getOrDefault(
                originId,
                List.of()
        );
    }

    public static boolean hasConnections(
            ResourceLocation originId
    ) {
        return originId != null
                && CONNECTIONS_BY_ORIGIN.containsKey(
                originId
        );
    }

    private static boolean isSupportedType(
            ConnectionType type
    ) {
        return type == ConnectionType.PLANET
                || type == ConnectionType.LOCATION
                || type == ConnectionType.SHARD;
    }

    public static boolean applyConnections(
            ServerPlayer player,
            ResourceLocation originId
    ) {
        if (player == null || originId == null) {
            return false;
        }

        List<ConnectionData> originConnections =
                getConnections(originId);

        // An origin without entries is valid
        if (originConnections.isEmpty()) {
            return true;
        }

        //Validation of all the declarations
        for (ConnectionData connection :
                originConnections) {

            ResourceLocation targetId =
                    ResourceLocation.tryParse(
                            connection.target()
                    );

            if (targetId == null
                    || !ConnectionTargetValidator.isValid(
                    connection.type(),
                    targetId
            )) {

                return false;
            }
        }

        SpiritwebData spiritweb =
                CosmereAttachments.get(player)
                        .getSpiritweb();

        for (ConnectionData declaredConnection :
                originConnections) {

            ConnectionData existingConnection =
                    spiritweb.getConnection(
                            declaredConnection.type(),
                            declaredConnection.target()
                    );

            // Strength is only created or increased, never decreased
            if (existingConnection == null
                    || existingConnection.strength()
                    < declaredConnection.strength()) {

                spiritweb.setConnection(
                        declaredConnection.type(),
                        declaredConnection.target(),
                        declaredConnection.strength()
                );
            }
        }

        return true;
    }
}