package net.rovalio.CosmereAPI.onboarding;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.OpenOnboardingS2CPayload;
import net.rovalio.CosmereAPI.player.ConnectionData;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

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

        List<ConnectionData> orphanedConnections =
                data.getSpiritweb()
                        .getOrphanedConnections();

        if (!orphanedConnections.isEmpty()) {

            player.sendSystemMessage(
                    Component.literal(
                            "[Cosmere API] Data belonging to add-ons has been saved despite their absence."
                                    + "Please check that no add-on file has been deleted or modified."
                                    + "No data has been deleted."
                    ).withStyle(ChatFormatting.YELLOW)
            );

            String orphanedSummary =
                    orphanedConnections.stream()
                            .map(connection ->
                                    connection.type().name()
                                            + " -> "
                                            + connection.target()
                                            + " ["
                                            + connection.strength()
                                            + "]"
                            )
                            .collect(
                                    Collectors.joining(", ")
                            );

            LOGGER.warn(
                    "[Cosmere API Connections] Player {} has {} orphaned connection(s): {}",
                    player.getGameProfile().getName(),
                    orphanedConnections.size(),
                    orphanedSummary
            );
        }

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