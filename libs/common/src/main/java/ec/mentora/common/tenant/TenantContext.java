package ec.mentora.common.tenant;

public final class TenantContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String tenantId) {
        CURRENT.set(tenantId);
    }

    public static String get() {
        return CURRENT.get();
    }

    public static String require() {
        String tenantId = CURRENT.get();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("tenant_id requerido");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
