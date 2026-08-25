    package net.rovalio.CosmereAPI.network.payload;

    import io.netty.buffer.ByteBuf;
    import net.minecraft.network.codec.StreamCodec;
    import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
    import net.minecraft.resources.ResourceLocation;
    import net.rovalio.CosmereAPI.CosmereAPI;

    public record RandomPlanetOriginC2SPayload(
            ResourceLocation planetId
    ) implements CustomPacketPayload {

        public static final Type<RandomPlanetOriginC2SPayload> TYPE =
                new Type<>(
                        ResourceLocation.fromNamespaceAndPath(
                                CosmereAPI.MOD_ID,
                                "random_planet_origin"
                        )
                );

        public static final StreamCodec<ByteBuf, RandomPlanetOriginC2SPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC,
                        RandomPlanetOriginC2SPayload::planetId,
                        RandomPlanetOriginC2SPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
