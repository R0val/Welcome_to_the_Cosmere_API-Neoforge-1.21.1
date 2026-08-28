package net.rovalio.CosmereAPI.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import net.minecraft.nbt.StringTag;
import net.rovalio.CosmereAPI.registry.CosmereRegistries;

///This class is used to compact all Spiritweb aspects from their individual classes,
/// so these aspects can be accessed easily from one individual class

public class SpiritwebData {

    //Sets default universal values
    public static final double DEFAULT_INTEGRITY = 1.0;
    public static final double DEFAULT_INVESTITURE_BEU = 1.0;
    public static final double DEFAULT_FORTUNE = 0.0;

    private static final String TAG_INTEGRITY = "Integrity";
    private static final String TAG_INVESTITURE = "InvestitureBEU";
    private static final String TAG_FORTUNE = "Fortune";
    private static final String TAG_IDENTITY = "Identity";
    private static final String TAG_CONNECTIONS = "Connections";

    private static final String TAG_INVESTED_ARTS = "InvestedArts";

    private double integrity;
    private double investitureBEU;
    private double fortune;

    private final IdentityData identity;
    private final List<ConnectionData> connections;

    private final Set<ResourceLocation> investedArts;

    //Sets default Spiritweb aspects. These can be modified later by the Cosmere addons
    public SpiritwebData() {
        this.integrity = DEFAULT_INTEGRITY;
        this.investitureBEU = DEFAULT_INVESTITURE_BEU;
        this.fortune = DEFAULT_FORTUNE;

        //These are unique for every player
        this.identity = new IdentityData();
        this.connections = new ArrayList<>();
        this.investedArts = new HashSet<>();
        resetPlayer();
    }

    /// Prohibites null, NaN, infinites and double entries
    private static double requireFinite(
            String fieldName,
            double value
    ) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(
                    fieldName + " must be finite"
            );
        }

        return value;
    }

    //Sets integrity of the Spiritweb. Spiritweb does not "lose hp" but integrity
    public double getIntegrity() {
        return integrity;
    }

    public void setIntegrity(double integrity) {
        this.integrity = Math.clamp(
                requireFinite(
                        "Spiritweb integrity",
                        integrity
                ),
                0.0,
                1.0
        );
    }

    /// Investiture
    //This is a Cosmere's unit used to quantify how much Investiture and entity has
    // Investiture is kind of "amount of magic energy" that an entity has.
    // It's measured in BEU (Breath Equivalent Unit)
    public double getInvestitureBEU() {
        return investitureBEU;
    }

    public void setInvestitureBEU(double investitureBEU) {
        this.investitureBEU = Math.max(
                0.0,
                requireFinite(
                        "Investiture BEU",
                        investitureBEU
                )
        );
    }

    /// FORTUNE
    //Fortune is useful in the canon to know certain aspects about the future.
    //In this mod it will surely be used as literal fortune, but it's WIP
    public double getFortune() {
        return fortune;
    }

    public void setFortune(double fortune) {
        this.fortune = requireFinite(
                "Fortune",
                fortune
        );
    }

    //Gets the UUID from IdentityData.java
    public IdentityData getIdentity() {
        return identity;
    }

    /// Connections
    //Gets the Connections list from ConnectionData.java
    public List<ConnectionData> getConnections() {
        return List.copyOf(connections);
    }

    private void storeConnection(
            ConnectionData connection
    ) {
        Objects.requireNonNull(
                connection,
                "Connection cannot be null"
        );

        connections.removeIf(
                existing -> existing.hasKey(
                        connection.type(),
                        connection.target()
                )
        );

        if (connection.strength()
                > ConnectionData.MIN_STRENGTH) {

            connections.add(connection);
        }
    }

    public void addConnection(
            ConnectionData connection
    ) {
        Objects.requireNonNull(
                connection,
                "Connection cannot be null"
        );

        setConnection(
                connection.type(),
                connection.target(),
                connection.strength()
        );
    }

    public void removeConnection(
            ConnectionData connection
    ) {
        Objects.requireNonNull(
                connection,
                "Connection cannot be null"
        );

        connections.removeIf(
                existing -> existing.hasKey(
                        connection.type(),
                        connection.target()
                )
        );
    }

    public ConnectionData getConnection(
            ConnectionType type,
            String target
    ) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(target);

        for (ConnectionData connection : connections) {
            if (connection.hasKey(type, target)) {
                return connection;
            }
        }

        return null;
    }

    public void setConnection(
            ConnectionType type,
            String target,
            int strength
    ) {
        Objects.requireNonNull(
                type,
                "Connection type cannot be null"
        );

        Objects.requireNonNull(
                target,
                "Connection target cannot be null"
        );

        ResourceLocation targetId =
                ResourceLocation.tryParse(target);

        if (targetId == null) {
            throw new IllegalArgumentException(
                    "Invalid Connection target ID: "
                            + target
            );
        }

        String canonicalTarget =
                targetId.toString();

        int normalizedStrength = Math.max(
                ConnectionData.MIN_STRENGTH,
                Math.min(
                        ConnectionData.MAX_STRENGTH,
                        strength
                )
        );

        if (normalizedStrength == 0) {
            connections.removeIf(
                    connection -> connection.hasKey(
                            type,
                            canonicalTarget
                    )
            );

            return;
        }

        if (!ConnectionTargetValidator.isValid(
                type,
                targetId
        )) {
            throw new IllegalArgumentException(
                    "Unknown or unsupported Connection target: "
                            + type
                            + " -> "
                            + canonicalTarget
            );
        }

        storeConnection(
                new ConnectionData(
                        type,
                        canonicalTarget,
                        normalizedStrength
                )
        );
    }

    public List<ConnectionData> getOrphanedConnections() {
        return connections.stream()
                .filter(
                        ConnectionTargetValidator::isOrphaned
                )
                .toList();
    }

    /// Invested Arts
    public Set<ResourceLocation> getInvestedArts() {
        return Set.copyOf(investedArts);
    }

    public boolean hasInvestedArt(
            ResourceLocation art
    ) {
        Objects.requireNonNull(
                art,
                "Invested Art ID cannot be null"
        );

        return investedArts.contains(art);
    }

    public boolean grantInvestedArt(
            ResourceLocation art
    ) {
        Objects.requireNonNull(
                art,
                "Invested Art ID cannot be null"
        );

        if (!CosmereRegistries
                .INVESTED_ART_REGISTRY
                .containsKey(art)) {

            throw new IllegalArgumentException(
                    "Unknown Invested Art ID: "
                            + art
            );
        }

        return investedArts.add(art);
    }

    public boolean revokeInvestedArt(
            ResourceLocation art
    ) {
        Objects.requireNonNull(
                art,
                "Invested Art ID cannot be null"
        );

        return investedArts.remove(art);
    }

    public List<ResourceLocation>
    getOrphanedInvestedArts() {

        return investedArts.stream()
                .filter(art ->
                        !CosmereRegistries
                                .INVESTED_ART_REGISTRY
                                .containsKey(art)
                )
                .sorted(
                        Comparator.comparing(
                                ResourceLocation::toString
                        )
                )
                .toList();
    }

    ///Resets only universal stats
    public void resetStats() {
        this.integrity = DEFAULT_INTEGRITY;
        this.investitureBEU = DEFAULT_INVESTITURE_BEU;
        this.fortune = DEFAULT_FORTUNE;
    }

    ///Resets the full Spiritweb
    public void resetPlayer() {
        resetStats();
        this.identity.reset();
        this.connections.clear();
        this.investedArts.clear();
    }

    //NBT constructor
    public CompoundTag saveNBT() {

        CompoundTag tag = new CompoundTag();

        tag.putDouble(TAG_INTEGRITY, integrity);
        tag.putDouble(TAG_INVESTITURE, investitureBEU);
        tag.putDouble(TAG_FORTUNE, fortune);

        tag.put(TAG_IDENTITY, identity.saveNBT());

        ListTag connectionsTag = new ListTag();

        for (ConnectionData connection : connections) {
            connectionsTag.add(connection.saveNBT());
        }

        tag.put(TAG_CONNECTIONS, connectionsTag);

        ListTag investedArtsTag =
                new ListTag();

        List<ResourceLocation> sortedInvestedArts =
                investedArts.stream()
                        .sorted(
                                Comparator.comparing(
                                        ResourceLocation::toString
                                )
                        )
                        .toList();

        for (ResourceLocation art : sortedInvestedArts) {
            investedArtsTag.add(
                    StringTag.valueOf(
                            art.toString()
                    )
            );
        }

        tag.put(
                TAG_INVESTED_ARTS,
                investedArtsTag
        );

        return tag;
    }

    public void loadNBT(CompoundTag tag) {

        integrity = DEFAULT_INTEGRITY;
        investitureBEU = DEFAULT_INVESTITURE_BEU;
        fortune = DEFAULT_FORTUNE;

        connections.clear();
        investedArts.clear();

        //checks if Player has a valor for every Spiritweb aspect. If not, this constructor gives the default valor

        if (tag.contains(TAG_INTEGRITY)) {
            double loadedIntegrity =
                    tag.getDouble(TAG_INTEGRITY);

            if (Double.isFinite(loadedIntegrity)) {
                setIntegrity(loadedIntegrity);
            }
        }

        if (tag.contains(TAG_INVESTITURE)) {
            double loadedInvestiture =
                    tag.getDouble(TAG_INVESTITURE);

            if (Double.isFinite(loadedInvestiture)) {
                setInvestitureBEU(loadedInvestiture);
            }
        }

        if (tag.contains(TAG_FORTUNE)) {
            double loadedFortune =
                    tag.getDouble(TAG_FORTUNE);

            if (Double.isFinite(loadedFortune)) {
                setFortune(loadedFortune);
            }
        }

        CompoundTag identityTag =
                tag.contains(
                        TAG_IDENTITY,
                        Tag.TAG_COMPOUND
                )
                        ? tag.getCompound(TAG_IDENTITY)
                        : new CompoundTag();

        identity.loadNBT(identityTag);

        if (tag.contains(TAG_CONNECTIONS, Tag.TAG_LIST)) {

            ListTag connectionsTag =
                    tag.getList(TAG_CONNECTIONS, Tag.TAG_COMPOUND);

            for (int i = 0; i < connectionsTag.size(); i++) {

                CompoundTag connectionTag =
                        connectionsTag.getCompound(i);

                ConnectionData connection =
                        ConnectionData.loadNBT(connectionTag);

                if (connection != null) {
                    storeConnection(connection);
                }
            }
        }

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
