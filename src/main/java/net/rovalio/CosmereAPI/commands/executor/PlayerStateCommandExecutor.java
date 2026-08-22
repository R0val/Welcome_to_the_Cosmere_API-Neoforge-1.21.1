package net.rovalio.CosmereAPI.commands.executor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import net.rovalio.CosmereAPI.player.SpiritwebData;

import java.util.Collection;
import java.util.UUID;

public final class PlayerStateCommandExecutor {

    private PlayerStateCommandExecutor() {
    }

    private static String resolveIdentity(
            CommandSourceStack source,
            UUID identityId
    ) {
        if (identityId == null) {
            return "NONE";
        }

        ServerPlayer identityPlayer =
                source.getServer()
                        .getPlayerList()
                        .getPlayer(identityId);

        return identityPlayer != null
                ? identityPlayer
                .getGameProfile()
                .getName()
                : identityId.toString();
    }

    public static int showStats(
            CommandSourceStack source
    ) throws CommandSyntaxException {

        ServerPlayer player =
                source.getPlayerOrException();

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        SpiritwebData spiritweb =
                data.getSpiritweb();

        String identity =
                resolveIdentity(
                        source,
                        spiritweb.getIdentity()
                                .getIdentityId()
                );

        player.sendSystemMessage(
                Component.literal("§1------ §6COSMERE STATE §1------")
        );

        player.sendSystemMessage(
                Component.literal(
                        String.format(
                                "§7Spiritweb Integrity: §a%.2f%%",
                                spiritweb.getIntegrity() * 100.0
                        )
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        String.format(
                                "§7Investiture: §b%.2f BEU",
                                spiritweb.getInvestitureBEU()
                        )
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        String.format(
                                "§7Fortune: §d%.2f",
                                spiritweb.getFortune()
                        )
                )
        );

        if (spiritweb.getIdentity().getIdentityId() != null) {

            player.sendSystemMessage(
                    Component.literal(
                            "§7Identity: §f"
                                    + identity
                    )
            );
        }

        player.sendSystemMessage(
                Component.literal(
                        "§7Connections: §e"
                                + spiritweb.getConnections().size()
                )
        );

        player.sendSystemMessage(
                Component.literal("§1--------------------------")
        );

        return 1;
    }

    //Changes Spiritweb integrity
    public static int setIntegrity(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            double value
    ) {

        double normalizedValue = value / 100.0;

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setIntegrity(normalizedValue);

            source.sendSuccess(
                    () -> Component.literal(
                            "Spiritweb integrity set to "
                                    + value
                                    + "% for "
                                    + player.getGameProfile().getName()
                    ),
                    true
            );
        }

        return targets.size();
    }

    //Changes Investiture
    public static int setInvestiture(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            double value
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setInvestitureBEU(value);

            source.sendSuccess(
                    () -> Component.literal(
                            "Investiture set to " + value
                                    + " BEU for " + player.getGameProfile().getName()
                    ),
                    true
            );
        }

        return targets.size();
    }

    //Changes Fortune
    public static int setFortune(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            double value
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setFortune(value);

            source.sendSuccess(
                    () -> Component.literal(
                            "Fortune set to " + value
                                    + " for " + player.getGameProfile().getName()
                    ),
                    true
            );
        }

        return targets.size();
    }

    public static int resetValues(
            CommandSourceStack source,
            Collection<ServerPlayer> targets
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().resetStats();

            source.sendSuccess(
                    () -> Component.literal(
                            "Spiritweb values reset to their defaults for "
                                    + player.getGameProfile().getName()
                    ),
                    true
            );
        }

        return targets.size();
    }
}
