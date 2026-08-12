package net.rovalio.CosmereAPI.player;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.rovalio.CosmereAPI.CosmereAPI;

import java.util.function.Supplier;

public class CosmereAttachments {

    //Registers a NeoForge Registry with the label "cosmere_api"
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.ATTACHMENT_TYPES,
                    CosmereAPI.MOD_ID //saves the data with the ID cosmere_api:cosmere_data
            );


    // Creates new cosmere_data to players
    public static final Supplier<AttachmentType<CosmerePlayerData>> COSMERE_DATA =
            ATTACHMENT_TYPES.register(
                    "cosmere_data",
                    () -> AttachmentType
                            .serializable(CosmerePlayerData::new)
                            .copyOnDeath()
                            .build()
            );

    private CosmereAttachments () {

    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    //If Player already has COSMERE_DATA, it returns it
    public static CosmerePlayerData get(Player player) {

        CosmerePlayerData data =
                player.getData(COSMERE_DATA);

        data.getSpiritweb()
                .getIdentity()
                .initialize(player.getUUID());

        return data;
    }
}
