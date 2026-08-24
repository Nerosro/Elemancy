package be.nerosro.elemancy.ritual.attunement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.nerosro.elemancy.entity.RitualSigilEntity;
import be.nerosro.soulmark.element.Element;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Per-player state for an in-progress Attunement Ritual activation cutscene. Purely a data
 * holder - all behavior lives in {@link CutsceneEngine}.
 */
final class CutsceneState {

    enum Phase {
        STREAMING_PILLARS,
        SIGIL,
        STRIKING_PILLARS,
        PRE_FINALE,
        FINALE_STREAMING,
        FINALE_STRIKING,
        DONE
    }

    final Player player;
    final BlockPos anchor;
    final int rotation;
    final Element element;

    /**
     * Original floor position where successful rituals create the fresh, attuned Soulvial.
     */
    final Vec3 soulvialPosition;

    /**
     * Movement-lock anchor position, re-applied every tick while the cutscene is active.
     */
    final BlockPos lockPosition;

    /**
     * Random order (indices into {@link StructureTemplate#PILLARS}) of pillars.
     */
    final List<Integer> pillarOrder;

    /**
     * Pillars whose capstone has already been swapped to Elemetal - tracked for revert-on-fail.
     */
    final Set<Integer> transformedPillars = new HashSet<>();

    /**
     * Active pentagram visual, removed immediately when this ritual is interrupted.
     */
    RitualSigilEntity sigilEntity;

    /**
     * Whether the standing tile has already been swapped to Elemetal - excluded from re-validation once true.
     */
    boolean standingTileTransformed = false;

    Phase phase = Phase.STREAMING_PILLARS;

    /**
     * Ticks elapsed within the current phase/step - reset whenever the step changes.
     */
    int phaseTimer = 0;

    /**
     * Index into {@link #pillarOrder} of the pillar currently streaming/being struck.
     */
    int pillarProgress = 0;

    /**
     * How many of the finale's 4 rapid strikes on the player have landed so far.
     */
    int finaleStrikeCount = 0;

    CutsceneState(Player player, BlockPos anchor, int rotation, Element element, Vec3 soulvialPosition, BlockPos lockPosition, List<Integer> pillarOrder) {
        this.player = player;
        this.anchor = anchor;
        this.rotation = rotation;
        this.element = element;
        this.soulvialPosition = soulvialPosition;
        this.lockPosition = lockPosition;
        this.pillarOrder = pillarOrder;
    }
}
