package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;

import java.util.Comparator;
import java.util.List;

public final class OnboardingRegistryAccess {

    private OnboardingRegistryAccess() {
    }

    public static List<ResourceLocation> getSelectablePlanets() {

        return CosmereRegistries.PLANET_REGISTRY
                .entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue()
                                .selectableInOnboarding()
                )
                .filter(entry ->
                        !getSelectableOriginsForPlanet(
                                entry.getKey()
                                        .location()
                        ).isEmpty()
                )
                .map(entry ->
                        entry.getKey().location()
                )
                .sorted(
                        Comparator.comparing(
                                ResourceLocation::toString
                        )
                )
                .toList();
    }

    public static List<ResourceLocation> getSelectableOriginsForPlanet(
            ResourceLocation planetId
    ) {

        if (planetId == null) {
            return List.of();
        }

        PlanetDefinition planet =
                CosmereRegistries.PLANET_REGISTRY
                        .get(planetId);

        if (planet == null
                || !planet.selectableInOnboarding()) {
            return List.of();
        }

        return CosmereRegistries.ORIGIN_REGISTRY
                .entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue()
                                .selectableInOnboarding()
                )
                .filter(entry ->
                        OriginInitializationRegistry
                                .hasInitializer(
                                        entry.getKey()
                                                .location()
                                )
                )
                .filter(entry ->
                        entry.getValue()
                                .planet()
                                .location()
                                .equals(planetId)
                )
                .map(entry ->
                        entry.getKey()
                                .location()
                )
                .sorted(
                        Comparator.comparing(
                                ResourceLocation::toString
                        )
                )
                .toList();
    }

    public static boolean hasAvailableOrigins() {
        return !getSelectablePlanets().isEmpty();
    }
}