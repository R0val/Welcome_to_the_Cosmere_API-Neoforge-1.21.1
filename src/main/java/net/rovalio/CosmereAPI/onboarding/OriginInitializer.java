package net.rovalio.CosmereAPI.onboarding;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface OriginInitializer {

    OnboardingResult initialize(
            ServerPlayer player,
            ResourceLocation planetId,
            ResourceLocation originId
    );

    static OriginInitializer noOp() {
        return (
                player,
                planetId,
                originId
        ) -> OnboardingResult.SUCCESS;
    }
}