package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public record ConnectionData(
        ConnectionType type,
        String target,
        int strength
) {

    private static final String TAG_TYPE = "Type";
    private static final String TAG_TARGET = "Target";
    private static final String TAG_STRENGTH = "Strength";

    public static final int MIN_STRENGTH = 0;
    public static final int MAX_STRENGTH = 100;

    public boolean hasKey(
            ConnectionType type,
            String target
    ) {
        return this.type == Objects.requireNonNull(
                type,
                "Connection type cannot be null"
        ) && this.target.equals(
                Objects.requireNonNull(
                        target,
                        "Connection target cannot be null"
                )
        );
    }

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

        strength = normalizeStrength(strength);
    }

    private static int normalizeStrength(int strength) {
        return Math.max(
                MIN_STRENGTH,
                Math.min(MAX_STRENGTH, strength)
        );
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString(TAG_TYPE, type.name());
        tag.putString(TAG_TARGET, target);
        tag.putInt(TAG_STRENGTH, strength);

        return tag;
    }

    public static ConnectionData loadNBT(
            CompoundTag tag
    ) {
        if (tag == null) {
            return null;
        }

        try {
            ConnectionType type =
                    ConnectionType.valueOf(
                            tag.getString(TAG_TYPE)
                    );

            String target =
                    tag.getString(TAG_TARGET);

            int strength = normalizeStrength(
                    tag.getInt(TAG_STRENGTH)
            );

            // 0 strength means Connection does not exists anymore
            if (strength == 0) {
                return null;
            }

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