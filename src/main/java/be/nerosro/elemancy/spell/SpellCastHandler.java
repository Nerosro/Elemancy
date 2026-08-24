package be.nerosro.elemancy.spell;

import net.minecraft.world.entity.player.Player;

/**
 * Functional interface for one-shot spell cast handlers.
 * Returns true if the cast was consumed (even on fizzle).
 */
@FunctionalInterface
public interface SpellCastHandler {
    boolean tryCast(Player player);
}
