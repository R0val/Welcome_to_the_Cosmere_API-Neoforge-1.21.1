package net.rovalio.CosmereAPI.registry.definition;

import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public record OriginDefinition(
        ResourceKey<PlanetDefinition> race,
        boolean selectableInOnboarding
) {

    public OriginDefinition(
            ResourceKey<PlanetDefinition> race
    ) {
        this(race, true);
    }

    public OriginDefinition {
        Objects.requireNonNull(
                race,
                "Origin planet cannot be null"
        );
    }
}