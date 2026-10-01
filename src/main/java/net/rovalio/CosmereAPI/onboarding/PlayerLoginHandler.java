package net.rovalio.CosmereAPI.onboarding;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.handler.ServerPayloadHandler;
import net.rovalio.CosmereAPI.network.payload.OpenOnboardingS2CPayload;
import net.rovalio.CosmereAPI.player.ConnectionData;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import net.rovalio.CosmereAPI.player.PlayerStateLifecycleRegistry;
import net.rovalio.CosmereAPI.player.SpiritwebData;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public final class PlayerLoginHandler {

    private static final Logger LOGGER =
            LogUtils.getLogger();

    private PlayerLoginHandler() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                PlayerLoginHandler::onPlayerLoggedIn
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerLoginHandler::onPlayerRespawn
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerLoginHandler::onPlayerLoggedOut
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerLoginHandler::onServerStopped
        );
    }

    private static void onPlayerLoggedOut(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        if (event.getEntity()
                instanceof ServerPlayer player) {

            ServerPayloadHandler.forget(player);
        }
    }

    private static void onServerStopped(
            ServerStoppedEvent event
    ) {
        ServerPayloadHandler.clear();
    }

    private static void onPlayerLoggedIn(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {

            return;
        }

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        reconcilePlayerState(
                player,
                PlayerStateLifecycleRegistry
                        .Reason
                        .PLAYER_LOGIN
        );

        notifyOrphanedAddonData(
                player,
                data.getSpiritweb()
        );

        LOGGER.debug(
                "[Cosmere API Onboarding] Player login detected: {} | onboardingComplete: {}",
                player.getGameProfile().getName(),
                data.isOnboardingComplete()
        );

        if (!data.isOnboardingComplete()) {

            LOGGER.debug(
                    "[Cosmere API Onboarding] Player {} requires onboarding.",
                    player.getGameProfile().getName()
            );

            PacketDistributor.sendToPlayer(
                    player,
                    OpenOnboardingS2CPayload.INSTANCE
            );

        } else {

            LOGGER.debug(
                    "[Cosmere API Onboarding] Player {} has already completed onboarding.",
                    player.getGameProfile().getName()
            );
        }
    }

    private static void onPlayerRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {

            return;
        }

        reconcilePlayerState(
                player,
                PlayerStateLifecycleRegistry
                        .Reason
                        .PLAYER_RESPAWN
        );
    }

    private static void reconcilePlayerState(
            ServerPlayer player,
            PlayerStateLifecycleRegistry.Reason reason
    ) {
        try {
            PlayerStateLifecycleRegistry
                    .recalculate(
                            player,
                            reason
                    );

        } catch (RuntimeException exception) {

            //An addon failure must be reported, but must not prevent login or respawn.
            LOGGER.error(
                    "[Cosmere API] Player State Lifecycle "
                            + "reconciliation failed for "
                            + "player {} during {}",
                    player.getGameProfile().getName(),
                    reason,
                    exception
            );
        }
    }

    private static void notifyOrphanedAddonData(
            ServerPlayer player,
            SpiritwebData spiritweb
    ) {
        List<ConnectionData> orphanedConnections =
                spiritweb.getOrphanedConnections();

        List<ResourceLocation> orphanedInvestedArts =
                spiritweb.getOrphanedInvestedArts();

        if (orphanedConnections.isEmpty()
                && orphanedInvestedArts.isEmpty()) {

            return;
        }

        // Only one player-facing warning is sent, regardless of how many orphaned data types exist.
        player.sendSystemMessage(
                Component.literal(
                        "[Cosmere API] Data belonging to missing add-ons "
                                + "has been preserved. Please check that no "
                                + "add-on file has been deleted or modified. "
                                + "No data has been deleted."
                ).withStyle(ChatFormatting.YELLOW)
        );

        if (!orphanedConnections.isEmpty()) {

            String orphanedConnectionSummary =
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
                    orphanedConnectionSummary
            );
        }

        if (!orphanedInvestedArts.isEmpty()) {

            String orphanedArtSummary =
                    orphanedInvestedArts.stream()
                            .map(ResourceLocation::toString)
                            .collect(
                                    Collectors.joining(", ")
                            );

            LOGGER.warn(
                    "[Cosmere API Invested Arts] Player {} has {} orphaned Invested Art(s): {}",
                    player.getGameProfile().getName(),
                    orphanedInvestedArts.size(),
                    orphanedArtSummary
            );
        }
    }
}