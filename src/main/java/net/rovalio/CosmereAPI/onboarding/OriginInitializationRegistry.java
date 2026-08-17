package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class OriginInitializationRegistry {

    private static final Map<ResourceLocation, OriginInitializer> INITIALIZERS =
            new HashMap<>();

    private OriginInitializationRegistry() {
    }

    //Registry
    public static void register(
            ResourceLocation originId,
            OriginInitializer initializer
    ) {

        Objects.requireNonNull(
                originId,
                "Origin ID cannot be null"
        );

        Objects.requireNonNull(
                initializer,
                "Origin initializer cannot be null"
        );

        if (INITIALIZERS.containsKey(originId)) {
            throw new IllegalStateException(
                    "An initializer is already registered for origin: "
                            + originId
            );
        }

        INITIALIZERS.put(
                originId,
                initializer
        );
    }


    public static boolean hasInitializer(
            ResourceLocation originId
    ) {
        return originId != null
                && INITIALIZERS.containsKey(
                originId
        );
    }

    //Initialization
    public static boolean initialize(
            ServerPlayer player,
            ResourceLocation planetId,
            ResourceLocation originId
    ) {
        if (player == null
                || planetId == null
                || originId == null) {
            return false;
        }

        OriginInitializer initializer =
                INITIALIZERS.get(originId);

        if (initializer == null) {
            return false;
        }

        return initializer.initialize(
                player,
                planetId,
                originId
        );
    }
}