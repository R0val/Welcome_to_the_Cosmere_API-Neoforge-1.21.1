package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public record ConnectionData(
        ConnectionType type,
        String target,
        double strength
) {

    private static final String TAG_TYPE = "Type";
    private static final String TAG_TARGET = "Target";
    private static final String TAG_STRENGTH = "Strength";

    public ConnectionData {
        Objects.requireNonNull(
                type,
                "Connection type cannot be null"
        );

        Objects.requireNonNull(
                target,
                "Connection target cannot be null"
        );

        if (target.isBlank()) {
            throw new IllegalArgumentException(
                    "Connection target cannot be blank"
            );
        }

        if (!Double.isFinite(strength)) {
            throw new IllegalArgumentException(
                    "Connection strength must be finite"
            );
        }

        strength = Math.max(0.0, strength);
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString(TAG_TYPE, type.name());
        tag.putString(TAG_TARGET, target);
        tag.putDouble(TAG_STRENGTH, strength);

        return tag;
    }

    public static ConnectionData loadNBT(
            CompoundTag tag
    ) {
        try {
            ConnectionType type =
                    ConnectionType.valueOf(
                            tag.getString(TAG_TYPE)
                    );

            String target =
                    tag.getString(TAG_TARGET);

            double strength =
                    tag.getDouble(TAG_STRENGTH);

            return new ConnectionData(
                    type,
                    target,
                    strength
            );

        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}