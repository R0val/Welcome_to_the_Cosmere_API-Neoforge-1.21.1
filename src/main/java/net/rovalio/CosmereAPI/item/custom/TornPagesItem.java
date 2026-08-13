package net.rovalio.CosmereAPI.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.rovalio.CosmereAPI.data.CosmereDataComponents;
import net.rovalio.CosmereAPI.data.TornPagesData;
import net.rovalio.CosmereAPI.item.CosmereItems;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TornPagesItem extends Item {

    public TornPagesItem(
            Properties properties
    ) {
        super(properties);
    }

    public static ItemStack create(
            ResourceLocation planetId,
            Collection<ResourceLocation> entries
    ) {

        Objects.requireNonNull(
                planetId,
                "Torn Pages planet cannot be null"
        );

        Objects.requireNonNull(
                entries,
                "Torn Pages entries cannot be null"
        );

        List<ResourceLocation> normalizedEntries =
                entries.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        ItemStack stack =
                new ItemStack(
                        CosmereItems.TORN_PAGES.get()
                );

        stack.set(
                CosmereDataComponents.TORN_PAGES_DATA.get(),
                new TornPagesData(
                        planetId,
                        normalizedEntries
                )
        );

        return stack;
    }
}