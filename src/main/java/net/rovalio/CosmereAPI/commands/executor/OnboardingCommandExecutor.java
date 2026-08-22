package net.rovalio.CosmereAPI.commands.executor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.onboarding.OnboardingManager;
import net.rovalio.CosmereAPI.onboarding.OnboardingResult;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
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
        for (ServerPlayer player : targets) {
            CosmerePlayerData data =
                    CosmereAttachments.get(player);

            data.resetOnboarding();

            source.sendSuccess(
                    () -> Component.literal(
                            "Onboarding reset for "
                                    + player.getGameProfile()
                                    .getName()
                                    + ". Origin selection cleared."
                    ),
                    true
            );
        }

        return targets.size();
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
