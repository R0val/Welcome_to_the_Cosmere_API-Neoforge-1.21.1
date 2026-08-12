package net.rovalio.CosmereAPI.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TemporaryOnboardingScreen extends Screen {

    public TemporaryOnboardingScreen() {
        super(Component.literal("Cosmere Onboarding"));
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Continue"),
                                button -> Minecraft.getInstance().setScreen(
                                        new TemporaryPlanetSelectionScreen()
                                )
                        )
                        .bounds(
                                this.width / 2 - 50,
                                this.height / 2 + 40,
                                100,
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

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal("COSMERE API"),
                this.width / 2,
                this.height / 2 - 20,
                0xFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal("Temporary onboarding screen"),
                this.width / 2,
                this.height / 2,
                0xAAAAAA
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal("Origin selection will be implemented next."),
                this.width / 2,
                this.height / 2 + 20,
                0xAAAAAA
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}