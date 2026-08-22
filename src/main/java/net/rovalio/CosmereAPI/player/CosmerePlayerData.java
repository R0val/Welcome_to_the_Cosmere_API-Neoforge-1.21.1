package net.rovalio.CosmereAPI.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Objects;

public class CosmerePlayerData implements INBTSerializable<CompoundTag> {

    private static final String TAG_SPIRITWEB = "Spiritweb";

    private static final String TAG_ONBOARDING_COMPLETE =
            "OnboardingComplete";

    private static final String TAG_ORIGIN_PLANET = "OriginPlanet";
    private static final String TAG_ORIGIN = "Origin";

    private ResourceLocation originPlanetId = null;
    private ResourceLocation originId = null;

    private final SpiritwebData spiritweb;

    //Onboarding
    private boolean onboardingComplete = false;

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

        this.onboardingComplete = true;
    }

    public void resetOnboarding() {
        this.onboardingComplete = false;
        this.originPlanetId = null;
        this.originId = null;
    }

    public void resetPlayer() {
        this.spiritweb.resetPlayer();
        resetOnboarding();
    }

    //Creation and registry of the Player's Spiritweb
    public CosmerePlayerData (){
        this.spiritweb = new SpiritwebData();
        resetPlayer();
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
    public CompoundTag serializeNBT(HolderLookup. Provider provider) {

        CompoundTag tag = new CompoundTag();

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

        if (tag.contains(
                TAG_SPIRITWEB,
                Tag.TAG_COMPOUND
        )) {
            spiritweb.loadNBT(
                    tag.getCompound(TAG_SPIRITWEB)
            );
        } else {
            spiritweb.loadNBT(
                    new CompoundTag()
            );
        }

        boolean loadedOnboardingComplete =
                tag.getBoolean(
                        TAG_ONBOARDING_COMPLETE
                );

        ResourceLocation loadedPlanetId = null;
        ResourceLocation loadedOriginId = null;

        if (tag.contains(TAG_ORIGIN_PLANET)) {
            loadedPlanetId =
                    ResourceLocation.tryParse(
                            tag.getString(
                                    TAG_ORIGIN_PLANET
                            )
                    );
        }

        if (tag.contains(TAG_ORIGIN)) {
            loadedOriginId =
                    ResourceLocation.tryParse(
                            tag.getString(
                                    TAG_ORIGIN
                            )
                    );
        }

        if (loadedOnboardingComplete
                && loadedPlanetId != null
                && loadedOriginId != null) {

            completeOnboarding(
                    loadedPlanetId,
                    loadedOriginId
            );

        } else {
            resetOnboarding();
        }
    }
}
