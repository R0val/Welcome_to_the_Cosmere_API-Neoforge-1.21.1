package net.rovalio.CosmereAPI.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rovalio.CosmereAPI.CosmereAPI;
import net.rovalio.CosmereAPI.item.custom.TornPagesItem;

import java.util.function.Supplier;

public final class CosmereItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(
                    CosmereAPI.MOD_ID
            );

    public static final Supplier<TornPagesItem> TORN_PAGES =
            ITEMS.registerItem(
                    "torn_pages",
                    TornPagesItem::new,
                    new Item.Properties()
                            .stacksTo(1)
            );

    private CosmereItems() {

    }

    public static void register(
            IEventBus modEventBus
    ) {
        ITEMS.register(modEventBus);
    }
}