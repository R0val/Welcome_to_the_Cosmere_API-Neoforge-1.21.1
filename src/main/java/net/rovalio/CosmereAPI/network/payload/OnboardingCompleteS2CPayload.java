package net.rovalio.CosmereAPI.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.CosmereAPI;

public record OnboardingCompleteS2CPayload()
        implements CustomPacketPayload {

    public static final OnboardingCompleteS2CPayload INSTANCE =
            new OnboardingCompleteS2CPayload();

    public static final Type<OnboardingCompleteS2CPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CosmereAPI.MOD_ID,
                            "onboarding_complete"
                    )
            );

    public static final StreamCodec<ByteBuf, OnboardingCompleteS2CPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}