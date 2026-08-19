package net.rovalio.CosmereAPI.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.CosmereAPI;
import net.rovalio.CosmereAPI.onboarding.OnboardingResult;

public record OnboardingResultS2CPayload(
        OnboardingResult result
) implements CustomPacketPayload {

    public OnboardingResultS2CPayload {
        if (result == null) {
            result = OnboardingResult.INTERNAL_ERROR;
        }
    }

    private static final StreamCodec<
            ByteBuf,
            OnboardingResult
            > RESULT_STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    OnboardingResultS2CPayload::parseResult,
                    OnboardingResult::name
            );

    public static final Type<
            OnboardingResultS2CPayload
            > TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(
                    CosmereAPI.MOD_ID,
                    "onboarding_result"
            )
    );

    public static final StreamCodec<
            ByteBuf,
            OnboardingResultS2CPayload
            > STREAM_CODEC =
            StreamCodec.composite(
                    RESULT_STREAM_CODEC,
                    OnboardingResultS2CPayload::result,
                    OnboardingResultS2CPayload::new
            );

    private static OnboardingResult parseResult(
            String resultName
    ) {
        if (resultName == null) {
            return OnboardingResult.INTERNAL_ERROR;
        }

        try {
            return OnboardingResult.valueOf(
                    resultName
            );
        } catch (IllegalArgumentException exception) {
            return OnboardingResult.INTERNAL_ERROR;
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}