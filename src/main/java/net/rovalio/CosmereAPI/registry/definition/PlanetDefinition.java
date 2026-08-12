package net.rovalio.CosmereAPI.registry.definition;

import net.minecraft.resources.ResourceKey;

import java.util.Objects;
import java.util.logging.Level;

public record PlanetDefinition (ResourceKey<Level> dimension){

    public PlanetDefinition {
        Objects.requireNonNull(dimension, "Planet definition cannot be null");
    }
}
