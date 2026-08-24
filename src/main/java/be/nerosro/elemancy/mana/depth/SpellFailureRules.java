package be.nerosro.elemancy.mana.depth;

import java.util.List;

import be.nerosro.elemancy.traits.TraitNames;
import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/**
 * Spell failure chance rules and trait modifiers.
 */
final class SpellFailureRules {
    private SpellFailureRules() {
    }

    static float finalFailChance(Player player, DepthTier depth) {
        if (!depth.isOvercast()) {
            return hasNullspark(player) ? ManaDepthValues.failNullspark() : 0f;
        }

        float failChance = getDepthFailChance(depth);
        if (hasOverwrought(player)) {
            failChance *= 0.5f;
        }

        failChance += ScarFacade.hasArcaneTremor(player) ? 0.10f : 0f;
        return Mth.clamp(failChance, 0f, ManaDepthValues.failMaxCap());
    }

    static boolean hasOverwrought(Player player) {
        return hasTraitByName(TraitUtil.getBoostTraits(player), TraitNames.OVERWROUGHT);
    }

    private static boolean hasNullspark(Player player) {
        return hasTraitByName(TraitUtil.getPenaltyTraits(player), TraitNames.NULLSPARK);
    }

    private static float getDepthFailChance(DepthTier depth) {
        return switch (depth) {
            case DEPTH_1 -> ManaDepthValues.failDepth1();
            case DEPTH_2 -> ManaDepthValues.failDepth2();
            case DEPTH_3 -> ManaDepthValues.failDepth3();
            case DEPTH_4 -> ManaDepthValues.failDepth4();
            case NONE -> 0f;
        };
    }

    private static boolean hasTraitByName(List<Trait> traits, String expectedName) {
        for (Trait trait : traits) {
            if (trait.name().equalsIgnoreCase(expectedName)) {
                return true;
            }
        }
        return false;
    }
}

