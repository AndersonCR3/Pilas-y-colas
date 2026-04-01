package uptc.edu.co.structures;

public enum CollectionMode {
    QUEUE,
    STACK;

    public static CollectionMode from(String raw, CollectionMode fallback) {
        if (raw == null) {
            return fallback;
        }
        String value = raw.trim().toUpperCase();
        if ("STACK".equals(value)) {
            return STACK;
        }
        if ("QUEUE".equals(value)) {
            return QUEUE;
        }
        return fallback;
    }
}
