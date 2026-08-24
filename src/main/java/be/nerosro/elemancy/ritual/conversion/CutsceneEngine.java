package be.nerosro.elemancy.ritual.conversion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.ritual.shared.CutscenePresentation;
import be.nerosro.elemancy.ritual.shared.RitualStructureDetector;
import be.nerosro.elemancy.ritual.shared.Timings;
import be.nerosro.soulmark.element.Element;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Drives anchor-scoped Elemetal Conversion presentations without involving a participant.
 */
public final class CutsceneEngine {
    private static final int SOURCE_SIGIL_HOLD_TICKS = 40;
    public static final int TARGET_SIGIL_HOLD_TICKS = 40;

    private static final Map<ServerLevel, Map<BlockPos, CutsceneState>> ACTIVE = new HashMap<>();

    private CutsceneEngine() {
    }

    public static boolean isActive(ServerLevel level, BlockPos anchor) {
        Map<BlockPos, CutsceneState> rituals = ACTIVE.get(level);
        return rituals != null && rituals.containsKey(anchor);
    }

    public static void start(ServerLevel level, BlockPos anchor, int rotation, Element source, Element target) {
        if (isActive(level, anchor)) return;
        CutsceneState state = new CutsceneState(
            anchor,
            rotation,
            source,
            target,
            StructureTemplate.capstonePositions(anchor, rotation));
        ACTIVE.computeIfAbsent(level, ignored -> new HashMap<>()).put(state.anchor, state);
    }

    /**
     * Ticks every active Conversion Ritual once per server tick.
     */
    public static void tick() {
        Iterator<Map.Entry<ServerLevel, Map<BlockPos, CutsceneState>>> levelIterator = ACTIVE.entrySet().iterator();
        while (levelIterator.hasNext()) {
            Map.Entry<ServerLevel, Map<BlockPos, CutsceneState>> levelEntry = levelIterator.next();
            Iterator<CutsceneState> ritualIterator = levelEntry.getValue().values().iterator();
            //Keep this explicit iterator loop. "removeIf" would hide the lifecycle tick operation inside a predicate, making it harder to read
            while (ritualIterator.hasNext()) {
                CutsceneState state = ritualIterator.next();
                if (!tick(levelEntry.getKey(), state)) {
                    ritualIterator.remove();
                }
            }
            if (levelEntry.getValue().isEmpty()) {
                levelIterator.remove();
            }
        }
    }

    private static boolean tick(ServerLevel level, CutsceneState state) {
        if (!structureStillValid(level, state)) {
            CutscenePresentation.discardSigil(state.sigil);
            return false;
        }

        return switch (state.phase) {
            case OUTBOUND_BEAMS -> tickOutboundBeams(level, state);
            case SOURCE_SIGIL -> tickSourceSigil(state);
            case TARGET_SIGIL -> tickTargetSigil(state);
            case RETURN_BEAMS -> tickReturnBeams(level, state);
        };
    }

    private static boolean tickOutboundBeams(ServerLevel level, CutsceneState state) {
        for (BlockPos capstone : state.capstones) {
            CutscenePresentation.spawnTravelingBeam(
                level, state.source, state.anchor, capstone, state.phaseTicks, Timings.STREAM_TRAVEL_TICKS);
        }
        if (++state.phaseTicks >= Timings.STREAM_TRAVEL_TICKS) {
            state.sigil = CutscenePresentation.spawnSigil(level, state.capstones, state.source);
            advance(state, CutsceneState.Phase.SOURCE_SIGIL);
        }
        return true;
    }

    private static boolean tickSourceSigil(CutsceneState state) {
        if (++state.phaseTicks >= SOURCE_SIGIL_HOLD_TICKS) {
            CutscenePresentation.changeSigilElement(state.sigil, state.target);
            advance(state, CutsceneState.Phase.TARGET_SIGIL);
        }
        return true;
    }

    private static boolean tickTargetSigil(CutsceneState state) {
        if (++state.phaseTicks >= TARGET_SIGIL_HOLD_TICKS) {
            CutscenePresentation.discardSigil(state.sigil);
            state.sigil = null;
            advance(state, CutsceneState.Phase.RETURN_BEAMS);
        }
        return true;
    }

    private static boolean tickReturnBeams(ServerLevel level, CutsceneState state) {
        for (BlockPos capstone : state.capstones) {
            CutscenePresentation.spawnTravelingBeam(
                level, state.target, capstone, state.anchor, state.phaseTicks, Timings.STREAM_TRAVEL_TICKS);
        }
        if (++state.phaseTicks < Timings.STREAM_TRAVEL_TICKS) return true;

        CutscenePresentation.strikeVisualOnly(level, state.anchor, state.target);
        level.setBlockAndUpdate(state.anchor, ElemancyBlocks.getElemetalBlock(state.target).get().defaultBlockState());
        return false;
    }

    private static boolean structureStillValid(ServerLevel level, CutsceneState state) {
        if (!RitualStructureDetector.matches(level, state.anchor, StructureTemplate.TEMPLATE, state.rotation)) {
            return false;
        }
        if (ElemancyBlocks.getElemetalElement(level.getBlockState(state.anchor)).orElse(null) != state.source) {
            return false;
        }
        return StructureTemplate.resolveTargetElement(level, state.anchor, state.rotation).orElse(null) == state.target;
    }

    private static void advance(CutsceneState state, CutsceneState.Phase phase) {
        state.phase = phase;
        state.phaseTicks = 0;
    }
}