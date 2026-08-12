package net.rovalio.CosmereAPI.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.CosmereAPI;

public record RandomGlobalOriginC2SPayload()
        implements CustomPacketPayload {

    public static final RandomGlobalOriginC2SPayload INSTANCE =
            new RandomGlobalOriginC2SPayload();

    public static final Type<RandomGlobalOriginC2SPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "random_global_origin"
                    )
            );

    public static final StreamCodec<ByteBuf, RandomGlobalOriginC2SPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
