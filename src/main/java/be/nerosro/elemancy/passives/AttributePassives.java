package be.nerosro.elemancy.passives;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * Declarative table of attribute-modifier passives from the Elemancy skill tree.
 * Each passive maps a skill node unlock to a persistent attribute modifier.
 * Adding a new attribute passive = one constant + one line in {@link #sync}.
 */
public final class AttributePassives {
    private AttributePassives() {
    }

    // ── Modifier IDs ────────────────────────────────────────────────────────

    private static final Identifier SMOLDERING_POWER_DAMAGE_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "smoldering_power_damage");

    private static final Identifier EARTHEN_POISE_KNOCKBACK_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "earthen_poise_knockback");

    private static final Identifier BREEZE_TREAD_FALL_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "breeze_tread_fall");

    private static final Identifier BREEZE_TREAD_SPEED_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "breeze_tread_speed");

    // ── Tuning constants ────────────────────────────────────────────────────

    private static final double SMOLDERING_DAMAGE_BONUS = 0.10;       // +10% attack damage
    private static final double EARTHEN_KNOCKBACK_RESIST = 0.20;      // +20% knockback resistance
    private static final double BREEZE_FALL_REDUCTION = -0.30;        // -30% fall damage
    private static final double BREEZE_SPEED_BONUS = 0.05;            // +5% movement speed

    // ── Sync ────────────────────────────────────────────────────────────────

    /**
     * Applies or removes attribute modifiers based on current skill node unlocks.
     * Called once per second from the orchestrator.
     */
    public static void sync(Player player) {
        syncModifier(player, Attributes.ATTACK_DAMAGE, SMOLDERING_POWER_DAMAGE_ID,
            SMOLDERING_DAMAGE_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            SkillTreeUtil.hasNode(player, SkillTreeEntries.SMOLDERING_POWER_ID));

        syncModifier(player, Attributes.KNOCKBACK_RESISTANCE, EARTHEN_POISE_KNOCKBACK_ID,
            EARTHEN_KNOCKBACK_RESIST, AttributeModifier.Operation.ADD_VALUE,
            SkillTreeUtil.hasNode(player, SkillTreeEntries.EARTHEN_POISE_ID));

        syncModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, BREEZE_TREAD_FALL_ID,
            BREEZE_FALL_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            SkillTreeUtil.hasNode(player, SkillTreeEntries.BREEZE_TREAD_ID));

        syncModifier(player, Attributes.MOVEMENT_SPEED, BREEZE_TREAD_SPEED_ID,
            BREEZE_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            SkillTreeUtil.hasNode(player, SkillTreeEntries.BREEZE_TREAD_ID));
    }

    // ── Dev reset ───────────────────────────────────────────────────────────

    /**
     * Removes all attribute modifiers managed by this class. Used for dev reset.
     */
    public static void removeAll(Player player) {
        removeModifier(player, Attributes.ATTACK_DAMAGE, SMOLDERING_POWER_DAMAGE_ID);
        removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, EARTHEN_POISE_KNOCKBACK_ID);
        removeModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, BREEZE_TREAD_FALL_ID);
        removeModifier(player, Attributes.MOVEMENT_SPEED, BREEZE_TREAD_SPEED_ID);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private static void syncModifier(Player player, Holder<Attribute> attribute,
                                     Identifier modifierId, double amount,
                                     AttributeModifier.Operation operation, boolean shouldBeActive) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        boolean hasModifier = instance.getModifier(modifierId) != null;

        if (shouldBeActive && !hasModifier) {
            instance.addPermanentModifier(new AttributeModifier(modifierId, amount, operation));
        } else if (!shouldBeActive && hasModifier) {
            instance.removeModifier(modifierId);
        }
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, Identifier modifierId) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }
}
