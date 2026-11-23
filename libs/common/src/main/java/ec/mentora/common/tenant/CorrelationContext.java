package ec.mentora.common.tenant;

import ec.mentora.common.event.Uuids;

public final class CorrelationContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String correlationId) {
        CURRENT.set(correlationId);
    }

    public static String getOrCreate() {
        String current = CURRENT.get();
        if (current == null || current.isBlank()) {
            current = Uuids.v7().toString();
            CURRENT.set(current);
        }
        return current;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
