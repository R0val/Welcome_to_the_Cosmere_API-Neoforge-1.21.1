package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;
import java.util.UUID;

public class IdentityData {

    public static final double DEFAULT_STRENGTH = 1.0;

    private static final String TAG_ID = "Id";
    private static final String TAG_STRENGTH = "Strength";

    private UUID identityId;
    private double strength;

    public IdentityData() {
        reset();
    }

    public UUID getIdentityId() {
        return identityId;
    }

    public void initialize(UUID playerId) {
        if (this.identityId == null) {
            this.identityId =
                    Objects.requireNonNull(
                            playerId,
                            "Player ID cannot be null"
                    );
        }
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        if (!Double.isFinite(strength)) {
            throw new IllegalArgumentException(
                    "Identity strength must be finite"
            );
        }

        this.strength =
                Math.max(
                        0.0,
                        strength
                );
    }

    public void reset() {
        this.identityId = null;
        this.strength = DEFAULT_STRENGTH;
    }

    public static CompoundTag migrateNBT(
            CompoundTag sourceTag
    ) {
        CompoundTag migratedTag =
                sourceTag == null
                        ? new CompoundTag()
                        : sourceTag.copy();

        double migratedStrength =
                DEFAULT_STRENGTH;

        if (migratedTag.contains(
                TAG_STRENGTH,
                Tag.TAG_ANY_NUMERIC
        )) {
            double loadedStrength =
                    migratedTag.getDouble(
                            TAG_STRENGTH
                    );

            if (Double.isFinite(loadedStrength)) {
                migratedStrength =
                        Math.max(
                                0.0,
                                loadedStrength
                        );
            }
        }

        migratedTag.putDouble(
                TAG_STRENGTH,
                migratedStrength
        );

        return migratedTag;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag =
                new CompoundTag();

        if (identityId != null) {
            tag.putUUID(
                    TAG_ID,
                    identityId
            );
        }

        tag.putDouble(
                TAG_STRENGTH,
                strength
        );

        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        reset();

        if (tag == null) {
            return;
        }

        CompoundTag migratedTag =
                migrateNBT(tag);

        if (migratedTag.hasUUID(TAG_ID)) {
            this.identityId =
                    migratedTag.getUUID(TAG_ID);
        }

        setStrength(
                migratedTag.getDouble(
                        TAG_STRENGTH
                )
        );
    }
}