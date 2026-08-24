package be.nerosro.elemancy.skilltree;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

/**
 * Public API for querying and setting the player's equipped spell.
 */
public final class EquippedSpellUtil {
    private EquippedSpellUtil() {
    }

    @Nullable
    public static Identifier getEquippedSpell(Player player) {
        return player.getData(Attachments.EQUIPPED_SPELL).getEquippedSpellId();
    }

    public static void setEquippedSpell(Player player, @Nullable Identifier spellId) {
        player.getData(Attachments.EQUIPPED_SPELL).setEquippedSpellId(spellId);
    }

    public static boolean hasEquippedSpell(Player player) {
        return player.getData(Attachments.EQUIPPED_SPELL).hasEquippedSpell();
    }

    /**
     * Returns true if the currently equipped spell matches the given node ID.
     */
    public static boolean isEquipped(Player player, Identifier nodeId) {
        Identifier equipped = getEquippedSpell(player);
        return equipped != null && equipped.equals(nodeId);
    }
}

