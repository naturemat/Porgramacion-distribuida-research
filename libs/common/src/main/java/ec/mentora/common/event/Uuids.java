package ec.mentora.common.event;

import java.security.SecureRandom;
import java.util.UUID;

public final class Uuids {

    private static final SecureRandom RANDOM = new SecureRandom();

    private Uuids() {
    }

    public static UUID v7() {
        long timestamp = System.currentTimeMillis() & 0xFFFFFFFFFFFFL;
        byte[] random = new byte[10];
        RANDOM.nextBytes(random);
        long msb = (timestamp << 16)
            | 0x7000L
            | ((random[0] & 0x0FL) << 8)
            | (random[1] & 0xFFL);
        long lsb = 0L;
        for (int index = 0; index < 8; index++) {
            lsb = (lsb << 8) | (random[index + 2] & 0xFFL);
        }
        lsb = (lsb & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        return new UUID(msb, lsb);
    }
}
