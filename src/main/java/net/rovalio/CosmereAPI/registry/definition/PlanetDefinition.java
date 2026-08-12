package net.rovalio.CosmereAPI.registry.definition;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;

public record PlanetDefinition (ResourceKey<Level> dimension){

    public PlanetDefinition {
        Objects.requireNonNull(dimension, "Planet definition cannot be null");
    }
}
