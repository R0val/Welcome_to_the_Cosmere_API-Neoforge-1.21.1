package net.rovalio.CosmereAPI.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class CosmerePlayerData implements INBTSerializable<CompoundTag> {

    private static final int CURRENT_DATA_VERSION = 1;

    private static final String TAG_DATA_VERSION = "DataVersion";
    private static final String TAG_SPIRITWEB = "Spiritweb";

    private static final String TAG_ONBOARDING_COMPLETE =
            "OnboardingComplete";

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
    }
}
