package net.rovalio.CosmereAPI.network.handler;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rovalio.CosmereAPI.network.payload.NetworkPongC2SPayload;
import org.slf4j.Logger;

public final class ServerPayloadHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private ServerPayloadHandler() {
    }

    public static void handlePong(
            NetworkPongC2SPayload payload,
            IPayloadContext context
    ) {

        LOGGER.info(
                "[Cosmere API Networking] C2S pong received on thread: {}",
                Thread.currentThread().getName()
        );
    }
}