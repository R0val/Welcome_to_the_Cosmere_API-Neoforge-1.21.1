package net.rovalio.CosmereAPI.registry.definition;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;

public record PlanetDefinition (
        ResourceKey<Level> dimension,
        boolean selectableInOnboarding
) {

    public PlanetDefinition(ResourceKey<Level> dimension) {
        this(dimension, true);
    }

    public PlanetDefinition {
        Objects.requireNonNull(
                dimension,
                "Planet dimension cannot be null"
        );
    }
}