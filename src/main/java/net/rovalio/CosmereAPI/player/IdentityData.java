package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class IdentityData {

    private static final String TAG_ID = "Id";
    private static final String TAG_STRENGTH = "Strength";

    private UUID identityId;
    private double strength;

    //Gets Player UUID to have a unique key to use for Connections
    public IdentityData() {
        this.identityId = null;
        this.strength = 1.0;
    }

    public UUID getIdentityId() {
        return identityId;
    }

    public void initialize(UUID playerId) {
        if (this.identityId == null) {
            this.identityId = playerId;
        }
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = Math.max(0.0, strength);
    }

    //Save the Identity in NBT
    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();

        if (identityId != null) {
            tag.putUUID(TAG_ID, identityId);
        }

        tag.putDouble(TAG_STRENGTH, strength);

        return tag;
    }

    public void loadNBT(CompoundTag tag) {

        //Gets identity ID if its already storaged in NBT
        if (tag.hasUUID(TAG_ID)) {
            this.identityId = tag.getUUID(TAG_ID);
        }

        //Gets identity strength if its already storaged in NBT
        if (tag.contains(TAG_STRENGTH)) {
            setStrength(tag.getDouble(TAG_STRENGTH));
        }
    }
}
