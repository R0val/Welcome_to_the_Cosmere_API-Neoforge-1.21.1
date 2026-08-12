package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.client.screen.TemporaryOnboardingScreen;
import net.rovalio.CosmereAPI.network.payload.*;
import net.rovalio.CosmereAPI.onboarding.OnboardingManager;
import org.slf4j.Logger;

public final class ClientPayloadHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private ClientPayloadHandler() {
    }

    public static void handlePing(
            NetworkPingS2CPayload payload,
            IPayloadContext context
    ) {

        LOGGER.info(
                "[Cosmere API Networking] S2C ping received on thread: {}",
                Thread.currentThread().getName()
        );

        context.reply(
                NetworkPongC2SPayload.INSTANCE
        );
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

    public static void handleOnboardingComplete(
            OnboardingCompleteS2CPayload payload,
            IPayloadContext context
    ) {

        LOGGER.info(
                "[Cosmere API Onboarding] Completion confirmation received | Thread: {}",
                Thread.currentThread().getName()
        );

        context.enqueueWork(() -> {

            LOGGER.info(
                    "[Cosmere API Onboarding] Closing onboarding screen | Thread: {}",
                    Thread.currentThread().getName()
            );

            Minecraft.getInstance().setScreen(null);

        }).exceptionally(exception -> {

            LOGGER.error(
                    "[Cosmere API Onboarding] Failed to close onboarding screen",
                    exception
            );

            return null;
        });
    }
}