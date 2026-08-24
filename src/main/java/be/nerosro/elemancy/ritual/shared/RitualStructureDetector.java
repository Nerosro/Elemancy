package be.nerosro.elemancy.ritual.shared;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Generic, rotation-aware multiblock structure detector.
 * <p>
 * Given an anchor position and a {@link StructureRotationTemplate}, tries all 4 cardinal
 * rotations (0/90/180/270 degrees) of the template and reports the first one that fully
 * matches the world. Reusable by any ritual needing orientation-independent detection.
 */
public final class RitualStructureDetector {

    private RitualStructureDetector() {
    }

    /**
     * Attempts to match the given template against the world, anchored at {@code anchor},
     * trying all 4 rotations.
     *
     * @return the matched rotation step count (0-3, in 90-degree increments), or empty if no
     * rotation matches.
     */
    public static Optional<Integer> detect(Level level, BlockPos anchor, StructureRotationTemplate template) {
        for (int steps = 0; steps < 4; steps++) {
            if (matchesEntries(level, anchor, template.rotated(steps))) {
                return Optional.of(steps);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks a template against one already-resolved rotation.
     */
    public static boolean matches(Level level, BlockPos anchor, StructureRotationTemplate template, int rotation) {
        return matchesEntries(level, anchor, template.rotated(rotation));
    }

    private static boolean matchesEntries(Level level, BlockPos anchor, List<StructureRotationTemplate.Entry> entries) {
        for (StructureRotationTemplate.Entry entry : entries) {
            BlockPos worldPos = anchor.offset(entry.offset());
            BlockState state = level.getBlockState(worldPos);
            if (!entry.predicate().test(level, state, worldPos)) {
                return false;
            }
        }
        return true;
    }
}
