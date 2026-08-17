package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;

import java.util.List;

public final class OnboardingManager {
    private OnboardingManager (){

    }

    public static OnboardingResult selectOrigin(
            ServerPlayer player,
            ResourceLocation originId
    ) {
        if (player == null || originId == null) {
            return OnboardingResult.INVALID_REQUEST;
        }

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        if (data.isOnboardingComplete()) {
            return OnboardingResult.ALREADY_COMPLETE;
        }

        OriginDefinition origin =
                CosmereRegistries.ORIGIN_REGISTRY
                        .get(originId);

        if (origin == null) {
            return OnboardingResult.UNKNOWN_ORIGIN;
        }

        if (!origin.selectableInOnboarding()) {
            return OnboardingResult.ORIGIN_NOT_SELECTABLE;
        }

        ResourceLocation planetId =
                origin.planet()
                        .location();

        PlanetDefinition planet =
                CosmereRegistries.PLANET_REGISTRY
                        .get(planetId);

        if (planet == null) {
            return OnboardingResult.UNKNOWN_PLANET;
        }

        if (!planet.selectableInOnboarding()) {
            return OnboardingResult.PLANET_NOT_SELECTABLE;
        }

        OnboardingResult initializationResult =
                OriginInitializationRegistry
                        .initialize(
                                player,
                                planetId,
                                originId
                        );

        if (!initializationResult.isSuccess()) {
            return initializationResult;
        }

        data.completeOnboarding(
                planetId,
                originId
        );

        return OnboardingResult.SUCCESS;
    }

    private static List<ResourceLocation> getOriginsForPlanet(
            ResourceLocation planetId
    ) {

        return OnboardingRegistryAccess
                .getSelectableOriginsForPlanet(
                        planetId
                );
    }

    public static OnboardingResult
    selectRandomOriginForPlanet(
            ServerPlayer player,
            ResourceLocation planetId
    ) {
        if (player == null || planetId == null) {
            return OnboardingResult.INVALID_REQUEST;
        }

        PlanetDefinition planet =
                CosmereRegistries.PLANET_REGISTRY
                        .get(planetId);

        if (planet == null) {
            return OnboardingResult.UNKNOWN_PLANET;
        }

        if (!planet.selectableInOnboarding()) {
            return OnboardingResult.PLANET_NOT_SELECTABLE;
        }

        List<ResourceLocation> origins =
                getOriginsForPlanet(planetId);

        if (origins.isEmpty()) {
            return OnboardingResult.NO_AVAILABLE_ORIGINS;
        }

        ResourceLocation selectedOrigin =
                origins.get(
                        player.getRandom()
                                .nextInt(origins.size())
                );

        return selectOrigin(
                player,
                selectedOrigin
        );
    }

    public static OnboardingResult
    selectRandomOriginGlobal(
            ServerPlayer player
    ) {
        if (player == null) {
            return OnboardingResult.INVALID_REQUEST;
        }

        List<ResourceLocation> validPlanets =
                OnboardingRegistryAccess
                        .getSelectablePlanets()
                        .stream()
                        .filter(planetId ->
                                !getOriginsForPlanet(
                                        planetId
                                ).isEmpty()
                        )
                        .toList();

        if (validPlanets.isEmpty()) {
            return OnboardingResult
                    .NO_AVAILABLE_PLANETS;
        }

        ResourceLocation selectedPlanet =
                validPlanets.get(
                        player.getRandom()
                                .nextInt(
                                        validPlanets.size()
                                )
                );

        return selectRandomOriginForPlanet(
                player,
                selectedPlanet
        );
    }
}
