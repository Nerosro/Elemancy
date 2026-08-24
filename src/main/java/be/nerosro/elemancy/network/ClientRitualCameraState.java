package be.nerosro.elemancy.network;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Enforces the ritual camera and restores the player's original point of view afterward.
 */
public final class ClientRitualCameraState {

    private static boolean locked;
    private static CameraType originalCameraType;

    private ClientRitualCameraState() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (RitualCameraLockState.isLocked()) {
            lock();
            Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
        } else {
            restore();
        }
    }

    public static void restore() {
        if (!locked) return;

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.options.setCameraType(originalCameraType);
        originalCameraType = null;
        locked = false;
    }

    private static void lock() {
        if (locked) return;

        Minecraft minecraft = Minecraft.getInstance();
        originalCameraType = minecraft.options.getCameraType();
        minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        locked = true;
    }
}