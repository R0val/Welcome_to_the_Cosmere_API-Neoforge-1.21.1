package net.rovalio.CosmereAPI.player;

import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

public final class ConnectionTargetValidator {

    private ConnectionTargetValidator() {
    }

    public static boolean isValid(
            ConnectionType type,
            ResourceLocation targetId
    ) {

        if (type == null || targetId == null) {
            return false;
        }

        return switch (type) {

            case PLANET ->
                    CosmereRegistries.PLANET_REGISTRY
                            .containsKey(targetId);

            case SHARD ->
                    CosmereRegistries.SHARD_REGISTRY
                            .containsKey(targetId);

            case LOCATION ->
                    CosmereRegistries.LOCATION_REGISTRY
                            .containsKey(targetId);

            default ->
                    false;
        };
    }

    public static boolean isOrphaned(
            ConnectionData connection
    ) {
        if (connection == null) {
            return false;
        }

        ResourceLocation targetId =
                ResourceLocation.tryParse(
                        connection.target()
                );

        if (targetId == null) {
            return true;
        }

        return !isValid(
                connection.type(),
                targetId
        );
    }
}