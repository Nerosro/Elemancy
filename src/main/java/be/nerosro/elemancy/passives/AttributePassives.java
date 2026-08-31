package be.nerosro.elemancy.passives;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

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

    private static final Identifier EARTHEN_POISE_ARMOR_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "earthen_poise_armor");

    private static final Identifier BREEZE_TREAD_FALL_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "breeze_tread_fall");

    private static final Identifier BREEZE_TREAD_SPEED_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "breeze_tread_speed");

    // ── Tuning constants ────────────────────────────────────────────────────

    private static final double SMOLDERING_DAMAGE_BONUS = 0.10;       // +10% attack damage
    private static final double EARTHEN_KNOCKBACK_RESIST = 0.20;      // +20% knockback resistance
    private static final double EARTHEN_ARMOR_BONUS = 1.0;            // +1 armor
    private static final double BREEZE_FALL_REDUCTION = -0.30;        // -30% fall damage
    private static final double BREEZE_SPEED_BONUS = 0.05;            // +5% movement speed
    private static final double BREEZE_FALL_REDUCTION_ATTUNED = -0.50;
    private static final double BREEZE_SPEED_BONUS_ATTUNED = 0.10;

    // ── Sync ────────────────────────────────────────────────────────────────

    /**
     * Applies or removes attribute modifiers based on current skill node unlocks.
     * Called once per second from the orchestrator.
     */
    public static void sync(Player player) {
        boolean hasBreezeTread = SkillTreeUtil.hasNode(player, SkillTreeEntries.BREEZE_TREAD_ID);
        boolean hasEarthenPoise = SkillTreeUtil.hasNode(player, SkillTreeEntries.EARTHEN_POISE_ID);

        syncModifier(player, Attributes.ATTACK_DAMAGE, SMOLDERING_POWER_DAMAGE_ID,
            SMOLDERING_DAMAGE_BONUS, null,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            SkillTreeUtil.hasNode(player, SkillTreeEntries.SMOLDERING_POWER_ID), false);

        syncModifier(player, Attributes.KNOCKBACK_RESISTANCE, EARTHEN_POISE_KNOCKBACK_ID,
            EARTHEN_KNOCKBACK_RESIST, null,
            AttributeModifier.Operation.ADD_VALUE,
            hasEarthenPoise, false);

        syncModifier(player, Attributes.ARMOR, EARTHEN_POISE_ARMOR_ID,
            0.0, EARTHEN_ARMOR_BONUS, 
            AttributeModifier.Operation.ADD_VALUE,hasEarthenPoise,
            AttunementUtil.isAttunedTo(player, SoulmarkElements.EARTH.get()));

        syncModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, BREEZE_TREAD_FALL_ID,
            BREEZE_FALL_REDUCTION, BREEZE_FALL_REDUCTION_ATTUNED,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, hasBreezeTread,
            AttunementUtil.isAttunedTo(player, SoulmarkElements.AIR.get()));

        syncModifier(player, Attributes.MOVEMENT_SPEED, BREEZE_TREAD_SPEED_ID,
            BREEZE_SPEED_BONUS, BREEZE_SPEED_BONUS_ATTUNED,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, hasBreezeTread,
            AttunementUtil.isAttunedTo(player, SoulmarkElements.AIR.get()));
    }

    // ── Dev reset ───────────────────────────────────────────────────────────

    /**
     * Removes all attribute modifiers managed by this class. Used for dev reset.
     */
    public static void removeAll(Player player) {
        removeModifier(player, Attributes.ATTACK_DAMAGE, SMOLDERING_POWER_DAMAGE_ID);
        removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, EARTHEN_POISE_KNOCKBACK_ID);
        removeModifier(player, Attributes.ARMOR, EARTHEN_POISE_ARMOR_ID);
        removeModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, BREEZE_TREAD_FALL_ID);
        removeModifier(player, Attributes.MOVEMENT_SPEED, BREEZE_TREAD_SPEED_ID);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private static void syncModifier(Player player, Holder<Attribute> attribute,
                                     Identifier modifierId, double baseAmount, @Nullable Double attunedAmount,
                                     AttributeModifier.Operation operation, boolean shouldBeActive,
                                     boolean isAttuned) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        AttributeModifier currentModifier = instance.getModifier(modifierId);
        double amount = (isAttuned && attunedAmount != null) ? attunedAmount : baseAmount;

        if (shouldBeActive && (currentModifier == null || currentModifier.amount() != amount)) {
            if (currentModifier != null) {
                instance.removeModifier(modifierId);
            }
            instance.addPermanentModifier(new AttributeModifier(modifierId, amount, operation));
        } else if (!shouldBeActive && currentModifier != null) {
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
