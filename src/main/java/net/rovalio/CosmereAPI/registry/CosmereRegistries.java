package net.rovalio.CosmereAPI.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.rovalio.CosmereAPI.CosmereAPI;
import net.rovalio.CosmereAPI.registry.definition.*;

import net.minecraft.core.Registry;

public final class CosmereRegistries {

    //Planets
    public static final ResourceKey<Registry<PlanetDefinition>> PLANET_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "planet"
                    )
            );

    public static final Registry<PlanetDefinition> PLANET_REGISTRY =
            new RegistryBuilder<>(PLANET_REGISTRY_KEY)
                    .sync(true)
                    .create();

    //Origins
    public static final ResourceKey<Registry<OriginDefinition>> ORIGIN_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "origin"
                    )
            );

    public static final Registry<OriginDefinition> ORIGIN_REGISTRY =
            new RegistryBuilder<>(ORIGIN_REGISTRY_KEY)
                    .sync(true)
                    .create();


    //Shards
    public static final ResourceKey<Registry<ShardDefinition>> SHARD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "shard"
                    )
            );

    public static final Registry<ShardDefinition> SHARD_REGISTRY =
            new RegistryBuilder<>(SHARD_REGISTRY_KEY)
                    .sync(true)
                    .create();

    //Location
    public static final ResourceKey<Registry<LocationDefinition>> LOCATION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "location"
                    )
            );

    public static final Registry<LocationDefinition> LOCATION_REGISTRY =
            new RegistryBuilder<>(LOCATION_REGISTRY_KEY)
                    .sync(true)
                    .create();

    // Invested Arts
    public static final ResourceKey<
            Registry<InvestedArtDefinition>
            > INVESTED_ART_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "invested_art"
                    )
            );

    public static final Registry<
            InvestedArtDefinition
            > INVESTED_ART_REGISTRY =
            new RegistryBuilder<>(
                    INVESTED_ART_REGISTRY_KEY
            )
                    .sync(true)
                    .create();

    private CosmereRegistries() {
    }


    public static void registerRegistries(NewRegistryEvent event) {
        event.register(PLANET_REGISTRY);
        event.register(ORIGIN_REGISTRY);
        event.register(SHARD_REGISTRY);
        event.register(LOCATION_REGISTRY);
        event.register(INVESTED_ART_REGISTRY);
    }
}
