package be.nerosro.elemancy.ritual.attunement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.jobpoint.MilestoneEvents;
import be.nerosro.elemancy.network.ElemancyNetwork;
import be.nerosro.elemancy.ritual.shared.CutscenePresentation;
import be.nerosro.elemancy.ritual.shared.StructureRotationTemplate;
import be.nerosro.elemancy.ritual.shared.Timings;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.ElementRegistry;
import be.nerosro.soulmark.element.ElementUtil;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Drives the Attunement Ritual's activation cutscene: a tick-based state machine per player
 */
public final class CutsceneEngine {
    private CutsceneEngine() {
    }

    private static final Map<Player, CutsceneState> ACTIVE = new WeakHashMap<>();

    public static boolean isInRitual(Player player) {
        return ACTIVE.containsKey(player);
    }

    /**
     * Begins the cutscene for a player who has triggered the ritual. {@code anchor} is the
     * center block position, {@code rotation} is the detector's matched rotation (0-3). The
     * Soulvial is destroyed by visual lightning here. A successful finale creates a fresh,
     * element-attuned Soulvial at the same floor position.
     */
    public static void start(Player player, BlockPos anchor, int rotation, Element element, ItemEntity soulvialEntity) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (ACTIVE.containsKey(player)) return;

        List<Integer> pillarOrder = new ArrayList<>(List.of(0, 1, 2, 3));
        shuffle(pillarOrder, player.getRandom());

        Vec3 soulvialPosition = soulvialEntity.position();
        CutscenePresentation.strikeVisualOnly(level, soulvialPosition, element);
        soulvialEntity.discard();

        BlockPos lockPosition = player.blockPosition();
        CutsceneState state = new CutsceneState(player, anchor, rotation, element, soulvialPosition, lockPosition, pillarOrder);
        ACTIVE.put(player, state);

        if (player instanceof ServerPlayer serverPlayer) {
            ElemancyNetwork.setRitualCameraLocked(serverPlayer, true);
        }

        player.sendSystemMessage(Component.literal("Worldly magic locks you into place."));
    }

    /**
     * Called every tick for every player (see {@link CutsceneEvents}); no-op if not ritualing.
     */
    public static void tick(Player player) {
        CutsceneState state = ACTIVE.get(player);
        if (state == null) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        applyMovementLock(state);

        if (!structureStillValid(state)) {
            interrupt(player);
            return;
        }

        switch (state.phase) {
            case STREAMING_PILLARS -> tickStreamingPillars(level, state);
            case SIGIL -> tickSigil(state);
            case STRIKING_PILLARS -> tickStrikingPillars(level, state);
            case PRE_FINALE -> tickPreFinale(state);
            case FINALE_STREAMING -> tickFinaleStreaming(level, state);
            case FINALE_STRIKING -> tickFinaleStriking(level, state);
            case DONE -> finish(player);
        }
    }

    /**
     * Interrupts an in-progress ritual: reverts transformed pillars, releases the lock, no refund.
     */
    public static void interrupt(Player player) {
        CutsceneState state = ACTIVE.remove(player);
        if (state == null) return;
        restoreCamera(player);
        if (player.level() instanceof ServerLevel level) {
            CutscenePresentation.discardSigil(state.sigilEntity);
            for (int pillarIndex : state.transformedPillars) {
                BlockPos capstonePos = pillarCapstonePos(state, pillarIndex);
                level.setBlockAndUpdate(capstonePos, ElemancyBlocks.INFUSED_METAL_BLOCK.get().defaultBlockState());
            }
        }
    }

    // ── Phase logic ──────────────────────────────────────────────────────────

    private static void tickStreamingPillars(ServerLevel level, CutsceneState state) {
        int completedCount = state.pillarProgress;
        for (int i = 0; i < completedCount; i++) {
            CutscenePresentation.spawnChargeParticles(level, pillarCapstonePos(state, state.pillarOrder.get(i)));
        }

        if (completedCount >= state.pillarOrder.size()) {
            state.sigilEntity = CutscenePresentation.spawnSigil(level, sigilVertices(state), state.element);
            advancePhase(state, CutsceneState.Phase.SIGIL);
            return;
        }

        int pillarIndex = state.pillarOrder.get(completedCount);
        BlockPos target = pillarCapstonePos(state, pillarIndex);
        if (state.phaseTimer < Timings.STREAM_TRAVEL_TICKS) {
            CutscenePresentation.spawnTravelingBeam(level, state.element, state.anchor, target, state.phaseTimer, Timings.STREAM_TRAVEL_TICKS);
        }
        // else: silent pause between this pillar's stream finishing and the next one starting.

        state.phaseTimer++;
        if (state.phaseTimer >= Timings.STREAM_TRAVEL_TICKS + Timings.STREAM_PAUSE_TICKS) {
            state.pillarProgress++;
            state.phaseTimer = 0;
        }
    }

    private static void tickSigil(CutsceneState state) {
        state.phaseTimer++;
        if (state.phaseTimer >= Timings.SIGIL_HOLD_TICKS) {
            advancePhase(state, CutsceneState.Phase.STRIKING_PILLARS);
        }
    }

    private static void tickStrikingPillars(ServerLevel level, CutsceneState state) {
        int strikeCount = state.pillarProgress;
        for (int i = strikeCount; i < state.pillarOrder.size(); i++) {
            CutscenePresentation.spawnChargeParticles(level, pillarCapstonePos(state, state.pillarOrder.get(i)));
        }

        if (strikeCount >= state.pillarOrder.size()) {
            advancePhase(state, CutsceneState.Phase.PRE_FINALE);
            return;
        }

        state.phaseTimer++;
        if (strikeCount == 0 || state.phaseTimer >= Timings.PILLAR_STRIKE_INTERVAL_TICKS) {
            int pillarIndex = state.pillarOrder.get(strikeCount);
            BlockPos capstonePos = pillarCapstonePos(state, pillarIndex);
            if (strikeCount == 0) {
                CutscenePresentation.fadeSigil(state.sigilEntity);
            }
            CutscenePresentation.strikeVisualOnly(level, capstonePos, state.element);
            level.setBlockAndUpdate(capstonePos, ElemancyBlocks.getElemetalBlock(state.element).get().defaultBlockState());
            state.transformedPillars.add(pillarIndex);
            state.pillarProgress++;
            state.phaseTimer = 0;
        }
    }

    private static void tickPreFinale(CutsceneState state) {
        state.phaseTimer++;
        if (state.phaseTimer >= Timings.PRE_FINALE_DELAY_TICKS) {
            advancePhase(state, CutsceneState.Phase.FINALE_STREAMING);
        }
    }

    private static void tickFinaleStreaming(ServerLevel level, CutsceneState state) {
        BlockPos standingPos = standingTilePos(state);
        for (int pillarIndex : state.pillarOrder) {
            BlockPos from = pillarCapstonePos(state, pillarIndex);
            CutscenePresentation.spawnTravelingBeam(level, state.element, from, standingPos, state.phaseTimer, Timings.STREAM_TRAVEL_TICKS);
        }

        state.phaseTimer++;
        if (state.phaseTimer >= Timings.STREAM_TRAVEL_TICKS) {
            advancePhase(state, CutsceneState.Phase.FINALE_STRIKING);
        }
    }

    private static void tickFinaleStriking(ServerLevel level, CutsceneState state) {
        BlockPos standingPos = standingTilePos(state);
        CutscenePresentation.spawnChargeParticles(level, standingPos);

        if (state.finaleStrikeCount == 0 && state.phaseTimer == 0) {
            CutscenePresentation.strikeVisualOnly(level, state.anchor, state.element);
        }

        state.phaseTimer++;
        if (state.phaseTimer >= Timings.FINALE_STRIKE_INTERVAL_TICKS * state.finaleStrikeCount) {
            CutscenePresentation.strikeVisualOnly(level, standingPos, state.element);
            state.finaleStrikeCount++;

            if (state.finaleStrikeCount >= 4) {
                level.setBlockAndUpdate(standingPos, ElemancyBlocks.getElemetalBlock(state.element).get().defaultBlockState());
                state.standingTileTransformed = true;
                spawnAttunedSoulvial(level, state);
                finalizeAttunement(state);
                advancePhase(state, CutsceneState.Phase.DONE);
            }
        }
    }

    /**
     * Sets the player's attunement to the ritual's resolved element and syncs it to their client.
     */
    private static void finalizeAttunement(CutsceneState state) {
        AttunementUtil.setAttunement(state.player, state.element);
        if (state.player instanceof ServerPlayer serverPlayer) {
            MilestoneEvents.onAttunementCompleted(serverPlayer);
            SoulmarkNetwork.syncAttunement(serverPlayer);
            if (SkillTreeUtil.getTreeData(serverPlayer).unlock(SkillTreeEntries.CONVERSION_RITUAL_ID)) {
                SoulmarkNetwork.syncSkillTree(serverPlayer);
            }
        }
        String elementName = ElementUtil.getDisplayName(ElementRegistry.ELEMENT_REGISTRY.getKey(state.element));
        state.player.sendSystemMessage(Component.literal("You have been attuned to " + elementName + "!"));
    }

    private static void finish(Player player) {
        CutsceneState state = ACTIVE.remove(player);
        if (state != null) {
            CutscenePresentation.discardSigil(state.sigilEntity);
        }
        restoreCamera(player);
    }

    private static void restoreCamera(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ElemancyNetwork.setRitualCameraLocked(serverPlayer, false);
        }
    }

    private static void spawnAttunedSoulvial(ServerLevel level, CutsceneState state) {
        ItemStack soulvialStack = new ItemStack(ElemancyItems.getAttunedSoulvial(state.element).get());
        ItemEntity soulvialEntity = new ItemEntity(
            level,
            state.soulvialPosition.x,
            state.soulvialPosition.y,
            state.soulvialPosition.z,
            soulvialStack);
        soulvialEntity.setNoPickUpDelay();
        level.addFreshEntity(soulvialEntity);
    }

    private static void advancePhase(CutsceneState state, CutsceneState.Phase next) {
        state.phase = next;
        state.phaseTimer = 0;
        state.pillarProgress = 0;
    }

    // ── Movement lock / positions ────────────────────────────────────────────

    private static void applyMovementLock(CutsceneState state) {
        Player player = state.player;
        player.setDeltaMovement(Vec3.ZERO);
        player.teleportTo(
            state.lockPosition.getX() + 0.5,
            state.lockPosition.getY(),
            state.lockPosition.getZ() + 0.5
        );
        player.setSprinting(false);
        player.fallDistance = 0f;
    }

    private static BlockPos pillarCapstonePos(CutsceneState state, int pillarIndex) {
        StructureTemplate.Pillar pillar = StructureTemplate.PILLARS[pillarIndex];
        BlockPos rotated = StructureRotationTemplate.rotateOffset(pillar.dx(), pillar.capstoneDy(), pillar.dz(), state.rotation);
        return state.anchor.offset(rotated);
    }

    private static BlockPos standingTilePos(CutsceneState state) {
        BlockPos rotated = StructureRotationTemplate.rotateOffset(
            StructureTemplate.STANDING_TILE_OFFSET.getX(),
            StructureTemplate.STANDING_TILE_OFFSET.getY(),
            StructureTemplate.STANDING_TILE_OFFSET.getZ(),
            state.rotation);
        return state.anchor.offset(rotated);
    }

    private static List<BlockPos> sigilVertices(CutsceneState state) {
        List<BlockPos> vertices = new ArrayList<>(5);
        for (int pillarIndex = 0; pillarIndex < StructureTemplate.PILLARS.length; pillarIndex++) {
            vertices.add(pillarCapstonePos(state, pillarIndex));
        }
        vertices.add(standingTilePos(state));
        vertices.sort(Comparator.comparingDouble(pos -> Math.atan2(
            pos.getZ() - state.anchor.getZ(), pos.getX() - state.anchor.getX())));

        BlockPos firstActivated = pillarCapstonePos(state, state.pillarOrder.getFirst());
        Collections.rotate(vertices, -vertices.indexOf(firstActivated));
        return vertices;
    }

    // ── Structure re-validation (skips already-transformed pillar capstones) ────

    private static boolean structureStillValid(CutsceneState state) {
        Level level = state.player.level();
        List<StructureRotationTemplate.Entry> entries = StructureTemplate.TEMPLATE.rotated(state.rotation);
        for (StructureRotationTemplate.Entry entry : entries) {
            BlockPos offset = entry.offset();
            if (isTransformedCapstoneOffset(state, offset.getX(), offset.getY(), offset.getZ())) {
                continue;
            }
            if (state.standingTileTransformed && isStandingTileOffset(state, offset.getX(), offset.getY(), offset.getZ())) {
                continue;
            }
            BlockPos pos = state.anchor.offset(offset);
            BlockState blockState = level.getBlockState(pos);
            if (!entry.predicate().test(level, blockState, pos)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStandingTileOffset(CutsceneState state, int dx, int dy, int dz) {
        BlockPos rotated = StructureRotationTemplate.rotateOffset(
            StructureTemplate.STANDING_TILE_OFFSET.getX(),
            StructureTemplate.STANDING_TILE_OFFSET.getY(),
            StructureTemplate.STANDING_TILE_OFFSET.getZ(),
            state.rotation);
        return dy == rotated.getY() && dx == rotated.getX() && dz == rotated.getZ();
    }

    private static boolean isTransformedCapstoneOffset(CutsceneState state, int dx, int dy, int dz) {
        for (int pillarIndex : state.transformedPillars) {
            StructureTemplate.Pillar pillar = StructureTemplate.PILLARS[pillarIndex];
            BlockPos rotated = StructureRotationTemplate.rotateOffset(pillar.dx(), pillar.capstoneDy(), pillar.dz(), state.rotation);
            if (rotated.getX() == dx && rotated.getY() == dy && rotated.getZ() == dz) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fisher-Yates shuffle using Minecraft's {@link net.minecraft.util.RandomSource}.
     */
    private static void shuffle(List<Integer> list, net.minecraft.util.RandomSource random) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(list, i, j);
        }
    }
}
