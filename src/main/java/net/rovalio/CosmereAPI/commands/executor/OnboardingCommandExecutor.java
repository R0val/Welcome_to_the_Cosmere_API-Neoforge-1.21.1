package net.rovalio.CosmereAPI.commands.executor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.OpenOnboardingS2CPayload;
import net.rovalio.CosmereAPI.onboarding.OnboardingManager;
import net.rovalio.CosmereAPI.onboarding.OnboardingResult;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import net.rovalio.CosmereAPI.player.PlayerStateLifecycleRegistry;
import org.slf4j.Logger;

import java.util.Collection;

public final class OnboardingCommandExecutor {

    private OnboardingCommandExecutor() {
    }

    public static int onboardingStatus(
            CommandSourceStack source,
            Collection<ServerPlayer> targets
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data =
                    CosmereAttachments.get(player);

            source.sendSuccess(
                    () -> Component.literal(
                            "§7Onboarding complete for §f"
                                    + player.getGameProfile()
                                    .getName()
                                    + "§7: "
                                    + (data.isOnboardingComplete()
                                    ? "§atrue"
                                    : "§cfalse")
                    ),
                    false
            );
        }

        return targets.size();
    }

    public static int resetOnboarding(
            CommandSourceStack source,
            Collection<ServerPlayer> targets
    ) {
        int resetPlayers = 0;

        for (ServerPlayer player : targets) {

            CosmerePlayerData data =
                    CosmereAttachments.get(player);

            data.resetOnboarding();

            PacketDistributor.sendToPlayer(
                    player,
                    OpenOnboardingS2CPayload.INSTANCE
            );

            PlayerStateLifecycleRegistry
                    .RecalculationContext context;

            try {
                context =
                        PlayerStateLifecycleRegistry
                                .recalculate(
                                        player,
                                        PlayerStateLifecycleRegistry
                                                .Reason
                                                .RESET_ONBOARDING
                                );

            } catch (RuntimeException exception) {

                source.sendFailure(
                        Component.literal(
                                "Onboarding was reset for "
                                        + player.getGameProfile().getName()
                                        + ", but addon recalculation failed: "
                                        + exception.getMessage()
                        )
                );

                continue;
            }

            source.sendSuccess(
                    () -> Component.literal(
                            "Onboarding reset and player state "
                                    + "reconciled for "
                                    + player.getGameProfile().getName()
                                    + ". Origin selection cleared."
                    ),
                    true
            );

            for (Component message :
                    context.getMessages()) {

                source.sendSuccess(
                        () -> Component.literal(
                                "State correction for "
                                        + player.getGameProfile().getName()
                                        + ": "
                        ).append(message),
                        false
                );
            }

            resetPlayers++;
        }

        return resetPlayers;
    }

    public static int showOrigin(
            CommandSourceStack source
    ) throws CommandSyntaxException {

        ServerPlayer player =
                source.getPlayerOrException();

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        player.sendSystemMessage(
                Component.literal(
                        "§1------ §6ORIGIN STATE §1------"
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "§7Origin Planet: §b"
                                + (
                                data.getOriginPlanetId() != null
                                        ? data.getOriginPlanetId()
                                        : "NONE"
                        )
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "§7Origin: §d"
                                + (
                                data.getOriginId() != null
                                        ? data.getOriginId()
                                        : "NONE"
                        )
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "§7Onboarding Complete: "
                                + (
                                data.isOnboardingComplete()
                                        ? "§atrue"
                                        : "§cfalse"
                        )
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "§1--------------------------"
                )
        );

        return 1;
    }

    public static int changeOrigin(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            ResourceLocation planetId,
            ResourceLocation originId
    ) {
        int changedPlayers = 0;

        for (ServerPlayer player : targets) {

            OnboardingResult result;

            try {
                result =
                        OnboardingManager.changeOrigin(
                                player,
                                planetId,
                                originId
                        );

            } catch (RuntimeException exception) {
                LOGGER.error(
                        "[Cosmere API] Failed to change origin for player {}",
                        player.getGameProfile().getName(),
                        exception
                );

                result = OnboardingResult.INTERNAL_ERROR;
            }

            if (!result.isSuccess()) {
                source.sendFailure(
                        Component.literal(
                                "Could not change origin for "
                                        + player.getGameProfile().getName()
                                        + ": "
                                        + result
                        )
                );

                continue;
            }

            changedPlayers++;

            source.sendSuccess(
                    () -> Component.literal(
                            "Origin changed for "
                                    + player.getGameProfile().getName()
                                    + ": "
                                    + planetId
                                    + " -> "
                                    + originId
                    ),
                    true
            );
        }

        return changedPlayers;
    }

    private static final Logger LOGGER =
            LogUtils.getLogger();
}
