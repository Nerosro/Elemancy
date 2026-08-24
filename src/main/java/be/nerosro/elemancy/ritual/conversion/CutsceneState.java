package be.nerosro.elemancy.ritual.conversion;

import java.util.List;

import be.nerosro.elemancy.entity.RitualSigilEntity;
import be.nerosro.soulmark.element.Element;
import net.minecraft.core.BlockPos;

/**
 * Anchor-scoped state for one active Elemetal Conversion presentation.
 */
final class CutsceneState {
    enum Phase {
        OUTBOUND_BEAMS,
        SOURCE_SIGIL,
        TARGET_SIGIL,
        RETURN_BEAMS
    }

    final BlockPos anchor;
    final int rotation;
    final Element source;
    final Element target;
    final List<BlockPos> capstones;

    RitualSigilEntity sigil;
    Phase phase = Phase.OUTBOUND_BEAMS;
    int phaseTicks;

    CutsceneState(BlockPos anchor, int rotation, Element source, Element target, List<BlockPos> capstones) {
        this.anchor = anchor.immutable();
        this.rotation = rotation;
        this.source = source;
        this.target = target;
        this.capstones = List.copyOf(capstones);
    }
}