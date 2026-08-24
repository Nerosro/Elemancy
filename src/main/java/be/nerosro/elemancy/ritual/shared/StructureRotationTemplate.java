package be.nerosro.elemancy.ritual.shared;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A rotatable multiblock structure template: a set of relative offsets, each paired with a
 * predicate that validates the world block at that offset. Rotation is 90-degree steps around
 * the vertical (Y) axis only - never a mirror/reflection.
 * <p>
 * Reusable across any ritual that needs orientation-independent structure detection
 * (e.g. the future mutation ritual), not just the Attunement Ritual.
 */
public final class StructureRotationTemplate {

    private final List<Entry> entries;

    public StructureRotationTemplate(List<Entry> entries) {
        this.entries = List.copyOf(entries);
    }

    /**
     * Returns this template's entries rotated by {@code steps} increments of 90 degrees
     * (clockwise when viewed from above). {@code steps} is normalized into the [0, 3] range.
     */
    public List<Entry> rotated(int steps) {
        List<Entry> current = entries;
        int normalizedSteps = ((steps % 4) + 4) % 4;
        for (int i = 0; i < normalizedSteps; i++) {
            current = rotateOnce(current);
        }
        return current;
    }

    private static List<Entry> rotateOnce(List<Entry> source) {
        List<Entry> rotated = new ArrayList<>(source.size());
        for (Entry entry : source) {
            BlockPos offset = entry.offset();
            rotated.add(new Entry(new BlockPos(-offset.getZ(), offset.getY(), offset.getX()), entry.predicate()));
        }
        return rotated;
    }

    /**
     * Rotates a single raw offset by {@code steps} increments of 90 degrees, using the same
     * convention as {@link #rotated(int)}. Lets callers locate structure-relative positions
     * (e.g. "where should the player stand") once a matched rotation is known, without needing
     * a full template.
     */
    public static BlockPos rotateOffset(int dx, int dy, int dz, int steps) {
        int normalizedSteps = ((steps % 4) + 4) % 4;
        int rotatedDx = dx;
        int rotatedDz = dz;
        for (int i = 0; i < normalizedSteps; i++) {
            int nextDx = -rotatedDz;
            int nextDz = rotatedDx;
            rotatedDx = nextDx;
            rotatedDz = nextDz;
        }
        return new BlockPos(rotatedDx, dy, rotatedDz);
    }

    /**
     * A single structural requirement: a block must exist at the given offset from the
     * template's anchor point, satisfying the given predicate.
     */
    public record Entry(BlockPos offset, BlockPredicate predicate) {
    }

    /**
     * Validates a world block against a structural requirement. Receives the level and world
     * position (not just the {@link BlockState}) so predicates can also check fluid state
     * (e.g. a water source block) rather than being limited to block matching.
     */
    @FunctionalInterface
    public interface BlockPredicate {
        boolean test(Level level, BlockState state, BlockPos worldPos);
    }
}
