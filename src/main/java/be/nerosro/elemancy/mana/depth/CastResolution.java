package be.nerosro.elemancy.mana.depth;

/**
 * Result of a cast attempt after mana spend and depth/failure resolution.
 */
public record CastResolution(boolean spellResolved, boolean castConsumed, DepthTier depthTier) {
}

