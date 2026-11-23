package ec.mentora.common.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public final class Events {

    private Events() {
    }

    public static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    public static UUID uuid(JsonNode node, String field) {
        String value = text(node, field);
        return value == null ? null : UUID.fromString(value);
    }

    public static Instant instant(JsonNode node, String field) {
        String value = text(node, field);
        return value == null ? null : Instant.parse(value);
    }

    public static int integer(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? 0 : value.asInt();
    }

    public static boolean bool(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && value.asBoolean();
    }
}
