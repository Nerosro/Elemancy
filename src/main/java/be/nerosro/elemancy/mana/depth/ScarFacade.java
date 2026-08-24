package be.nerosro.elemancy.mana.depth;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * Scar storage, timers, and scar-side stat modifications.
 */
public final class ScarFacade {
    private ScarFacade() {
    }

    private static final String SCAR_ROOT = "elemancy_scars";
    private static final String EXPERIENCED_COLLAPSE_KEY = "elemancy_experienced_mana_collapse";

    /**
     * Returns true if the player has experienced Mana Collapse at least once.
     * Used to unlock the Mana Collapse entry in the Tome scars section.
     */
    public static boolean hasExperiencedManaCollapse(Player player) {
        return player.getPersistentData().getBoolean(EXPERIENCED_COLLAPSE_KEY).orElse(false);
    }

    /**
     * Marks the player as having experienced Mana Collapse.
     * Called automatically by applyManaCollapse().
     */
    private static void markManaCollapseExperienced(Player player) {
        player.getPersistentData().putBoolean(EXPERIENCED_COLLAPSE_KEY, true);
    }

    public static float applyCastCostModifiers(Player player, float baseCost) {
        float multiplier = hasChannelDisruption(player)
            ? ManaDepthValues.castCostChannelDisruptionMultiplier()
            : 1f;
        return baseCost * multiplier;
    }

    static void tickScars(Player player) {
        CompoundTag scars = getScarTag(player);

        decrementTimer(scars, ScarType.ARCANE_TREMOR.tickKey());
        decrementTimer(scars, ScarType.SPELL_DRIFT.tickKey());
        decrementTimer(scars, ScarType.CHANNEL_DISRUPTION.tickKey());

        decrementTimer(scars, ScarType.MANA_BURN.tickKey());
        if (intValue(scars, ScarType.MANA_BURN.tickKey()) <= 0) {
            scars.putInt(ScarType.MANA_BURN.requiredStackKey(), 0);
        }

        decrementTimer(scars, ScarType.ARCANE_FATIGUE.tickKey());
        if (intValue(scars, ScarType.ARCANE_FATIGUE.tickKey()) <= 0) {
            scars.putInt(ScarType.ARCANE_FATIGUE.requiredStackKey(), 0);
        }

        decrementTimer(scars, ScarType.SPELL_WEAKNESS.tickKey());
        if (intValue(scars, ScarType.SPELL_WEAKNESS.tickKey()) <= 0) {
            scars.putInt(ScarType.SPELL_WEAKNESS.requiredStackKey(), 0);
        }

        decrementTimer(scars, ScarType.MANA_COLLAPSE.tickKey());
    }

    static CompoundTag copyScarData(Player player) {
        return getScarTag(player).copy();
    }

    /**
     * Copies only death-persistent scars (strips transient timers like tremor and drift).
     */
    static CompoundTag copyPersistentScarData(Player player) {
        CompoundTag copy = getScarTag(player).copy();
        copy.putInt(ScarType.ARCANE_TREMOR.tickKey(), 0);
        copy.putInt(ScarType.SPELL_DRIFT.tickKey(), 0);
        return copy;
    }

    static void loadScarData(Player player, CompoundTag scarData) {
        player.getPersistentData().put(SCAR_ROOT, scarData.copy());
    }

    static boolean hasArcaneTremor(Player player) {
        return intValue(getScarTag(player), ScarType.ARCANE_TREMOR.tickKey()) > 0;
    }

    static boolean hasChannelDisruption(Player player) {
        return intValue(getScarTag(player), ScarType.CHANNEL_DISRUPTION.tickKey()) > 0;
    }

    static boolean hasManaCollapse(Player player) {
        return intValue(getScarTag(player), ScarType.MANA_COLLAPSE.tickKey()) > 0;
    }

    /**
     * Builds a byte bitfield representing active scar presence.
     * Bit positions are defined by {@link ScarType} ordinals.
     * <p>
     * NOTE: New scars must be added to {@link ScarType} (at the end to preserve wire order)
     * and checked here. Also add a matching hasX() in ClientScarData.
     * If scars exceed 8, change return type to short/int and update ScarSyncPayload accordingly.
     */
    static byte buildScarBitfield(Player player) {
        CompoundTag scars = getScarTag(player);
        byte flags = 0;
        if (intValue(scars, ScarType.ARCANE_TREMOR.tickKey()) > 0) flags |= (byte) ScarType.ARCANE_TREMOR.bit();
        if (intValue(scars, ScarType.SPELL_DRIFT.tickKey()) > 0) flags |= (byte) ScarType.SPELL_DRIFT.bit();
        if (intValue(scars, ScarType.CHANNEL_DISRUPTION.tickKey()) > 0)
            flags |= (byte) ScarType.CHANNEL_DISRUPTION.bit();
        if (intValue(scars, ScarType.MANA_BURN.requiredStackKey()) > 0) flags |= (byte) ScarType.MANA_BURN.bit();
        if (intValue(scars, ScarType.ARCANE_FATIGUE.requiredStackKey()) > 0)
            flags |= (byte) ScarType.ARCANE_FATIGUE.bit();
        if (intValue(scars, ScarType.SPELL_WEAKNESS.requiredStackKey()) > 0)
            flags |= (byte) ScarType.SPELL_WEAKNESS.bit();
        if (intValue(scars, ScarType.MANA_COLLAPSE.tickKey()) > 0) flags |= (byte) ScarType.MANA_COLLAPSE.bit();
        return flags;
    }

    /**
     * Builds full scar payload with bitfield, stacks, and timers for network sync.
     * Used by ElemancyNetwork.syncScars().
     */
    static be.nerosro.elemancy.network.ScarSyncPayload buildScarPayload(Player player) {
        CompoundTag scars = getScarTag(player);
        byte flags = buildScarBitfield(player);

        return new be.nerosro.elemancy.network.ScarSyncPayload(
            flags,
            (byte) intValue(scars, ScarType.MANA_BURN.requiredStackKey()),
            intValue(scars, ScarType.MANA_BURN.tickKey()),
            (byte) intValue(scars, ScarType.ARCANE_FATIGUE.requiredStackKey()),
            intValue(scars, ScarType.ARCANE_FATIGUE.tickKey()),
            (byte) intValue(scars, ScarType.SPELL_WEAKNESS.requiredStackKey()),
            intValue(scars, ScarType.SPELL_WEAKNESS.tickKey()),
            intValue(scars, ScarType.ARCANE_TREMOR.tickKey()),
            intValue(scars, ScarType.SPELL_DRIFT.tickKey()),
            intValue(scars, ScarType.CHANNEL_DISRUPTION.tickKey()),
            intValue(scars, ScarType.MANA_COLLAPSE.tickKey())
        );
    }

    private record PhysicalScar(Holder<MobEffect> effect, boolean shortOnly) {
    }

    private static final PhysicalScar[] PHYSICAL_SCARS = {
        new PhysicalScar(MobEffects.SLOWNESS, false),
        new PhysicalScar(MobEffects.BLINDNESS, false),
        new PhysicalScar(MobEffects.MINING_FATIGUE, false),
        new PhysicalScar(MobEffects.NAUSEA, false),
        new PhysicalScar(MobEffects.POISON, true),
        new PhysicalScar(MobEffects.WITHER, true),
    };

    // Cumulative weights — intentionally unequal: common effects at 22.5% each, dangerous ones (Poison/Wither) at 5% each.
    private static final float[] PHYSICAL_SCAR_WEIGHTS = {0.225f, 0.45f, 0.675f, 0.90f, 0.95f, 1.0f};

    static void maybeApplyPhysicalScar(Player player, float chance) {
        if (player.getRandom().nextFloat() >= chance) return;

        float roll = player.getRandom().nextFloat();
        PhysicalScar scar = PHYSICAL_SCARS[PHYSICAL_SCARS.length - 1];
        for (int i = 0; i < PHYSICAL_SCAR_WEIGHTS.length; i++) {
            if (roll < PHYSICAL_SCAR_WEIGHTS[i]) {
                scar = PHYSICAL_SCARS[i];
                break;
            }
        }

        int duration = scar.shortOnly
            ? 20 + player.getRandom().nextInt(40)  // always 1-3 seconds
            : randomPhysicalDuration(player);      // variable 1-12 seconds

        player.addEffect(new MobEffectInstance(scar.effect, duration, 0));
    }

    /**
     * Returns a random physical scar duration across three severity bands:
     * Low (1-3s), Medium (4-7s), High (8-12s) with equal chance.
     */
    private static int randomPhysicalDuration(Player player) {
        int band = player.getRandom().nextInt(3);
        return switch (band) {
            case 0 -> 20 + player.getRandom().nextInt(40);    // 1-3 seconds
            case 1 -> 80 + player.getRandom().nextInt(60);    // 4-7 seconds
            default -> 160 + player.getRandom().nextInt(80);   // 8-12 seconds
        };
    }

    static void maybeApplyPhysiologicalScar(Player player, float chance) {
        if (player.getRandom().nextFloat() >= chance) return;

        CompoundTag scars = getScarTag(player);
        int pick = player.getRandom().nextInt(3);
        if (pick == 0) {
            scars.putInt(ScarType.ARCANE_TREMOR.tickKey(), Math.max(intValue(scars, ScarType.ARCANE_TREMOR.tickKey()), ManaDepthValues.scarArcaneTremorDurationTicks()));
        } else if (pick == 1) {
            scars.putInt(ScarType.SPELL_DRIFT.tickKey(), Math.max(intValue(scars, ScarType.SPELL_DRIFT.tickKey()), ManaDepthValues.scarSpellDriftDurationTicks()));
        } else {
            scars.putInt(ScarType.CHANNEL_DISRUPTION.tickKey(), Math.max(intValue(scars, ScarType.CHANNEL_DISRUPTION.tickKey()), ManaDepthValues.scarChannelDisruptionDurationTicks()));
        }
    }

    static void maybeApplyEnergeticScar(Player player, float chance) {
        if (player.getRandom().nextFloat() >= chance) return;

        CompoundTag scars = getScarTag(player);
        int pick = player.getRandom().nextInt(3);
        if (pick == 0) {
            scars.putInt(ScarType.MANA_BURN.requiredStackKey(), Math.min(ScarType.MANA_BURN.maxStacks(), intValue(scars, ScarType.MANA_BURN.requiredStackKey()) + 1));
            scars.putInt(ScarType.MANA_BURN.tickKey(), Math.max(intValue(scars, ScarType.MANA_BURN.tickKey()), ManaDepthValues.scarEnergeticDurationTicks()));
        } else if (pick == 1) {
            scars.putInt(ScarType.ARCANE_FATIGUE.requiredStackKey(), Math.min(ScarType.ARCANE_FATIGUE.maxStacks(), intValue(scars, ScarType.ARCANE_FATIGUE.requiredStackKey()) + 1));
            scars.putInt(ScarType.ARCANE_FATIGUE.tickKey(), Math.max(intValue(scars, ScarType.ARCANE_FATIGUE.tickKey()), ManaDepthValues.scarEnergeticDurationTicks()));
        } else {
            scars.putInt(ScarType.SPELL_WEAKNESS.requiredStackKey(), Math.min(ScarType.SPELL_WEAKNESS.maxStacks(), intValue(scars, ScarType.SPELL_WEAKNESS.requiredStackKey()) + 1));
            scars.putInt(ScarType.SPELL_WEAKNESS.tickKey(), Math.max(intValue(scars, ScarType.SPELL_WEAKNESS.tickKey()), ManaDepthValues.scarEnergeticDurationTicks()));
        }
    }

    static void applyManaCollapse(Player player) {
        CompoundTag scars = getScarTag(player);
        scars.putInt(ScarType.MANA_COLLAPSE.tickKey(), Math.max(intValue(scars, ScarType.MANA_COLLAPSE.tickKey()), ManaDepthValues.scarManaCollapseDurationTicks()));
        markManaCollapseExperienced(player);
    }

    /**
     * Returns the pool multiplier from active scars (Mana Burn).
     * 1.0 = no penalty. Minimum 0.50.
     */
    public static float getPoolMultiplier(Player player) {
        return ScarType.MANA_BURN.computeMultiplier(getScarTag(player));
    }

    /**
     * Returns the regen multiplier from active scars (Arcane Fatigue, Mana Collapse).
     * 1.0 = no penalty. 0.0 = full suppression (collapse).
     */
    public static float getRegenMultiplier(Player player) {
        CompoundTag scars = getScarTag(player);
        if (intValue(scars, ScarType.MANA_COLLAPSE.tickKey()) > 0) return 0f;
        return ScarType.ARCANE_FATIGUE.computeMultiplier(scars);
    }

    /**
     * Returns the damage multiplier from active scars (Spell Weakness).
     * 1.0 = no penalty. Minimum 0.75 (-5% per stack, max 5 stacks).
     */
    public static float getDamageMultiplier(Player player) {
        return ScarType.SPELL_WEAKNESS.computeMultiplier(getScarTag(player));
    }

    private static CompoundTag getScarTag(Player player) {
        CompoundTag persistent = player.getPersistentData();
        Optional<CompoundTag> existing = persistent.getCompound(SCAR_ROOT);
        if (existing.isPresent()) {
            return existing.get();
        }

        CompoundTag created = new CompoundTag();
        persistent.put(SCAR_ROOT, created);
        return created;
    }

    private static void decrementTimer(CompoundTag tag, String key) {
        int current = intValue(tag, key);
        if (current > 0) {
            tag.putInt(key, current - 1);
        }
    }

    private static int intValue(CompoundTag tag, String key) {
        return tag.getInt(key).orElse(0);
    }
}



