package be.nerosro.elemancy.mana.depth;

/**
 * Persistent player knowledge of Mana Collapse.
 */
public enum ManaCollapseKnowledge {
    NEVER_EXPERIENCED((byte) 0),
    EXPERIENCED_UNDOCUMENTED((byte) 1),
    MIRROR_DIAGNOSED((byte) 2);

    private final byte id;

    ManaCollapseKnowledge(byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }

    public static ManaCollapseKnowledge byId(int id) {
        return switch (id) {
            case 1 -> EXPERIENCED_UNDOCUMENTED;
            case 2 -> MIRROR_DIAGNOSED;
            default -> NEVER_EXPERIENCED;
        };
    }
}