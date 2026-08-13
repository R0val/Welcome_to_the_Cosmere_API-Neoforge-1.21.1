package net.rovalio.CosmereAPI.registry.test;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rovalio.CosmereAPI.CosmereAPI;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.LocationDefinition;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;
import net.rovalio.CosmereAPI.registry.definition.ShardDefinition;


public final class TestDefinition {

    public static final DeferredRegister<PlanetDefinition> PLANETS =
            DeferredRegister.create(
                    CosmereRegistries.PLANET_REGISTRY,
                    CosmereAPI.MOD_ID
            );

    public static final DeferredRegister<OriginDefinition> ORIGINS =
            DeferredRegister.create(
                    CosmereRegistries.ORIGIN_REGISTRY,
                    CosmereAPI.MOD_ID
            );

    public static final DeferredRegister<ShardDefinition> SHARDS =
            DeferredRegister.create(
                    CosmereRegistries.SHARD_REGISTRY,
                    CosmereAPI.MOD_ID
            );

    public static final DeferredRegister<LocationDefinition> LOCATIONS =
            DeferredRegister.create(
                    CosmereRegistries.LOCATION_REGISTRY,
                    CosmereAPI.MOD_ID
            );

    /// Planet key test
    public static final ResourceLocation TEST_PLANET_ID =
            ResourceLocation.fromNamespaceAndPath(
                    CosmereAPI.MOD_ID,
                    "test_planet"
            );

    public static final ResourceKey<PlanetDefinition> TEST_PLANET_KEY =
            ResourceKey.create(
                    CosmereRegistries.PLANET_REGISTRY_KEY,
                    TEST_PLANET_ID
            );

    /// Planet test
    public static final DeferredHolder<PlanetDefinition, PlanetDefinition> TEST_PLANET =
            PLANETS.register(
                    "test_planet",
                    () -> new PlanetDefinition(
                            Level.OVERWORLD,
                            false
                            )
            );

    /// Origin test
    public static final DeferredHolder<OriginDefinition, OriginDefinition> TEST_ORIGIN_A =
            ORIGINS.register(
                    "test_origin_a",
                    () -> new OriginDefinition(
                            TEST_PLANET_KEY,
                            false
                    )
            );

    public static final DeferredHolder<OriginDefinition, OriginDefinition> TEST_ORIGIN_B =
            ORIGINS.register(
                    "test_origin_b",
                    () -> new OriginDefinition(
                            TEST_PLANET_KEY,
                            false
                    )
            );

    public static final DeferredHolder<ShardDefinition, ShardDefinition> TEST_SHARD =
            SHARDS.register(
                    "test_shard",
                    ShardDefinition::new
            );

    public static final DeferredHolder<LocationDefinition, LocationDefinition> TEST_LOCATION =
            LOCATIONS.register(
                    "test_location",
                    LocationDefinition::new
            );

    private TestDefinition() {
    }


    public static void register(IEventBus modBus) {
        PLANETS.register(modBus);
        ORIGINS.register(modBus);
        SHARDS.register(modBus);
        LOCATIONS.register(modBus);
    }
}
