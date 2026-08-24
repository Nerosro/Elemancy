package be.nerosro.elemancy.passives;

import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * Stateless tick-based passives from the Elemancy skill tree.
 * Each passive checks its own unlock condition and applies an effect per-tick or on interval.
 */
public final class TickPassives {
    private TickPassives() {
    }

    private static final float VITAL_CURRENTS_REDUCTION = -0.005f;    // exhaustion offset per tick
    private static final int VITAL_CURRENTS_INTERVAL = 5;             // every 5 ticks (~25% hunger reduction)

    private static final int NIGHT_SIGHT_DURATION_TICKS = 260;        // ~13 seconds
    private static final int NIGHT_SIGHT_REFRESH_THRESHOLD = 200;     // refresh at 10s remaining
    private static final int NIGHT_SIGHT_LIGHT_THRESHOLD = 7;         // only in low light
    private static final int NIGHT_SIGHT_CHECK_INTERVAL = 20;         // check every second

    /**
     * Called every tick from the orchestrator. Each passive handles its own interval throttle.
     */
    public static void tick(Player player) {
        tickVitalCurrents(player);
        tickNightSight(player);
    }

    // ── Vital Currents (Water) — reduce exhaustion gain ─────────────────────

    private static void tickVitalCurrents(Player player) {
        if (player.tickCount % VITAL_CURRENTS_INTERVAL != 0) return;
        if (!SkillTreeUtil.hasNode(player, SkillTreeEntries.VITAL_CURRENTS_ID)) return;

        player.getFoodData().addExhaustion(VITAL_CURRENTS_REDUCTION);
    }

    // ── Night Sight (Dark) — night vision in low light ──────────────────────

    private static void tickNightSight(Player player) {
        if (player.tickCount % NIGHT_SIGHT_CHECK_INTERVAL != 5) return; // offset from attribute check
        if (!SkillTreeUtil.hasNode(player, SkillTreeEntries.NIGHT_SIGHT_ID)) return;

        BlockPos pos = player.blockPosition();
        int lightLevel = player.level().getMaxLocalRawBrightness(pos);

        if (lightLevel <= NIGHT_SIGHT_LIGHT_THRESHOLD) {
            MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < NIGHT_SIGHT_REFRESH_THRESHOLD) {
                player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, NIGHT_SIGHT_DURATION_TICKS, 0, true, false, true));
            }
        } else {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
