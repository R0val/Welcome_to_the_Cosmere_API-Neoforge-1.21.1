package net.rovalio.CosmereAPI.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.CosmereAPI;

public record OpenOnboardingS2CPayload()
        implements CustomPacketPayload {

    public static final OpenOnboardingS2CPayload INSTANCE =
            new OpenOnboardingS2CPayload();

    public static final Type<OpenOnboardingS2CPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "open_onboarding"
                    )
            );

    public static final StreamCodec<ByteBuf, OpenOnboardingS2CPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}