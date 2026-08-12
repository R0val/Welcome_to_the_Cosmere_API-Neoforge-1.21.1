package net.rovalio.CosmereAPI.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class CosmerePlayerData implements INBTSerializable<CompoundTag> {

    private static final int CURRENT_DATA_VERSION = 2;

    private static final String TAG_DATA_VERSION = "DataVersion";
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

    public void setOnboardingComplete(boolean onboardingComplete) {
        this.onboardingComplete = onboardingComplete;
    }

    //Creation and registry of the Player's Spiritweb
    public CosmerePlayerData (){
        this.spiritweb = new SpiritwebData();
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

    public void setOriginSelection(
            ResourceLocation originPlanetId,
            ResourceLocation originId
    ) {
        this.originPlanetId = originPlanetId;
        this.originId = originId;
    }

    public void clearOriginSelection() {
        this.originPlanetId = null;
        this.originId = null;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup. Provider provider) {

        CompoundTag tag = new CompoundTag();

        tag.putBoolean(
                TAG_ONBOARDING_COMPLETE,
                onboardingComplete
        );

        tag.putInt(
                TAG_DATA_VERSION,
                CURRENT_DATA_VERSION
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
        if (tag.contains(TAG_SPIRITWEB)) {
            spiritweb.loadNBT(
                    tag.getCompound(TAG_SPIRITWEB)
            );
        }
        onboardingComplete =
                tag.getBoolean(TAG_ONBOARDING_COMPLETE);

        originPlanetId = null;
        originId = null;

        if (tag.contains(TAG_ORIGIN_PLANET)) {
            originPlanetId =
                    ResourceLocation.tryParse(
                            tag.getString(TAG_ORIGIN_PLANET)
                    );
        }

        if (tag.contains(TAG_ORIGIN)) {
            originId =
                    ResourceLocation.tryParse(
                            tag.getString(TAG_ORIGIN)
                    );
        }
    }
}
