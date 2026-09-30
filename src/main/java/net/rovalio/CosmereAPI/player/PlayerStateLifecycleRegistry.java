package net.rovalio.CosmereAPI.player;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.rovalio.CosmereAPI.util.CopyOnWriteRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PlayerStateLifecycleRegistry {

    private static final CopyOnWriteRegistry<
            ResourceLocation,
            Handler
            > HANDLERS =
            new CopyOnWriteRegistry<>();

    private PlayerStateLifecycleRegistry() {
    }

    public static void register(
            ResourceLocation handlerId,
            Handler handler
    ) {
        Objects.requireNonNull(
                handlerId,
                "Lifecycle handler ID cannot be null"
        );

        Objects.requireNonNull(
                handler,
                "Lifecycle handler cannot be null"
        );

        if (!HANDLERS.putIfAbsent(handlerId, handler)) {
            throw new IllegalStateException(
                    "A Player State Lifecycle handler "
                            + "is already registered with ID: "
                            + handlerId
            );
        }
    }

    public static boolean hasHandler(
            ResourceLocation handlerId
    ) {
        return HANDLERS.containsKey(handlerId);
    }

    public static RecalculationContext recalculate(
            ServerPlayer player,
            Reason reason
    ) {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        Objects.requireNonNull(
                reason,
                "Recalculation reason cannot be null"
        );

        RecalculationContext context =
                new RecalculationContext();

        RuntimeException firstFailure = null;

        for (Map.Entry<ResourceLocation, Handler> entry :
                HANDLERS.entries().entrySet()) {

            RecalculationContext handlerContext =
                    new RecalculationContext();

            try {
                entry.getValue()
                        .recalculate(
                                player,
                                reason,
                                handlerContext
                        );

                context.mergeFrom(
                        handlerContext
                );

            } catch (RuntimeException exception) {

                IllegalStateException wrappedException =
                        new IllegalStateException(
                                "Player State Lifecycle handler failed: "
                                        + entry.getKey(),
                                exception
                        );

                if (firstFailure == null) {
                    firstFailure = wrappedException;
                } else {
                    firstFailure.addSuppressed(
                            wrappedException
                    );
                }
            }
        }

        if (reason == Reason.RESET_STATS) {
            context.applyTo(player);
        }

        if (firstFailure != null) {
            throw firstFailure;
        }

        return context;
    }

    public static void reset(
            ServerPlayer player
    ) {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        RuntimeException firstFailure = null;

        for (Map.Entry<ResourceLocation, Handler> entry :
                HANDLERS.entries().entrySet()) {

            try {
                entry.getValue()
                        .reset(player);

            } catch (RuntimeException exception) {

                IllegalStateException wrappedException =
                        new IllegalStateException(
                                "Player State Lifecycle reset failed: "
                                        + entry.getKey(),
                                exception
                        );

                if (firstFailure == null) {
                    firstFailure = wrappedException;
                } else {
                    firstFailure.addSuppressed(
                            wrappedException
                    );
                }
            }
        }

        if (firstFailure != null) {
            throw firstFailure;
        }
    }

    @FunctionalInterface
    public interface Handler {

        void recalculate(
                ServerPlayer player,
                Reason reason,
                RecalculationContext context
        );

        default void reset(
                ServerPlayer player
        ) {
        }
    }

    public enum Reason {
        PLAYER_LOGIN,
        PLAYER_RESPAWN,
        RESET_STATS,
        RESET_ONBOARDING
    }

    public static final class RecalculationContext {

        private final Map<
                ResourceLocation,
                StatModifier
                > modifiers =
                new LinkedHashMap<>();

        private final Map<
                ResourceLocation,
                Component
                > messages =
                new LinkedHashMap<>();

        private RecalculationContext() {
        }

        public void addModifier(
                ResourceLocation sourceId,
                double integrityDelta,
                double investitureDelta,
                double fortuneDelta
        ) {
            Objects.requireNonNull(
                    sourceId,
                    "Modifier source ID cannot be null"
            );

            requireFinite(
                    "Integrity modifier",
                    integrityDelta
            );

            requireFinite(
                    "Investiture modifier",
                    investitureDelta
            );

            requireFinite(
                    "Fortune modifier",
                    fortuneDelta
            );

            StatModifier previous =
                    modifiers.putIfAbsent(
                            sourceId,
                            new StatModifier(
                                    integrityDelta,
                                    investitureDelta,
                                    fortuneDelta
                            )
                    );

            if (previous != null) {
                throw new IllegalStateException(
                        "A modifier is already registered "
                                + "for source: "
                                + sourceId
                );
            }
        }

        public void addMessage(
                ResourceLocation sourceId,
                Component message
        ) {
            Objects.requireNonNull(
                    sourceId,
                    "Message source ID cannot be null"
            );

            Objects.requireNonNull(
                    message,
                    "Correction message cannot be null"
            );

            Component previous =
                    messages.putIfAbsent(
                            sourceId,
                            message
                    );

            if (previous != null) {
                throw new IllegalStateException(
                        "A correction message is already "
                                + "registered for source: "
                                + sourceId
                );
            }
        }

        public List<Component> getMessages() {
            return List.copyOf(
                    new ArrayList<>(
                            messages.values()
                    )
            );
        }

        public boolean hasModifiers() {
            return !modifiers.isEmpty();
        }

        public boolean hasMessages() {
            return !messages.isEmpty();
        }

        public double getFinalIntegrity() {
            return SpiritwebData.DEFAULT_INTEGRITY
                    + modifiers.values()
                    .stream()
                    .mapToDouble(
                            StatModifier::integrityDelta
                    )
                    .sum();
        }

        public double getFinalInvestiture() {
            return SpiritwebData.DEFAULT_INVESTITURE_BEU
                    + modifiers.values()
                    .stream()
                    .mapToDouble(
                            StatModifier::investitureDelta
                    )
                    .sum();
        }

        public double getFinalFortune() {
            return SpiritwebData.DEFAULT_FORTUNE
                    + modifiers.values()
                    .stream()
                    .mapToDouble(
                            StatModifier::fortuneDelta
                    )
                    .sum();
        }

        private void applyTo(
                ServerPlayer player
        ) {

            SpiritwebData spiritweb =
                    CosmereAttachments.get(player)
                            .getSpiritweb();

            spiritweb.setIntegrity(
                    getFinalIntegrity()
            );

            spiritweb.setInvestitureBEU(
                    getFinalInvestiture()
            );

            spiritweb.setFortune(
                    getFinalFortune()
            );
        }

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

        private void mergeFrom(
                RecalculationContext source
        ) {
            Objects.requireNonNull(
                    source,
                    "Source recalculation context cannot be null"
            );

            for (ResourceLocation sourceId :
                    source.modifiers.keySet()) {

                if (modifiers.containsKey(sourceId)) {
                    throw new IllegalStateException(
                            "A modifier is already registered "
                                    + "for source: "
                                    + sourceId
                    );
                }
            }

            for (ResourceLocation sourceId :
                    source.messages.keySet()) {

                if (messages.containsKey(sourceId)) {
                    throw new IllegalStateException(
                            "A correction message is already "
                                    + "registered for source: "
                                    + sourceId
                    );
                }
            }

            modifiers.putAll(source.modifiers);
            messages.putAll(source.messages);
        }
    }

    private record StatModifier(
            double integrityDelta,
            double investitureDelta,
            double fortuneDelta
    ) {
    }
}