package be.nerosro.elemancy.mana.depth;

import be.nerosro.elemancy.mana.CastCostPipeline;
import be.nerosro.elemancy.network.ScarSyncPayload;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.soulmark.mana.ManaUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * Facade for Elemancy's overcasting risk system.
 * Handles cast attempts (cost → spend → depth resolution), scar lifecycle, and collapse.
 */
public final class ManaDepthSystem {
    private static final int CHANNEL_DEPTH_PULSE_TICKS = 30;

    private ManaDepthSystem() {
    }

    /**
     * Full spell cast pipeline entry point with spell context.
     * Applies cost modifiers (scars + affinity), spends mana, and resolves depth/failure consequences.
     */
    public static CastResolution attemptCast(Player player, float baseCost, SpellContext context) {
        return attemptCast(player, baseCost, context, false);
    }

    public static CastResolution attemptChannelingStart(Player player, float baseCost, SpellContext context) {
        return attemptCast(player, baseCost, context, true);
    }

    private static CastResolution attemptCast(Player player, float baseCost, SpellContext context, boolean channelStart) {
        float adjustedCost = CastCostPipeline.resolve(player, baseCost, context);
        DepthTier depthBeforeCast = DepthTier.fromMana(ManaUtil.getMana(player).getCurrentMana());
        if (!ManaUtil.trySpend(player, adjustedCost)) {
            return new CastResolution(false, false, DepthTier.NONE);
        }
        return resolveAfterSpend(player, depthBeforeCast, channelStart);
    }

    public static CastResolution continueChanneling(Player player, float baseCost, SpellContext context, int ticksInDepth) {
        DepthTier depthBeforeSpend = DepthTier.fromMana(ManaUtil.getMana(player).getCurrentMana());
        float adjustedCost = CastCostPipeline.resolve(player, baseCost, context);
        if (!ManaUtil.trySpend(player, adjustedCost)) {
            return new CastResolution(false, false, DepthTier.NONE);
        }

        DepthTier depthAfterSpend = DepthTier.fromMana(ManaUtil.getMana(player).getCurrentMana());
        boolean pulseDue = depthAfterSpend.isOvercast() && (ticksInDepth + 1) % CHANNEL_DEPTH_PULSE_TICKS == 0;
        if (depthAfterSpend == DepthTier.DEPTH_4
            && (depthBeforeSpend != DepthTier.DEPTH_4 || pulseDue)
            && !survivesCollapse(player)) {
            return new CastResolution(false, true, depthAfterSpend);
        }

        if (pulseDue) {
            ManaDepthRules.applyDepthConsequences(player, depthAfterSpend);
        }
        return new CastResolution(player.isAlive(), true, depthAfterSpend);
    }

    /**
     * Evaluates depth tier consequences after mana has already been spent.
     * Fail chance is judged against the depth the player was already in BEFORE this cast, not
     * the depth this cast's own cost pushes them into - a spell shouldn't fail due to a risk
     * that its own cost created while the player still had positive mana beforehand. Collapse
     * checks and scar consequences still use the resulting (post-spend) depth, since those are
     * meant to reflect the player's new state.
     */
    private static CastResolution resolveAfterSpend(Player player, DepthTier depthBeforeCast, boolean channelStart) {
        DepthTier depthAfterCast = DepthTier.fromMana(ManaUtil.getMana(player).getCurrentMana());

        if (depthAfterCast == DepthTier.DEPTH_4 && !survivesCollapse(player)) {
            return new CastResolution(false, true, depthAfterCast);
        }

        float failChance = SpellFailureRules.finalFailChance(player, depthBeforeCast);
        boolean failed = failChance > 0f && player.getRandom().nextFloat() < failChance;

        if (depthAfterCast.isOvercast() && (!channelStart || depthBeforeCast.isOvercast())) {
            ManaDepthRules.applyDepthConsequences(player, depthAfterCast);
        }

        return new CastResolution(!failed && player.isAlive(), true, depthAfterCast);
    }

    private static boolean survivesCollapse(Player player) {
        if (SpellFailureRules.hasOverwrought(player) && player.getRandom().nextFloat() >= 0.50f) {
            return true;
        }
        ScarFacade.applyManaCollapse(player);
        collapsePlayer(player);
        return false;
    }

    public static void tickScars(Player player) {
        ScarFacade.tickScars(player);
    }

    public static CompoundTag copyScarData(Player player) {
        return ScarFacade.copyScarData(player);
    }

    public static CompoundTag copyPersistentScarData(Player player) {
        return ScarFacade.copyPersistentScarData(player);
    }

    public static void loadScarData(Player player, CompoundTag scarData) {
        ScarFacade.loadScarData(player, scarData);
    }

    public static boolean hasManaCollapse(Player player) {
        return ScarFacade.hasManaCollapse(player);
    }

    public static byte buildScarBitfield(Player player) {
        return ScarFacade.buildScarBitfield(player);
    }

    public static ScarSyncPayload buildScarPayload(Player player) {
        return ScarFacade.buildScarPayload(player);
    }

    public static ManaCollapseKnowledge getManaCollapseKnowledge(Player player) {
        return ScarFacade.getManaCollapseKnowledge(player);
    }

    public static boolean diagnoseManaCollapse(Player player) {
        return ScarFacade.diagnoseManaCollapse(player);
    }

    private static void collapsePlayer(Player player) {
        if (!player.isAlive()) return;
        if (player.level() instanceof ServerLevel serverLevel) {
            player.hurtServer(serverLevel, ElemancyDamageTypes.collapse(player), Float.MAX_VALUE);
        }
    }
}
