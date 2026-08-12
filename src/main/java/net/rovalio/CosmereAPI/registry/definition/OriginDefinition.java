package net.rovalio.CosmereAPI.registry.definition;

import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public record OriginDefinition (ResourceKey<PlanetDefinition> race) {

    public OriginDefinition {
        Objects.requireNonNull(race, "Origin cannot be null");
    }
}
