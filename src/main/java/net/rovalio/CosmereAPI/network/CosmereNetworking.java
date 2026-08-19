package net.rovalio.CosmereAPI.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rovalio.CosmereAPI.network.handler.ClientPayloadHandler;
import net.rovalio.CosmereAPI.network.handler.ServerPayloadHandler;
import net.rovalio.CosmereAPI.network.payload.*;

public final class CosmereNetworking {

    private static final String PROTOCOL_VERSION = "2";

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

        registrar.playToServer(
                SelectOriginC2SPayload.TYPE,
                SelectOriginC2SPayload.STREAM_CODEC,
                ServerPayloadHandler::handleSelectOrigin
        );

        registrar.playToServer(
                RandomGlobalOriginC2SPayload.TYPE,
                RandomGlobalOriginC2SPayload.STREAM_CODEC,
                ServerPayloadHandler::handleRandomGlobalOrigin
        );

        registrar.playToServer(
                RandomPlanetOriginC2SPayload.TYPE,
                RandomPlanetOriginC2SPayload.STREAM_CODEC,
                ServerPayloadHandler::handleRandomPlanetOrigin
        );

        registrar.playToClient(
                OnboardingResultS2CPayload.TYPE,
                OnboardingResultS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOnboardingResult
        );
    }
}