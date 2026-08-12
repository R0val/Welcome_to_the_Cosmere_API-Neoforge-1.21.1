package net.rovalio.CosmereAPI.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.rovalio.CosmereAPI.CosmereAPI;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;

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


    private CosmereRegistries() {
    }


    public static void registerRegistries(NewRegistryEvent event) {
        event.register(PLANET_REGISTRY);
        event.register(ORIGIN_REGISTRY);
    }
}
