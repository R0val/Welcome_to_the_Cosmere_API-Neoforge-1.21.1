package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.client.screen.AbstractOnboardingScreen;
import net.rovalio.CosmereAPI.client.screen.TemporaryOnboardingScreen;
import net.rovalio.CosmereAPI.network.payload.*;
import org.slf4j.Logger;

public final class ClientPayloadHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private ClientPayloadHandler() {
    }

    public static void handleOpenOnboarding(
            OpenOnboardingS2CPayload payload,
            IPayloadContext context
    ) {
        LOGGER.debug(
                "[Cosmere API Onboarding] Opening temporary onboarding screen"
        );

        Minecraft.getInstance().setScreen(
                new TemporaryOnboardingScreen()
        );
    }

    public static void handleOnboardingResult(
            OnboardingResultS2CPayload payload,
            IPayloadContext context
    ) {
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
    }
}
