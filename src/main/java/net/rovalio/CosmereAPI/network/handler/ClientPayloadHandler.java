package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.client.screen.AbstractOnboardingScreen;
import net.rovalio.CosmereAPI.client.screen.TemporaryOnboardingScreen;
import net.rovalio.CosmereAPI.network.payload.*;
import net.rovalio.CosmereAPI.onboarding.OnboardingManager;
import org.slf4j.Logger;

public final class ClientPayloadHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private ClientPayloadHandler() {
    }

    public static void handleOpenOnboarding(
            OpenOnboardingS2CPayload payload,
            IPayloadContext context
    ) {

        LOGGER.info(
                "[Cosmere API Onboarding] Open onboarding payload received on thread: {}",
                Thread.currentThread().getName()
        );

        context.enqueueWork(() -> {

            LOGGER.info(
                    "[Cosmere API Onboarding] Opening temporary onboarding screen on thread: {}",
                    Thread.currentThread().getName()
            );

            Minecraft.getInstance().setScreen(
                    new TemporaryOnboardingScreen()
            );

        }).exceptionally(exception -> {

            LOGGER.error(
                    "[Cosmere API Onboarding] Failed to open onboarding screen",
                    exception
            );

            return null;
        });
    }

    public static void handleOnboardingResult(
            OnboardingResultS2CPayload payload,
            IPayloadContext context
    ) {
        LOGGER.info(
                "[Cosmere API Onboarding] Result {} received | Thread: {}",
                payload.result(),
                Thread.currentThread().getName()
        );

        context.enqueueWork(() -> {
            Minecraft minecraft =
                    Minecraft.getInstance();

            if (!(minecraft.screen
                    instanceof AbstractOnboardingScreen screen)) {

                LOGGER.warn(
                        "[Cosmere API Onboarding] Result {} received without an onboarding screen open",
                        payload.result()
                );

                return;
            }

            if (payload.result().isSuccess()) {
                minecraft.setScreen(null);
                return;
            }

            screen.setOnboardingResult(
                    payload.result()
            );

        }).exceptionally(exception -> {
            LOGGER.error(
                    "[Cosmere API Onboarding] Failed to process onboarding result",
                    exception
            );

            return null;
        });
    }
}