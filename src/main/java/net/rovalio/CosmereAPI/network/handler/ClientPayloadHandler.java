package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.client.screen.TemporaryOnboardingScreen;
import net.rovalio.CosmereAPI.network.payload.NetworkPingS2CPayload;
import net.rovalio.CosmereAPI.network.payload.NetworkPongC2SPayload;
import net.rovalio.CosmereAPI.network.payload.OpenOnboardingS2CPayload;
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
}