package net.rovalio.CosmereAPI.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.CosmereAPI;

public record NetworkPingS2CPayload() implements CustomPacketPayload {

    public static final NetworkPingS2CPayload INSTANCE =
            new NetworkPingS2CPayload();

    public static final Type<NetworkPingS2CPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "network_ping_s2c"
                    )
            );

    public static final StreamCodec<ByteBuf, NetworkPingS2CPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}