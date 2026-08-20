package net.rovalio.CosmereAPI.commands.executor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.rovalio.CosmereAPI.data.CosmereDataComponents;
import net.rovalio.CosmereAPI.data.TornPagesData;
import net.rovalio.CosmereAPI.item.CosmereItems;

public final class TornPagesCommandExecutor {

    private TornPagesCommandExecutor() {}

    public static int showHeldTornPages(
            CommandSourceStack source
    ) throws CommandSyntaxException {

        ServerPlayer player =
                source.getPlayerOrException();

        //gets the held item
        ItemStack stack =
                player.getMainHandItem();

        //Checks what item is being held
        if (!stack.is(
                CosmereItems.TORN_PAGES.get()
        )) {

            //return an error if the wrong item is being helded
            source.sendFailure(
                    Component.literal(
                            "§cYou must hold Torn Pages in your main hand."
                    )
            );

            return 0;
        }

        //reads the Torn Pages data
        TornPagesData data =
                stack.get(
                        CosmereDataComponents
                                .TORN_PAGES_DATA
                                .get()
                );

        //Checks if there's existent data
        if (data == null) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§1------ §6TORN PAGES DATA §1------"
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§7Planet: §cNONE"
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§7Unlocked entries: §cNONE"
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§eThese Torn Pages contain no knowledge data."
                    ),
                    false
            );

            source.sendSuccess(
                    () -> Component.literal(
                            "§1-----------------------------"
                    ),
                    false
            );

            return 1;
        }

        //Output
        source.sendSuccess(
                () -> Component.literal(
                        "§1------ §6TORN PAGES DATA §1------"
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Planet: §b"
                                + data.planetId()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§7Unlocked entries: §d"
                                + data.entriesID().size()
                ),
                false
        );

        //Shows the unlocked entries
        if (data.entriesID().isEmpty()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "§7- §8No entries unlocked."
                    ),
                    false
            );

        } else {

            for (ResourceLocation entry
                    : data.entriesID()) {

                source.sendSuccess(
                        () -> Component.literal(
                                "§7- §d" + entry
                        ),
                        false
                );
            }
        }

        //Success advise
        source.sendSuccess(
                () -> Component.literal(
                        "§aTorn Pages data read: SUCCESS"
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "§1-----------------------------"
                ),
                false
        );

        return 1;
    }
}
