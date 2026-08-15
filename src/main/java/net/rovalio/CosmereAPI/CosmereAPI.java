package net.rovalio.CosmereAPI;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.rovalio.CosmereAPI.commands.CosmereCommands;
import net.rovalio.CosmereAPI.data.CosmereDataComponents;
import net.rovalio.CosmereAPI.item.CosmereItems;
import net.rovalio.CosmereAPI.network.CosmereNetworking;
import net.rovalio.CosmereAPI.onboarding.PlayerLoginHandler;
import net.rovalio.CosmereAPI.player.CosmereAttachments;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.test.TestDefinition;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CosmereAPI.MOD_ID)
public class CosmereAPI {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "cosmere_api";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CosmereAPI(IEventBus modEventBus) {

        /// Makes the mod load the CosmereAttachments registries when start up
        CosmereAttachments.register(modEventBus);

        modEventBus.addListener(CosmereRegistries::registerRegistries);

        modEventBus.addListener(CosmereNetworking::registerPayloadHandlers);

        CosmereDataComponents.register(modEventBus);
        CosmereItems.register(modEventBus);

        TestDefinition.register(modEventBus);

        PlayerLoginHandler.register();

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    @EventBusSubscriber(modid = MOD_ID)
    public static class GameEvents {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {

            CosmereCommands.register(event.getDispatcher());

            LOGGER.info("Comandos de Cosmere API registrados.");
        }
    }
}
