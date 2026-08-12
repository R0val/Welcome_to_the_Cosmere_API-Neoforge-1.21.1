package net.rovalio.CosmereAPI.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.RandomGlobalOriginC2SPayload;
import net.rovalio.CosmereAPI.registry.test.TestDefinition;

public class TemporaryPlanetSelectionScreen extends Screen {

    public TemporaryPlanetSelectionScreen() {
        super(Component.literal("Select Origin Planet"));
    }

    @Override
    protected void init() {
        super.init();

        // Test Planet
        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Test Planet"),
                                button -> Minecraft.getInstance().setScreen(
                                        new TemporaryOriginSelectionScreen(
                                                TestDefinition.TEST_PLANET.getId()
                                        )
                                )
                        )
                        .bounds(
                                this.width / 2 - 75,
                                this.height / 2 - 10,
                                150,
                                20
                        )
                        .build()
        );

        // Global Random
        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Random"),
                                button -> PacketDistributor.sendToServer(
                                        RandomGlobalOriginC2SPayload.INSTANCE
                                )
                        )
                        .bounds(
                                this.width / 2 - 75,
                                this.height / 2 + 20,
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
                Component.literal("SELECT ORIGIN PLANET"),
                this.width / 2,
                this.height / 2 - 50,
                0xFFFFFF
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}