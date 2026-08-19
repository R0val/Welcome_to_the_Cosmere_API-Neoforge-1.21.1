package net.rovalio.CosmereAPI.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.RandomGlobalOriginC2SPayload;
import net.rovalio.CosmereAPI.onboarding.OnboardingRegistryAccess;

import java.util.List;

public class TemporaryPlanetSelectionScreen extends AbstractOnboardingScreen {

    private List<ResourceLocation> planets;

    public TemporaryPlanetSelectionScreen() {
        super(Component.literal("Select Origin Planet"));
    }

    @Override
    protected void init() {
        super.init();

        planets =
                OnboardingRegistryAccess.getSelectablePlanets();

        int buttonWidth = 180;
        int buttonHeight = 20;
        int spacing = 24;

        int startY =
                this.height / 2
                        - ((planets.size() + 1) * spacing) / 2;

        // PLANETS

        for (int i = 0; i < planets.size(); i++) {

            ResourceLocation planetId =
                    planets.get(i);

            this.addRenderableWidget(
                    Button.builder(
                                    Component.literal(
                                            formatName(planetId)
                                    ),
                                    button ->
                                            Minecraft.getInstance().setScreen(
                                                    new TemporaryOriginSelectionScreen(
                                                            planetId
                                                    )
                                            )
                            )
                            .bounds(
                                    this.width / 2 - buttonWidth / 2,
                                    startY + i * spacing,
                                    buttonWidth,
                                    buttonHeight
                            )
                            .build()
            );
        }

        // GLOBAL RANDOM

        Button randomButton =
                Button.builder(
                                Component.literal("Random"),
                                button ->
                                        PacketDistributor.sendToServer(
                                                RandomGlobalOriginC2SPayload.INSTANCE
                                        )
                        )
                        .bounds(
                                this.width / 2 - buttonWidth / 2,
                                startY + planets.size() * spacing,
                                buttonWidth,
                                buttonHeight
                        )
                        .build();

        randomButton.active = !planets.isEmpty();

        this.addRenderableWidget(randomButton);
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
                Component.literal("SELECT ORIGIN PLANET"),
                this.width / 2,
                30,
                0xFFFFFF
        );

        if (planets != null && planets.isEmpty()) {

            graphics.drawCenteredString(
                    this.font,
                    Component.literal(
                            "No origin planets are currently available."
                    ),
                    this.width / 2,
                    this.height / 2 - 30,
                    0xFF5555
            );
        }

        this.renderOnboardingResult(
                graphics
        );
    }

    private static String formatName(
            ResourceLocation id
    ) {

        String[] parts =
                id.getPath().split("_");

        StringBuilder result =
                new StringBuilder();

        for (String part : parts) {

            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(
                    Character.toUpperCase(
                            part.charAt(0)
                    )
            );

            result.append(
                    part.substring(1)
            );
        }

        return result.toString();
    }
}