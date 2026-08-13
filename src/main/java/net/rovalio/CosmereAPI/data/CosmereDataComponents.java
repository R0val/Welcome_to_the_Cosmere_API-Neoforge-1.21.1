package net.rovalio.CosmereAPI.data;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rovalio.CosmereAPI.CosmereAPI;

public final class CosmereDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(
                    CosmereAPI.MOD_ID
            );

    public static final DeferredHolder<
            DataComponentType<?>,
            DataComponentType<TornPagesData>
            > TORN_PAGES_DATA =
            DATA_COMPONENTS.registerComponentType(
                    "torn_pages_data",
                    builder ->
                            builder.persistent(
                                    TornPagesData.CODEC
                            )
            );

    private CosmereDataComponents() {
    }

    public static void register(
            IEventBus modEventBus
    ) {
        DATA_COMPONENTS.register(modEventBus);
    }
}