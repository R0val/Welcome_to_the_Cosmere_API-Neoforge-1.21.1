package net.rovalio.CosmereAPI.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Objects;

public class CosmerePlayerData
        implements INBTSerializable<CompoundTag> {

    private static final String
            TAG_SPIRITWEB =
            "Spiritweb";

    private static final String
            TAG_ONBOARDING_COMPLETE =
            "OnboardingComplete";

    private static final String
            TAG_ORIGIN_PLANET =
            "OriginPlanet";

    private static final String
            TAG_ORIGIN =
            "Origin";

    private final SpiritwebData spiritweb;

    private ResourceLocation originPlanetId;
    private ResourceLocation originId;

    private boolean onboardingComplete;

    public CosmerePlayerData() {
        spiritweb = new SpiritwebData();
        resetPlayer();
    }

    public boolean isOnboardingComplete() {
        return onboardingComplete;
    }

    public void completeOnboarding(
            ResourceLocation originPlanetId,
            ResourceLocation originId
    ) {
        this.originPlanetId =
                Objects.requireNonNull(
                        originPlanetId,
                        "Origin planet ID cannot be null"
                );

        this.originId =
                Objects.requireNonNull(
                        originId,
                        "Origin ID cannot be null"
                );

        onboardingComplete = true;
    }

    public void resetOnboarding() {
        onboardingComplete = false;
        originPlanetId = null;
        originId = null;
    }

    public void resetPlayer() {
        spiritweb.resetPlayer();
        resetOnboarding();
    }

    public SpiritwebData getSpiritweb() {
        return spiritweb;
    }

    public ResourceLocation getOriginPlanetId() {
        return originPlanetId;
    }

    public ResourceLocation getOriginId() {
        return originId;
    }

    @Override
    public CompoundTag serializeNBT(
            HolderLookup.Provider provider
    ) {
        CompoundTag tag =
                new CompoundTag();

        tag.putBoolean(
                TAG_ONBOARDING_COMPLETE,
                onboardingComplete
        );

        tag.put(
                TAG_SPIRITWEB,
                spiritweb.saveNBT()
        );

        if (originPlanetId != null) {
            tag.putString(
                    TAG_ORIGIN_PLANET,
                    originPlanetId.toString()
            );
        }

        if (originId != null) {
            tag.putString(
                    TAG_ORIGIN,
                    originId.toString()
            );
        }

        return tag;
    }

    @Override
    public void deserializeNBT(
            HolderLookup.Provider provider,
            CompoundTag tag
    ) {

        resetPlayer();

        if (tag == null) {
            return;
        }

        if (tag.contains(
                TAG_SPIRITWEB,
                Tag.TAG_COMPOUND
        )) {
            spiritweb.loadNBT(
                    tag.getCompound(
                            TAG_SPIRITWEB
                    )
            );
        }

        boolean loadedOnboardingComplete =
                tag.contains(
                        TAG_ONBOARDING_COMPLETE,
                        Tag.TAG_BYTE
                )
                        && tag.getBoolean(
                        TAG_ONBOARDING_COMPLETE
                );

        ResourceLocation loadedPlanetId =
                loadResourceLocation(
                        tag,
                        TAG_ORIGIN_PLANET
                );

        ResourceLocation loadedOriginId =
                loadResourceLocation(
                        tag,
                        TAG_ORIGIN
                );

        if (loadedOnboardingComplete
                && loadedPlanetId != null
                && loadedOriginId != null) {

            completeOnboarding(
                    loadedPlanetId,
                    loadedOriginId
            );
        }
    }

    private static ResourceLocation
    loadResourceLocation(
            CompoundTag tag,
            String key
    ) {
        if (!tag.contains(
                key,
                Tag.TAG_STRING
        )) {
            return null;
        }

        return ResourceLocation.tryParse(
                tag.getString(key)
        );
    }
}