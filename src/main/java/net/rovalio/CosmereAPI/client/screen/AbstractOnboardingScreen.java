package net.rovalio.CosmereAPI.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.rovalio.CosmereAPI.onboarding.OnboardingRegistryAccess;
import net.rovalio.CosmereAPI.onboarding.OnboardingResult;

import java.util.Locale;

public abstract class AbstractOnboardingScreen
        extends Screen {

    private OnboardingResult lastResult;

    protected AbstractOnboardingScreen(
            Component title
    ) {
        super(title);
    }

    public final void setOnboardingResult(
            OnboardingResult result
    ) {
        lastResult = result != null
                ? result
                : OnboardingResult.INTERNAL_ERROR;
    }

    protected final void renderOnboardingResult(
            GuiGraphics graphics
    ) {
        if (lastResult == null
                || lastResult.isSuccess()) {
            return;
        }

        graphics.drawCenteredString(
                this.font,
                Component.translatable(
                        getTranslationKey(lastResult)
                ),
                this.width / 2,
                this.height - 30,
                0xFF5555
        );
    }

    private static String getTranslationKey(
            OnboardingResult result
    ) {
        return "message.cosmere_api.onboarding."
                + result.name()
                .toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !OnboardingRegistryAccess
                .hasAvailableOrigins();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}