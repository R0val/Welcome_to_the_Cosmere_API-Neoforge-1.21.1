package net.rovalio.CosmereAPI.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.rovalio.CosmereAPI.player.ConnectionType;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.player.CosmerePlayerData;
import net.rovalio.CosmereAPI.player.SpiritwebData;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;
import net.rovalio.CosmereAPI.registry.definition.PlanetDefinition;
import net.rovalio.CosmereAPI.registry.test.TestDefinition;

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


                                        .then(Commands.literal("size")
                                                .then(Commands.argument("targets", EntityArgument.players())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                                                                .executes(context ->
                                                                        setSize(
                                                                                context,
                                                                                EntityArgument.getPlayers(context, "targets"),
                                                                                DoubleArgumentType.getDouble(context, "value")
                                                                        )
                                                                )
                                                        )
                                                )
                                        )


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
                                //Mostrará planeta y origen
                        )

                        .then(Commands.literal("registrytest")
                                .executes(context ->
                                        showRegistryTest(context.getSource())
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
                                "§7Spiritweb Size: §b%.2f",
                                spiritweb.getSize()
                        )
                )
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
                                    + spiritweb.getIdentity().getIdentityId()
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

    //Changes Spiritweb size
    private static int setSize(
            CommandContext<CommandSourceStack> context,
            Collection<ServerPlayer> targets,
            double value
    ) {

        for (ServerPlayer player : targets) {

            CosmerePlayerData data = CosmereAttachments.get(player);

            data.getSpiritweb().setSize(value);

            context.getSource().sendSuccess(
                    () -> Component.literal(
                            "Spiritweb size set to " + value
                                    + " for " + player
                    ),
                    true
            );
        }

        return targets.size();
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

            data.getSpiritweb().setSize(SpiritwebData.DEFAULT_SIZE);
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

    private static int showRegistryTest(CommandSourceStack source) {

        // Get registered definitions

        PlanetDefinition testPlanet =
                CosmereRegistries.PLANET_REGISTRY.get(
                        TestDefinition.TEST_PLANET.getId()
                );

        OriginDefinition testOriginA =
                CosmereRegistries.ORIGIN_REGISTRY.get(
                        TestDefinition.TEST_ORIGIN_A.getId()
                );

        OriginDefinition testOriginB =
                CosmereRegistries.ORIGIN_REGISTRY.get(
                        TestDefinition.TEST_ORIGIN_B.getId()
                );

        // Check existence

        boolean planetExists = testPlanet != null;
        boolean originAExists = testOriginA != null;
        boolean originBExists = testOriginB != null;

        // Check planet dimension

        boolean planetDimensionCorrect =
                planetExists
                        && Level.OVERWORLD.equals(testPlanet.dimension());

        // Check origin -> planet relationships

        boolean originAPlanetCorrect =
                originAExists
                        && TestDefinition.TEST_PLANET_KEY.equals(
                        testOriginA.planet()
                );

        boolean originBPlanetCorrect =
                originBExists
                        && TestDefinition.TEST_PLANET_KEY.equals(
                        testOriginB.planet()
                );

        // Global result

        boolean success =
                planetExists
                        && originAExists
                        && originBExists
                        && planetDimensionCorrect
                        && originAPlanetCorrect
                        && originBPlanetCorrect;

        // Output

        source.sendSuccess(
                () -> Component.literal(
                        "§1------ §6REGISTRY TEST §1------"
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Planet §f"
                                + TestDefinition.TEST_PLANET.getId()
                                + ": "
                                + status(planetExists)
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Planet dimension §f"
                                + Level.OVERWORLD.location()
                                + ": "
                                + status(planetDimensionCorrect)
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Origin §f"
                                + TestDefinition.TEST_ORIGIN_A.getId()
                                + ": "
                                + status(originAExists)
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Origin A -> Planet: "
                                + status(originAPlanetCorrect)
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Origin §f"
                                + TestDefinition.TEST_ORIGIN_B.getId()
                                + ": "
                                + status(originBExists)
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Origin B -> Planet: "
                                + status(originBPlanetCorrect)
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        success
                                ? "§aRegistry test: SUCCESS"
                                : "§cRegistry test: FAILED"
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§1---------------------------"
                ),
                false
        );

        return success ? 1 : 0;
    }

    private static String status(boolean result) {
        return result ? "§aOK" : "§cFAILED";
    }
}
