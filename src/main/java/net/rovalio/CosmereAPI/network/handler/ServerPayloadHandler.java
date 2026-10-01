package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.network.payload.*;
import net.rovalio.CosmereAPI.onboarding.OnboardingManager;
import net.rovalio.CosmereAPI.onboarding.OnboardingResult;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class ServerPayloadHandler {

    private static final Logger LOGGER =
            LogUtils.getLogger();

    private static final int REQUEST_COOLDOWN_TICKS = 10;

    private static final Map<UUID, Integer> LAST_REQUEST_TICK =
            new HashMap<>();

    private ServerPayloadHandler() {
    }

    public static void handleSelectOrigin(
            SelectOriginC2SPayload payload,
            IPayloadContext context
    ) {
        process(
                context,
                "Specific origin selection",
                payload.originId(),
                player -> OnboardingManager.selectOrigin(
                        player,
                        payload.originId()
                )
        );
    }

    public static void handleRandomGlobalOrigin(
            RandomGlobalOriginC2SPayload payload,
            IPayloadContext context
    ) {
        process(
                context,
                "Global random origin selection",
                "global",
                OnboardingManager::selectRandomOriginGlobal
        );
    }

    public static void handleRandomPlanetOrigin(
            RandomPlanetOriginC2SPayload payload,
            IPayloadContext context
    ) {
        process(
                context,
                "Planet random origin selection",
                payload.planetId(),
                player -> OnboardingManager.selectRandomOriginForPlanet(
                        player,
                        payload.planetId()
                )
        );
    }

    public static void forget(ServerPlayer player) {
        LAST_REQUEST_TICK.remove(player.getUUID());
    }

    public static void clear() {
        LAST_REQUEST_TICK.clear();
    }

    private static void process(
            IPayloadContext context,
            String operationName,
            Object target,
            Function<ServerPlayer, OnboardingResult> operation
    ) {
        if (!(context.player() instanceof ServerPlayer player)
                || !acquireRequestSlot(player)) {
            return;
        }

        OnboardingResult result =
                executeSafely(
                        operation,
                        player,
                        operationName
                );

        sendOnboardingResult(
                player,
                result
        );

        LOGGER.debug(
                "[Cosmere API Onboarding] {} for {} returned {} for player {}",
                operationName,
                target,
                result,
                player.getGameProfile().getName()
        );
    }

    private static boolean acquireRequestSlot(
            ServerPlayer player
    ) {
        int now = player.server.getTickCount();
        Integer previous = LAST_REQUEST_TICK.get(player.getUUID());

        if (previous != null
                && now - previous < REQUEST_COOLDOWN_TICKS) {
            return false;
        }

        LAST_REQUEST_TICK.put(player.getUUID(), now);
        return true;
    }

    private static OnboardingResult executeSafely(
            Function<ServerPlayer, OnboardingResult> operation,
            ServerPlayer player,
            String operationName
    ) {
        try {
            OnboardingResult result =
                    operation.apply(player);

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
