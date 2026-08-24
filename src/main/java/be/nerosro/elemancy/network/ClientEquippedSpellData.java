package be.nerosro.elemancy.network;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;

/**
 * Client-side cache for the currently equipped spell.
 * Updated when the player makes a selection in the radial menu.
 * This is a local-only cache — not synced from server.
 * The client trusts its own selection since the server validates on use.
 */
public final class ClientEquippedSpellData {
    private ClientEquippedSpellData() {
    }

    @Nullable
    private static Identifier equippedSpellId;

    public static void set(@Nullable Identifier spellId) {
        equippedSpellId = spellId;
    }

    @Nullable
    public static Identifier get() {
        return equippedSpellId;
    }

    public static boolean isEquipped(Identifier nodeId) {
        return nodeId.equals(equippedSpellId);
    }

    public static void clear() {
        equippedSpellId = null;
    }
}

