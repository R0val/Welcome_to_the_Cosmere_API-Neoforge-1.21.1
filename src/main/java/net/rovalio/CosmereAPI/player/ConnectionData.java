package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;

public class ConnectionData {

    private static final String TAG_TYPE = "Type";
    private static final String TAG_TARGET = "Target";
    private static final String TAG_STRENGTH = "Strength";

    private final ConnectionType type;
    private final String target;
    private double strength;

    //Sets the type, target and strength of the Connections between Player and its enviroment
    public ConnectionData(ConnectionType type, String target, double strength) {
        this.type = type;
        this.target = target;
        this.strength = strength;
    }

    public ConnectionType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString(TAG_TYPE, type.name());
        tag.putString(TAG_TARGET, target);
        tag.putDouble(TAG_STRENGTH, strength);

        return tag;
    }

    public static ConnectionData loadNBT(CompoundTag tag) {

        try {
            ConnectionType type =
                    ConnectionType.valueOf(tag.getString(TAG_TYPE));

            String target =
                    tag.getString(TAG_TARGET);

            double strength =
                    tag.getDouble(TAG_STRENGTH);

            return new ConnectionData(type, target, strength);

        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
