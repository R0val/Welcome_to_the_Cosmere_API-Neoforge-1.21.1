package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

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

        return CosmereRegistries.ORIGIN_REGISTRY
                .entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue()
                                .selectableInOnboarding()
                )
                .filter(entry ->
                        entry.getValue()
                                .planet()
                                .location()
                                .equals(planetId)
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
}