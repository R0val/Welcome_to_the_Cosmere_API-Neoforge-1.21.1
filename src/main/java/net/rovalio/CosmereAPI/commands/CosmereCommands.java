package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.rovalio.CosmereAPI.data.CosmereDataComponents;
import net.rovalio.CosmereAPI.data.TornPagesData;
import net.rovalio.CosmereAPI.item.CosmereItems;
import net.rovalio.CosmereAPI.player.*;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;

import java.util.Collection;
import java.util.Locale;

public class CosmereCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        /// Sets values from ConnectionType Enum as usable arguments for commands
        var connectionBranch = Commands.literal("type");

        for (ConnectionType connection : ConnectionType.values()) {

            connectionBranch.then(
                    Commands.literal(connection.name().toLowerCase(Locale.ROOT))
                            .then(
                                    Commands.argument(
                                            "targets",
                                            EntityArgument.players()
                                    )
                            )
            );
        }

        dispatcher.register(Commands.literal("cosmere")

                .then(Commands.literal("stats")

                        .then(Commands.literal("setdefault")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(context ->
                                                restoreDefault(
                                                        context,
                                                        EntityArgument.getPlayers(context, "targets")
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("set")

                                /// Changes in Spiritweb

                                .then(Commands.literal("spiritweb")

                                        .then(Commands.literal("integrity")
                                                .then(Commands.argument("targets", EntityArgument.players())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 100.0))
                                                                .executes(context ->
                                                                        setIntegrity(
                                                                                context,
                                                                                EntityArgument.getPlayers(context, "targets"),
                                                                                DoubleArgumentType.getDouble(context, "value")
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )

                                /// Changes Investiture and then Fortune

                                .then(Commands.literal("investiture")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                                                        .executes(context ->
                                                                setInvestiture(
                                                                        context,
                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                        DoubleArgumentType.getDouble(context, "value")

                                                                )
                                                        )
                                                )
                                        )
                                )

                                .then(Commands.literal("fortune")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                        .executes(context ->
                                                                setFortune(
                                                                        context,
                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                        DoubleArgumentType.getDouble(context, "value")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )

                .then(Commands.literal("show")
                        .then(Commands.literal("stats")
                                .executes(context ->
                                        showStats(context.getSource())
                                )
                        )

                        .then(Commands.literal("origin")
                                .executes(context ->
                                        showOrigin(context.getSource())
                                )
                        )
                )

                .then(Commands.literal("connection")

                        .then(Commands.literal("list")
                                .then(connectionBranch)
                        )

                        .then(Commands.literal("set")
                                .then(connectionBranch)
                        )

                        .then(Commands.literal("erase")
                                .then(connectionBranch)
                        )
                )

                .then(Commands.literal("investedarts")
                        .then(Commands.literal("list"))
                        .then(Commands.literal("grant"))
                        .then(Commands.literal("revoke"))
                )

                .then(Commands.literal("test")
                        .then(Commands.literal("network")


                        )

                        .then(Commands.literal("show")
                                .then(Commands.literal("registrycontents")
                                        .executes(context ->
                                                showRegistryContents(context.getSource())
                                        )
                                )

                                .then(Commands.literal("tornpagesdata")
                                        .executes(context ->
                                                showHeldTornPages(
                                                        context.getSource()
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("onboarding")

                                .then(Commands.literal("status")
                                        .executes(context ->
                                                onboardingStatus(
                                                        context.getSource()
                                                )
                                        )
                                )

                                .then(Commands.literal("reset")
                                        .executes(context ->
                                                resetOnboarding(
                                                        context.getSource()
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("give")


                        )

                )
        );


    }

    private static int showStats(CommandSourceStack source) {

        if (!(source.getEntity() instanceof ServerPlayer player)) {

            source.sendFailure(
                    Component.literal(
                            "Este comando debe ser ejecutado por un jugador."
                    )
            );

            return 0;
        }

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        SpiritwebData spiritweb =
                data.getSpiritweb();

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
                                    + player
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
    private static int setIntegrity(
            CommandContext<CommandSourceStack> context,
            Collection<ServerPlayer> targets,
            double value
    ) {

        double normalizedValue = value / 100.0;

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setIntegrity(normalizedValue);

            context.getSource().sendSuccess(
                    () -> Component.literal(
                            "Spiritweb integrity set to " + normalizedValue
                                    + " % for " + player
                    ),
                    true
            );
        }

        return targets.size();
    }

    //Changes Investiture
    private static int setInvestiture(
            CommandContext<CommandSourceStack> context,
            Collection<ServerPlayer> targets,
            double value
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setInvestitureBEU(value);

            context.getSource().sendSuccess(
                    () -> Component.literal(
                            "Investiture set to " + value
                                    + " BEU for " + player
                    ),
                    true
            );
        }

        return targets.size();
    }

    //Changes Fortune
    private static int setFortune(
            CommandContext<CommandSourceStack> context,
            Collection<ServerPlayer> targets,
            double value
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setFortune(value);

            context.getSource().sendSuccess(
                    () -> Component.literal(
                            "Investiture set to " + value
                                    + " for " + player
                    ),
                    true
            );
        }

        return targets.size();
    }

    private static int restoreDefault(
            CommandContext<CommandSourceStack> context,
            Collection<ServerPlayer> targets
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);
;
            data.getSpiritweb().setIntegrity(SpiritwebData.DEFAULT_INTEGRITY);
            data.getSpiritweb().setInvestitureBEU(SpiritwebData.DEFAULT_INVESTITURE_BEU);
            data.getSpiritweb().setFortune(SpiritwebData.DEFAULT_FORTUNE);

            context.getSource().sendSuccess(
                    () -> Component.literal(
                            "Every Spiritweb aspect has been setted to default"
                    ),
                    true
            );
        }

        return 1;
    }

    private static int showRegistryContents(
            CommandSourceStack source
    ) {

        source.sendSuccess(
                () -> Component.literal(
                        "§1------ §6COSMERE REGISTRY CONTENTS §1------"
                ),
                false
        );

        // Planets
        source.sendSuccess(
                () -> Component.literal(
                        "§6Planets:"
                ),
                false
        );

        for (ResourceLocation planetId
                : CosmereRegistries.PLANET_REGISTRY.keySet()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §b" + planetId
                    ),
                    false
            );
        }

        // Origins
        source.sendSuccess(
                () -> Component.literal(
                        "§6Origins:"
                ),
                false
        );

        for (ResourceLocation originId
                : CosmereRegistries.ORIGIN_REGISTRY.keySet()) {

            OriginDefinition origin =
                    CosmereRegistries.ORIGIN_REGISTRY.get(originId);

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §d"
                                    + originId
                                    + " §7-> §b"
                                    + origin.planet().location()
                    ),
                    false
            );
        }

        source.sendSuccess(
                () -> Component.literal(
                        "§1--------------------------------"
                ),
                false
        );

        return 1;
    }

    private static String status(boolean result) {
        return result ? "§aOK" : "§cFAILED";
    }

    private static int onboardingStatus(
            CommandSourceStack source
    ) {

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        source.sendSuccess(
                () -> Component.literal(
                        "§7Onboarding complete: "
                                + (data.isOnboardingComplete()
                                ? "§atrue"
                                : "§cfalse")
                ),
                false
        );

        return 1;
    }

    private static int resetOnboarding(
            CommandSourceStack source
    ) {
        if (!(source.getEntity()
                instanceof ServerPlayer player)) {

            source.sendFailure(
                    Component.literal(
                            "Este comando debe ser ejecutado por un jugador."
                    )
            );

            return 0;
        }

        CosmerePlayerData data =
                CosmereAttachments.get(player);

        data.resetOnboarding();

        source.sendSuccess(
                () -> Component.literal(
                        "§7Onboarding reset. "
                                + "Origin selection cleared."
                ),
                false
        );

        return 1;
    }

    private static int showOrigin(
            CommandSourceStack source
    ) {

        if (!(source.getEntity() instanceof ServerPlayer player)) {

            source.sendFailure(
                    Component.literal(
                            "Este comando debe ser ejecutado por un jugador."
                    )
            );

            return 0;
        }

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

    private static int showHeldTornPages(
            CommandSourceStack source
    ) {

        //Checks if it's ran by a player
        if (!(source.getEntity() instanceof ServerPlayer player)) {

            source.sendFailure(
                    Component.literal(
                            "This command must be executed by a player."
                    )
            );

            return 0;
        }

        //gets the helded item
        ItemStack stack =
                player.getMainHandItem();

        //Checks what item is being helded
        if (!stack.is(
                CosmereItems.TORN_PAGES.get()
        )) {

            //return an error if the wrong item is being helded
            source.sendFailure(
                    Component.literal(
                            "§cYou must hold Torn Pages in your main hand."
                    )
            );

            return 0;
        }

        //reads the Torn Pages data
        TornPagesData data =
                stack.get(
                        CosmereDataComponents
                                .TORN_PAGES_DATA
                                .get()
                );

        //Chescks if there's existent data
        if (data == null) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§1------ §6TORN PAGES DATA §1------"
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§7Planet: §cNONE"
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§7Unlocked entries: §cNONE"
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§eThese Torn Pages contain no knowledge data."
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§1-----------------------------"
                    ),
                    false
            );

            return 1;
        }

        //Output
        source.sendSuccess(
                () -> Component.literal(
                        "§1------ §6TORN PAGES DATA §1------"
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Planet: §b"
                                + data.planetId()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Unlocked entries: §d"
                                + data.entriesID().size()
                ),
                false
        );

        //Shows the unlocked entries
        if (data.entriesID().isEmpty()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §8No entries unlocked."
                    ),
                    false
            );

        } else {

            for (ResourceLocation entry
                    : data.entriesID()) {

                source.sendSuccess(
                        () -> Component.literal(
                                "§7- §d" + entry
                        ),
                        false
                );
            }
        }

        //Success advise
        source.sendSuccess(
                () -> Component.literal(
                        "§aTorn Pages data read: SUCCESS"
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§1-----------------------------"
                ),
                false
        );

        return 1;
    }
}
