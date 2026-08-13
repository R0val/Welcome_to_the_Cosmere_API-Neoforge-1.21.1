package net.rovalio.CosmereAPI.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record TornPagesData(
        ResourceLocation planetId,
        List<ResourceLocation> unlockedEntries
) {

    public TornPagesData {
        unlockedEntries = List.copyOf(unlockedEntries);
    }

    public static final Codec<TornPagesData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ResourceLocation.CODEC
                                    .fieldOf("planet")
                                    .forGetter(TornPagesData::planetId),

                            ResourceLocation.CODEC
                                    .listOf()
                                    .fieldOf("entries")
                                    .forGetter(TornPagesData::unlockedEntries)
                    ).apply(
                            instance,
                            TornPagesData::new
                    )
            );
}