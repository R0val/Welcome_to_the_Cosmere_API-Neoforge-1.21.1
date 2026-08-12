package net.rovalio.CosmereAPI.onboarding;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.OpenOnboardingS2CPayload;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import org.slf4j.Logger;

public final class PlayerLoginHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private PlayerLoginHandler() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                PlayerLoginHandler::onPlayerLoggedIn
        );
    }

    private static void onPlayerLoggedIn(
            PlayerEvent.PlayerLoggedInEvent event
    ) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        LOGGER.info(
                "[Cosmere API Onboarding] Player login detected: {} | onboardingComplete: {}",
                player.getGameProfile().getName(),
                data.isOnboardingComplete()
        );

        if (!data.isOnboardingComplete()) {

            LOGGER.info(
                    "[Cosmere API Onboarding] Player {} requires onboarding.",
                    player.getGameProfile().getName()
            );

            PacketDistributor.sendToPlayer(
                    player,
                    OpenOnboardingS2CPayload.INSTANCE
            );

        } else {

            LOGGER.info(
                    "[Cosmere API Onboarding] Player {} has already completed onboarding.",
                    player.getGameProfile().getName()
            );
        }
    }
}