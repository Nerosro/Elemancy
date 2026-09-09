package be.nerosro.elemancy.items.tools.airspear;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Transient stance selected when a player begins using an Air Spear.
 */
final class AirSpearStance {
    private static final Map<Player, Type> ACTIVE_STANCES = new WeakHashMap<>();
    private static final Map<Player, Vec3> LAST_POSITIONS = new WeakHashMap<>();
    private static final double MOVEMENT_THRESHOLD_SQUARED = 0.0001D;

    private AirSpearStance() {
    }

    static void begin(Player player) {
        ACTIVE_STANCES.put(player, Type.PENDING);
        LAST_POSITIONS.put(player, player.position());
    }

    static boolean is(Player player, Type stance) {
        return ACTIVE_STANCES.get(player) == stance;
    }

    static Type get(Player player) {
        return ACTIVE_STANCES.get(player);
    }

    static void set(Player player, Type stance) {
        ACTIVE_STANCES.put(player, stance);
    }

    static void clear(Player player) {
        ACTIVE_STANCES.remove(player);
        LAST_POSITIONS.remove(player);
    }

    static boolean updateAndHasMovedHorizontally(Player player) {
        Vec3 currentPosition = player.position();
        Vec3 previousPosition = LAST_POSITIONS.put(player, currentPosition);
        if (previousPosition == null) {
            return false;
        }

        double deltaX = currentPosition.x - previousPosition.x;
        double deltaZ = currentPosition.z - previousPosition.z;
        return deltaX * deltaX + deltaZ * deltaZ > MOVEMENT_THRESHOLD_SQUARED;
    }

    enum Type {
        BRACE,
        BRACE_RELEASED,
        EXHAUSTED,
        INTERRUPTED,
        PENDING,
        RUNNING
    }
}