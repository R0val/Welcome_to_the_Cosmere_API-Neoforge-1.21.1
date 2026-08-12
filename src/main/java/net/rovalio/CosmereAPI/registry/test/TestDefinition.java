package net.rovalio.CosmereAPI.registry.test;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rovalio.CosmereAPI.CosmereAPI;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;

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
                    () -> new PlanetDefinition(Level.OVERWORLD)
            );

    /// Origin test
    public static final DeferredHolder<OriginDefinition, OriginDefinition> TEST_ORIGIN_A =
            ORIGINS.register(
                    "test_origin_a",
                    () -> new OriginDefinition(TEST_PLANET_KEY)
            );

    public static final DeferredHolder<OriginDefinition, OriginDefinition> TEST_ORIGIN_B =
            ORIGINS.register(
                    "test_origin_b",
                    () -> new OriginDefinition(TEST_PLANET_KEY)
            );


    private void TestDefinitions() {
    }


    public static void register(IEventBus modBus) {
        PLANETS.register(modBus);
        ORIGINS.register(modBus);
    }
}
