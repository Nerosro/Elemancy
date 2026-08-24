package be.nerosro.elemancy.network;

/**
 * Common payload state consumed by the client-only ritual camera handler.
 */
public final class RitualCameraLockState {

    private static boolean locked;

    private RitualCameraLockState() {
    }

    public static boolean isLocked() {
        return locked;
    }

    public static void setLocked(boolean locked) {
        RitualCameraLockState.locked = locked;
    }
}