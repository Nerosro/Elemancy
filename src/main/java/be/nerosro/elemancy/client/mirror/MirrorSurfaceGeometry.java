package be.nerosro.elemancy.client.mirror;

import org.joml.Vector3f;

import net.minecraft.core.Direction;

/**
 * Direct vertex representation of the Mirror's verified front surface.
 */
public final class MirrorSurfaceGeometry {

    private static final float LEFT = 1.0f / 16.0f;
    private static final float RIGHT = 15.0f / 16.0f;
    private static final float HEIGHT = 2.0f;
    private static final float PIVOT_Z = 2.0f / 16.0f;
    private static final float FRONT_DEPTH = PIVOT_Z - 0.01f;
    private static final float LEAN_RADIANS = (float) Math.toRadians(7.5f);
    private static final float PANEL_LEFT = 1.0f / 14.0f;
    private static final float PANEL_RIGHT = 13.0f / 14.0f;
    private static final float PANEL_BOTTOM = 1.0f / 32.0f;
    private static final float PANEL_TOP = 31.0f / 32.0f;

    private MirrorSurfaceGeometry() {
    }

    /**
     * Builds the four world-local corners of the rendered Mirror face for its horizontal block direction.
     */
    public static Surface front(Direction facing) {
        return new Surface(
            transform(facing, LEFT, 0.0f),
            transform(facing, RIGHT, 0.0f),
            transform(facing, RIGHT, HEIGHT),
            transform(facing, LEFT, HEIGHT)
        );
    }

    /**
     * Applies the Blockbench 7.5-degree lean, then rotates the canonical north-facing surface for the block state.
     */
    private static Vector3f transform(Direction facing, float x, float y) {
        float relativeZ = MirrorSurfaceGeometry.FRONT_DEPTH - PIVOT_Z;
        float leanedY = y * (float) Math.cos(LEAN_RADIANS) - relativeZ * (float) Math.sin(LEAN_RADIANS);
        float leanedZ = PIVOT_Z + y * (float) Math.sin(LEAN_RADIANS)
            + relativeZ * (float) Math.cos(LEAN_RADIANS);
        return switch (facing) {
            case NORTH -> new Vector3f(x, leanedY, leanedZ);
            case EAST -> new Vector3f(1.0f - leanedZ, leanedY, x);
            case SOUTH -> new Vector3f(1.0f - x, leanedY, 1.0f - leanedZ);
            case WEST -> new Vector3f(leanedZ, leanedY, 1.0f - x);
            default -> throw new IllegalArgumentException("Mirror requires a horizontal facing");
        };
    }

    public record Surface(Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft) {

        /**
         * Interpolates a point across the full rendered face and offsets it outward to avoid depth conflicts.
         */
        public Vector3f point(float horizontal, float vertical, float outwardOffset) {
            Vector3f bottom = new Vector3f(bottomLeft).lerp(bottomRight, horizontal);
            Vector3f top = new Vector3f(topLeft).lerp(topRight, horizontal);
            return bottom.lerp(top, vertical).add(outwardNormal().mul(outwardOffset));
        }

        /**
         * Interpolates a point inside the 12-by-30 reflective panel, preserving the model's gold frame border.
         */
        public Vector3f panelPoint(float horizontal, float vertical, float outwardOffset) {
            return point(
                PANEL_LEFT + (PANEL_RIGHT - PANEL_LEFT) * horizontal,
                PANEL_BOTTOM + (PANEL_TOP - PANEL_BOTTOM) * vertical,
                outwardOffset
            );
        }

        /**
         * Returns the face normal pointing away from the Mirror, used for safe overlay depth offsets.
         */
        public Vector3f outwardNormal() {
            Vector3f vertical = new Vector3f(topLeft).sub(bottomLeft);
            Vector3f horizontal = new Vector3f(bottomRight).sub(bottomLeft);
            return vertical.cross(horizontal).normalize();
        }
    }
}