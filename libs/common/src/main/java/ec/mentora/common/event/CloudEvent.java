package ec.mentora.common.event;

import ec.mentora.common.tenant.CorrelationContext;

import java.time.Instant;

public record CloudEvent<T>(
    String specversion,
    String id,
    String source,
    String type,
    String subject,
    Instant time,
    String datacontenttype,
    String tenantId,
    String correlationId,
    String causationId,
    String traceparent,
    T data
) {

    public static <T> CloudEvent<T> of(String source, String type, String subject, String tenantId, T data) {
        return new CloudEvent<>(
            "1.0",
            Uuids.v7().toString(),
            source,
            type,
            subject,
            Instant.now(),
            "application/json",
            tenantId,
            CorrelationContext.getOrCreate(),
            null,
            null,
            data
        );
    }
}
