package net.rovalio.CosmereAPI.commands.executor;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;
import net.rovalio.CosmereAPI.registry.definition.OriginDefinition;

public final class RegistryCommandExecutor {

    private RegistryCommandExecutor() {
    }

    public static int showRegistryContents(
            CommandSourceStack source
    ) {

        source.sendSuccess(
                () -> Component.literal(
                        "§1------ §6COSMERE REGISTRY CONTENTS §1------"
                ),
                false
        );

        // Planets
        source.sendSuccess(
                () -> Component.literal(
                        "§6Planets:"
                ),
                false
        );

        for (ResourceLocation planetId
                : CosmereRegistries.PLANET_REGISTRY.keySet()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §b" + planetId
                    ),
                    false
            );
        }

        // Shards
        source.sendSuccess(
                () -> Component.literal(
                        "§6Shards:"
                ),
                false
        );

        for (ResourceLocation shardId
                : CosmereRegistries.SHARD_REGISTRY.keySet()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §b" + shardId
                    ),
                    false
            );
        }

        // Origins
        source.sendSuccess(
                () -> Component.literal(
                        "§6Origins:"
                ),
                false
        );

        for (ResourceLocation originId
                : CosmereRegistries.ORIGIN_REGISTRY.keySet()) {

            OriginDefinition origin =
                    CosmereRegistries.ORIGIN_REGISTRY.get(originId);

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §d"
                                    + originId
                                    + " §7-> §b"
                                    + origin.planet().location()
                    ),
                    false
            );
        }

        // Invested Arts
        source.sendSuccess(
                () -> Component.literal(
                        "§6Invested Arts:"
                ),
                false
        );

        for (ResourceLocation investedArtId
                : CosmereRegistries.INVESTED_ART_REGISTRY.keySet()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §b" + investedArtId
                    ),
                    false
            );
        }

        source.sendSuccess(
                () -> Component.literal(
                        "§1--------------------------------"
                ),
                false
        );

        return 1;
    }
}
