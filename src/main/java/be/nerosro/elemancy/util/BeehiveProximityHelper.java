package be.nerosro.elemancy.util;

import be.nerosro.elemancy.block.ParadoxFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Utility for detecting beehives near Paradox Flowers.
 * Used for particle effects, discovery checks, and drop replacement mechanics.
 */
public final class BeehiveProximityHelper {
    private BeehiveProximityHelper() {
    }

    /**
     * Check if a position is within range of any Paradox Flower.
     *
     * @param level  The level to check in
     * @param center The center position to check from
     * @param hRange Horizontal range (X/Z directions)
     * @param vRange Vertical range (Y direction)
     * @return true if at least one Paradox Flower is within range
     */
    public static boolean isNearParadoxFlower(Level level, BlockPos center, int hRange, int vRange) {
        for (BlockPos pos : BlockPos.betweenClosed(
            center.offset(-hRange, -vRange, -hRange),
            center.offset(hRange, vRange, hRange))) {
            if (level.getBlockState(pos).getBlock() instanceof ParadoxFlowerBlock) {
                return true;
            }
        }
        return false;
    }

}
