package net.rovalio.CosmereAPI.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.CosmereAPI;

public record NetworkPongC2SPayload() implements CustomPacketPayload {

    public static final NetworkPongC2SPayload INSTANCE =
            new NetworkPongC2SPayload();

    public static final Type<NetworkPongC2SPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "network_pong_c2s"
                    )
            );

    public static final StreamCodec<ByteBuf, NetworkPongC2SPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}