package net.rovalio.CosmereAPI.onboarding;

public enum OnboardingResult {

    SUCCESS(true),

    INVALID_REQUEST(false),
    ALREADY_COMPLETE(false),

    UNKNOWN_ORIGIN(false),
    ORIGIN_NOT_SELECTABLE(false),

    UNKNOWN_PLANET(false),
    PLANET_NOT_SELECTABLE(false),

    ORIGIN_PLANET_MISMATCH(false),
    ORIGIN_ALREADY_SELECTED(false),

    MISSING_INITIALIZER(false),
    NO_AVAILABLE_PLANETS(false),
    NO_AVAILABLE_ORIGINS(false),

    INITIALIZATION_FAILED(false),
    REWARD_DELIVERY_FAILED(false),

    INTERNAL_ERROR(false);

    private final boolean success;

    OnboardingResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}