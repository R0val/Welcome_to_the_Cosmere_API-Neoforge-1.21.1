package net.rovalio.CosmereAPI.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.RandomPlanetOriginC2SPayload;
import net.rovalio.CosmereAPI.network.payload.SelectOriginC2SPayload;
import net.rovalio.CosmereAPI.registry.test.TestDefinition;

public class TemporaryOriginSelectionScreen extends Screen {

    private final ResourceLocation planetId;

    public TemporaryOriginSelectionScreen(
            ResourceLocation planetId
    ) {
        super(Component.literal("Select Origin"));
        this.planetId = planetId;
    }

    @Override
    protected void init() {
        super.init();

        // Test Origin A
        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Test Origin A"),
                                button -> PacketDistributor.sendToServer(
                                        new SelectOriginC2SPayload(
                                                TestDefinition.TEST_ORIGIN_A.getId()
                                        )
                                )
                        )
                        .bounds(
                                this.width / 2 - 75,
                                this.height / 2 - 20,
                                150,
                                20
                        )
                        .build()
        );

        // Test Origin B
        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Test Origin B"),
                                button -> PacketDistributor.sendToServer(
                                        new SelectOriginC2SPayload(
                                                TestDefinition.TEST_ORIGIN_B.getId()
                                        )
                                )
                        )
                        .bounds(
                                this.width / 2 - 75,
                                this.height / 2 + 10,
                                150,
                                20
                        )
                        .build()
        );

        // Random origin within selected planet
        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Random"),
                                button -> PacketDistributor.sendToServer(
                                        new RandomPlanetOriginC2SPayload(
                                                planetId
                                        )
                                )
                        )
                        .bounds(
                                this.width / 2 - 75,
                                this.height / 2 + 40,
                                150,
                                20
                        )
                        .build()
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        this.renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "ORIGINS — " + planetId
                ),
                this.width / 2,
                this.height / 2 - 60,
                0xFFFFFF
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}