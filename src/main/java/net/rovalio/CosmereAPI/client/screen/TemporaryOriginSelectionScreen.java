package net.rovalio.CosmereAPI.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rovalio.CosmereAPI.network.payload.RandomPlanetOriginC2SPayload;
import net.rovalio.CosmereAPI.network.payload.SelectOriginC2SPayload;
import net.rovalio.CosmereAPI.onboarding.OnboardingRegistryAccess;

import java.util.List;

public class TemporaryOriginSelectionScreen extends Screen {

    private final ResourceLocation planetId;

    private List<ResourceLocation> origins;

    public TemporaryOriginSelectionScreen(
            ResourceLocation planetId
    ) {

        super(Component.literal("Select Origin"));

        this.planetId = planetId;
    }

    @Override
    protected void init() {
        super.init();

        origins =
                OnboardingRegistryAccess
                        .getSelectableOriginsForPlanet(
                                planetId
                        );

        int buttonWidth = 180;
        int buttonHeight = 20;
        int spacing = 24;

        int startY =
                this.height / 2
                        - ((origins.size() + 2) * spacing) / 2;

        // =====================================================
        // ORIGINS
        // =====================================================

        for (int i = 0; i < origins.size(); i++) {

            ResourceLocation originId =
                    origins.get(i);

            this.addRenderableWidget(
                    Button.builder(
                                    Component.literal(
                                            formatName(originId)
                                    ),
                                    button ->
                                            PacketDistributor.sendToServer(
                                                    new SelectOriginC2SPayload(
                                                            originId
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

        // =====================================================
        // RANDOM WITHIN PLANET
        // =====================================================

        Button randomButton =
                Button.builder(
                                Component.literal("Random"),
                                button ->
                                        PacketDistributor.sendToServer(
                                                new RandomPlanetOriginC2SPayload(
                                                        planetId
                                                )
                                        )
                        )
                        .bounds(
                                this.width / 2 - buttonWidth / 2,
                                startY + origins.size() * spacing,
                                buttonWidth,
                                buttonHeight
                        )
                        .build();

        randomButton.active = !origins.isEmpty();

        this.addRenderableWidget(randomButton);


        // =====================================================
        // BACK
        // =====================================================

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Back"),
                                button ->
                                        this.minecraft.setScreen(
                                                new TemporaryPlanetSelectionScreen()
                                        )
                        )
                        .bounds(
                                this.width / 2 - buttonWidth / 2,
                                startY + (origins.size() + 1) * spacing,
                                buttonWidth,
                                buttonHeight
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
                Component.literal(
                        "ORIGINS — "
                                + formatName(planetId)
                ),
                this.width / 2,
                30,
                0xFFFFFF
        );

        if (origins != null && origins.isEmpty()) {

            graphics.drawCenteredString(
                    this.font,
                    Component.literal(
                            "No origins are available for this planet."
                    ),
                    this.width / 2,
                    this.height / 2 - 30,
                    0xFF5555
            );
        }
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}