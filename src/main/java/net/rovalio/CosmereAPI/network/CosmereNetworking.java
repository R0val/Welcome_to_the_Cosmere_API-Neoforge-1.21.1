package net.rovalio.CosmereAPI.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rovalio.CosmereAPI.network.handler.ClientPayloadHandler;
import net.rovalio.CosmereAPI.network.handler.ServerPayloadHandler;
import net.rovalio.CosmereAPI.network.payload.NetworkPingS2CPayload;
import net.rovalio.CosmereAPI.network.payload.NetworkPongC2SPayload;
import net.rovalio.CosmereAPI.network.payload.OpenOnboardingS2CPayload;

public final class CosmereNetworking {

    private static final String PROTOCOL_VERSION = "1";

    private CosmereNetworking() {
    }

    public static void registerPayloadHandlers(
            RegisterPayloadHandlersEvent event
    ) {

        final PayloadRegistrar registrar =
                event.registrar(PROTOCOL_VERSION)
                        .executesOn(HandlerThread.NETWORK);

        /// Test Server -> Client
        registrar.playToClient(
                NetworkPingS2CPayload.TYPE,
                NetworkPingS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handlePing
        );

        /// Test Client -> Server
        registrar.playToServer(
                NetworkPongC2SPayload.TYPE,
                NetworkPongC2SPayload.STREAM_CODEC,
                ServerPayloadHandler::handlePong
        );

        // REAL
        registrar.playToClient(
                OpenOnboardingS2CPayload.TYPE,
                OpenOnboardingS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOpenOnboarding
        );
    }
}