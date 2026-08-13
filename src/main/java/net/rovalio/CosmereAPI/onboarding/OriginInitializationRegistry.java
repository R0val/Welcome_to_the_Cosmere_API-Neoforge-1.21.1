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

        return INITIALIZERS.containsKey(
                originId
        );
    }

    //Initialization
    public static boolean initializeIfPresent(
            ServerPlayer player,
            ResourceLocation planetId,
            ResourceLocation originId
    ) {

        OriginInitializer initializer =
                INITIALIZERS.get(originId);

        if (initializer == null) {

            // An origin is allowed to exist without
            // having initialization behaviour yet.
            return true;
        }

        return initializer.initialize(
                player,
                planetId,
                originId
        );
    }
}