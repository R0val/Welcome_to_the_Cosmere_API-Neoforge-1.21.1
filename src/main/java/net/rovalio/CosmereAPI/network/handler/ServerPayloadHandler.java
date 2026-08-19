package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.network.payload.*;
import net.rovalio.CosmereAPI.onboarding.OnboardingManager;
import net.rovalio.CosmereAPI.onboarding.OnboardingResult;
import org.slf4j.Logger;

public final class ServerPayloadHandler {

    private static final Logger LOGGER =
            LogUtils.getLogger();

    private ServerPayloadHandler() {
    }

    public static void handlePong(
            NetworkPongC2SPayload payload,
            IPayloadContext context
    ) {
        LOGGER.info(
                "[Cosmere API Networking] C2S pong received on thread: {}",
                Thread.currentThread().getName()
        );
    }

    // SELECT SPECIFIC ORIGIN
    public static void handleSelectOrigin(
            SelectOriginC2SPayload payload,
            IPayloadContext context
    ) {
        LOGGER.info(
                "[Cosmere API Onboarding] Origin selection received: {} | Thread: {}",
                payload.originId(),
                Thread.currentThread().getName()
        );

        context.enqueueWork(() -> {

            if (!(context.player()
                    instanceof ServerPlayer player)) {
                return;
            }

            OnboardingResult result =
                    executeSafely(
                            () -> OnboardingManager.selectOrigin(
                                    player,
                                    payload.originId()
                            ),
                            player,
                            "Specific origin selection"
                    );

            sendOnboardingResult(
                    player,
                    result
            );

            LOGGER.info(
                    "[Cosmere API Onboarding] Origin {} returned {} for player {}",
                    payload.originId(),
                    result,
                    player.getGameProfile().getName()
            );

        }).exceptionally(exception -> {

            LOGGER.error(
                    "[Cosmere API Onboarding] Failed to process origin selection",
                    exception
            );

            return null;
        });
    }

    // RANDOM GLOBAL ORIGIN
    public static void handleRandomGlobalOrigin(
            RandomGlobalOriginC2SPayload payload,
            IPayloadContext context
    ) {
        LOGGER.info(
                "[Cosmere API Onboarding] Global random origin requested | Thread: {}",
                Thread.currentThread().getName()
        );

        context.enqueueWork(() -> {

            if (!(context.player()
                    instanceof ServerPlayer player)) {
                return;
            }

            OnboardingResult result =
                    executeSafely(
                            () -> OnboardingManager
                                    .selectRandomOriginGlobal(
                                            player
                                    ),
                            player,
                            "Global random origin selection"
                    );

            sendOnboardingResult(
                    player,
                    result
            );

            LOGGER.info(
                    "[Cosmere API Onboarding] Global random origin returned {} for player {}",
                    result,
                    player.getGameProfile().getName()
            );

        }).exceptionally(exception -> {

            LOGGER.error(
                    "[Cosmere API Onboarding] Failed to process global random origin selection",
                    exception
            );

            return null;
        });
    }

    // RANDOM ORIGIN FOR SELECTED PLANET
    public static void handleRandomPlanetOrigin(
            RandomPlanetOriginC2SPayload payload,
            IPayloadContext context
    ) {
        LOGGER.info(
                "[Cosmere API Onboarding] Random origin requested for planet: {} | Thread: {}",
                payload.planetId(),
                Thread.currentThread().getName()
        );

        context.enqueueWork(() -> {

            if (!(context.player()
                    instanceof ServerPlayer player)) {
                return;
            }

            OnboardingResult result =
                    executeSafely(
                            () -> OnboardingManager
                                    .selectRandomOriginForPlanet(
                                            player,
                                            payload.planetId()
                                    ),
                            player,
                            "Planet random origin selection"
                    );

            sendOnboardingResult(
                    player,
                    result
            );

            LOGGER.info(
                    "[Cosmere API Onboarding] Random origin for planet {} returned {} for player {}",
                    payload.planetId(),
                    result,
                    player.getGameProfile().getName()
            );

        }).exceptionally(exception -> {

            LOGGER.error(
                    "[Cosmere API Onboarding] Failed to process random planet origin selection",
                    exception
            );

            return null;
        });
    }

    private static OnboardingResult executeSafely(
            java.util.function.Supplier<
                    OnboardingResult
                    > operation,
            ServerPlayer player,
            String operationName
    ) {
        try {
            OnboardingResult result =
                    operation.get();

            return result != null
                    ? result
                    : OnboardingResult.INTERNAL_ERROR;

        } catch (RuntimeException exception) {
            LOGGER.error(
                    "[Cosmere API Onboarding] {} failed for player {}",
                    operationName,
                    player.getGameProfile().getName(),
                    exception
            );

            return OnboardingResult.INTERNAL_ERROR;
        }
    }

    private static void sendOnboardingResult(
            ServerPlayer player,
            OnboardingResult result
    ) {
        PacketDistributor.sendToPlayer(
                player,
                new OnboardingResultS2CPayload(
                        result
                )
        );
    }
}