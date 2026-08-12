package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import net.minecraft.nbt.StringTag;

///This class is used to compact all Spiritweb aspects from their individual classes,
/// so these aspects can be accessed easily from one individual class

public class SpiritwebData {

    //Sets default universal values
    public static final double DEFAULT_SIZE = 1.0;
    public static final double DEFAULT_INTEGRITY = 1.0;
    public static final double DEFAULT_INVESTITURE_BEU = 1.0;
    public static final double DEFAULT_FORTUNE = 0.0;

    private static final String TAG_SIZE = "Size";
    private static final String TAG_INTEGRITY = "Integrity";
    private static final String TAG_INVESTITURE = "InvestitureBEU";
    private static final String TAG_FORTUNE = "Fortune";
    private static final String TAG_IDENTITY = "Identity";
    private static final String TAG_CONNECTIONS = "Connections";

    private static final String TAG_INVESTED_ARTS = "InvestedArts";

    private double size;
    private double integrity;
    private double investitureBEU;
    private double fortune;

    private final IdentityData identity;
    private final List<ConnectionData> connections;

    private final Set<ResourceLocation> investedArts;

    //Sets default Spiritweb aspects. These can be modified later by the Cosmere addons
    public SpiritwebData() {
        this.size = DEFAULT_SIZE;
        this.integrity = DEFAULT_INTEGRITY;
        this.investitureBEU = DEFAULT_INVESTITURE_BEU;
        this.fortune = DEFAULT_FORTUNE;

        //These two are unique for every player
        this.identity = new IdentityData();
        this.connections = new ArrayList<>();

        this.investedArts = new HashSet<>();

    }

    //Sets size "HP" of the Spiritweb.
    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = Math.max(0.0, size);
    }

    //Sets integrity of the Spiritweb. Spiritweb does not "lose hp" but integrity
    public double getIntegrity() {
        return integrity;
    }

    public void setIntegrity(double integrity) {
        this.integrity = Math.clamp(integrity, 0.0, 1.0);
    }

    ///This is a Cosmere's unit used to quantify how much Investiture and entity has
    /// Investiture is kind of "amount of magic energy" that an entity has.
    /// It's measured in BEU (Breath Equivalent Unit)
    public double getInvestitureBEU() {
        return investitureBEU;
    }

    public void setInvestitureBEU(double investitureBEU) {
        this.investitureBEU = Math.max(0.0, investitureBEU);
    }

    ///Fortune is useful in the canon to know certain aspects about the future.
    ///In this mod it will surely be used as literal fortune, but it's WIP
    public double getFortune() {
        return fortune;
    }

    public void setFortune(double fortune) {
        this.fortune = fortune;
    }

    //Gets the UUID from IdentityData.java
    public IdentityData getIdentity() {
        return identity;
    }

    //Gets the Connections list from ConnectionData.java
    public List<ConnectionData> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    public void addConnection(ConnectionData connection) {
        connections.add(connection);
    }

    public void removeConnection(ConnectionData connection) {
        connections.remove(connection);
    }

    public ConnectionData getConnection(ConnectionType type, String target) {

        for (ConnectionData connection : connections) {
            if (connection.getType() == type &&
                    connection.getTarget().equals(target)) {

                return connection;
            }
        }

        return null;
    }

    public void setConnection(ConnectionType type, String target, double strength) {

        for (ConnectionData connection : connections) {
            if (connection.getType() == type &&
                    connection.getTarget().equals(target)) {

                connection.setStrength(strength);
                return;
            }
        }

        connections.add(new ConnectionData(type, target, strength));
    }

    public Set<ResourceLocation> getInvestedArts() {
        return Collections.unmodifiableSet(investedArts);
    }

    public boolean hasInvestedArt(ResourceLocation art) {
        return investedArts.contains(art);
    }

    public void grantInvestedArt(ResourceLocation art) {
        investedArts.add(art);
    }

    public void revokeInvestedArt(ResourceLocation art) {
        investedArts.remove(art);
    }

    //NBT constructor
    public CompoundTag saveNBT() {

        CompoundTag tag = new CompoundTag();

        tag.putDouble(TAG_SIZE, size);
        tag.putDouble(TAG_INTEGRITY, integrity);
        tag.putDouble(TAG_INVESTITURE, investitureBEU);
        tag.putDouble(TAG_FORTUNE, fortune);

        tag.put(TAG_IDENTITY, identity.saveNBT());

        ListTag connectionsTag = new ListTag();

        for (ConnectionData connection : connections) {
            connectionsTag.add(connection.saveNBT());
        }

        tag.put(TAG_CONNECTIONS, connectionsTag);

        ListTag investedArtsTag = new ListTag();

        for (ResourceLocation art : investedArts) {
            investedArtsTag.add(StringTag.valueOf(art.toString()));
        }

        tag.put(TAG_INVESTED_ARTS, investedArtsTag);

        return tag;
    }

    public void loadNBT(CompoundTag tag) {

        //checks if Player has a valor for every Spiritweb aspect. If not, this constructor gives the default valor

        if (tag.contains(TAG_SIZE)) {
            setSize(tag.getDouble(TAG_SIZE));
        }

        if (tag.contains(TAG_INTEGRITY)) {
            setIntegrity(tag.getDouble(TAG_INTEGRITY));
        }

        if (tag.contains(TAG_INVESTITURE)) {
            setInvestitureBEU(tag.getDouble(TAG_INVESTITURE));
        }

        if (tag.contains(TAG_FORTUNE)) {
            setFortune(tag.getDouble(TAG_FORTUNE));
        }

        if (tag.contains(TAG_IDENTITY, Tag.TAG_COMPOUND)) {
            identity.loadNBT(tag.getCompound(TAG_IDENTITY));
        }

        connections.clear();

        if (tag.contains(TAG_CONNECTIONS, Tag.TAG_LIST)) {

            ListTag connectionsTag =
                    tag.getList(TAG_CONNECTIONS, Tag.TAG_COMPOUND);

            for (int i = 0; i < connectionsTag.size(); i++) {

                CompoundTag connectionTag =
                        connectionsTag.getCompound(i);

                ConnectionData connection =
                        ConnectionData.loadNBT(connectionTag);

                if (connection != null) {
                    connections.add(connection);
                }
            }
        }

        investedArts.clear();

        if (tag.contains(TAG_INVESTED_ARTS, Tag.TAG_LIST)) {

            ListTag investedArtsTag =
                    tag.getList(TAG_INVESTED_ARTS, Tag.TAG_STRING);

            for (int i = 0; i < investedArtsTag.size(); i++) {

                ResourceLocation art =
                        ResourceLocation.tryParse(
                                investedArtsTag.getString(i)
                        );

                if (art != null) {
                    investedArts.add(art);
                }
            }
        }
    }
}
