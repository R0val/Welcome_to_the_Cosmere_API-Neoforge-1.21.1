package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;

import java.util.List;

public class OnboardingManager {

    public static boolean selectOrigin(
            ServerPlayer player,
            ResourceLocation originId
    ) {

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        // Never allows a second election
        if (data.isOnboardingComplete()) {
            return false;
        }

        OriginDefinition origin =
                CosmereRegistries.ORIGIN_REGISTRY.get(originId);

        if (origin == null) {
            return false;
        }

        ResourceLocation planetId =
                origin.planet().location();

        PlanetDefinition planet =
                CosmereRegistries.PLANET_REGISTRY.get(planetId);

        if (planet == null) {
            return false;
        }

        //Delegate origin initialization to Addons
        boolean initialized =
                OriginInitializationRegistry
                        .initializeIfPresent(
                                player,
                                planetId,
                                originId
                        );

        if (!initialized) {
            return false;
        }

        data.setOriginSelection(
                planetId,
                originId
        );

        data.setOnboardingComplete(true);

        return true;
    }

    private static List<ResourceLocation> getOriginsForPlanet(
            ResourceLocation planetId
    ) {

        return OnboardingRegistryAccess
                .getSelectableOriginsForPlanet(
                        planetId
                );
    }

    public static boolean selectRandomOriginForPlanet(
            ServerPlayer player,
            ResourceLocation planetId
    ) {

        if (!CosmereRegistries.PLANET_REGISTRY.containsKey(planetId)) {
            return false;
        }

        List<ResourceLocation> origins =
                getOriginsForPlanet(planetId);

        if (origins.isEmpty()) {
            return false;
        }

        ResourceLocation selectedOrigin =
                origins.get(
                        player.getRandom().nextInt(origins.size())
                );

        return selectOrigin(
                player,
                selectedOrigin
        );
    }

    public static boolean selectRandomOriginGlobal(
            ServerPlayer player
    ) {

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
            return false;
        }

        ResourceLocation selectedPlanet =
                validPlanets.get(
                        player.getRandom()
                                .nextInt(validPlanets.size())
                );

        return selectRandomOriginForPlanet(
                player,
                selectedPlanet
        );
    }
}
