package net.rovalio.CosmereAPI.registry.definition;

import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public record OriginDefinition (ResourceKey<PlanetDefinition> planet) {

    public OriginDefinition {
        Objects.requireNonNull(planet, "Origin cannot be null");
    }
}
