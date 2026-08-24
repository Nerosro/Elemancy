package be.nerosro.elemancy.spell.casting;

import be.nerosro.elemancy.spell.SpellCastHandler;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.elemancy.spell.SpellShape;
import be.nerosro.elemancy.spell.data.ContinuousSpellData;
import be.nerosro.elemancy.spell.data.ElementSpellDefaults;
import be.nerosro.elemancy.traits.TraitNames;
import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.world.entity.player.Player;

/**
 * Resolves the Spellwarp trait effect: randomizes the delivery shape of attack spells.
 * When active, the player's spell fires as a random shape (projectile, beam, or continuous)
 * regardless of the spell's default shape, using element-appropriate defaults.
 * <p>
 * The rolled shape is cached per-player for one game tick to ensure both SpellDispatcher
 * and WandItem see the same result on the same right-click event.
 */
public final class SpellwarpResolver {
    private SpellwarpResolver() {
    }

    /**
     * The shapes Spellwarp can roll between.
     */
    private static final SpellShape[] ATTACK_SHAPES = {
        SpellShape.PROJECTILE,
        SpellShape.BEAM,
        SpellShape.CONTINUOUS
    };

    /**
     * Cached roll result per player — ensures same shape within a single tick.
     */
    private record CachedRoll(long tick, int shapeIndex) {
    }

    private static final java.util.WeakHashMap<Player, CachedRoll> ROLL_CACHE = new java.util.WeakHashMap<>();

    /**
     * Checks if the player has the Spellwarp trait.
     */
    public static boolean hasSpellwarp(Player player) {
        Trait neutral = TraitUtil.getNeutralTrait(player);
        return neutral != null && neutral.name().equalsIgnoreCase(TraitNames.SPELLWARP);
    }

    /**
     * Rolls a random attack shape for this cast.
     * The result is cached for the current game tick so both SpellDispatcher
     * and WandItem see the same shape on the same right-click.
     */
    public static SpellShape rollShape(Player player) {
        long currentTick = player.level().getGameTime();
        CachedRoll cached = ROLL_CACHE.get(player);

        if (cached != null && cached.tick() == currentTick) {
            return ATTACK_SHAPES[cached.shapeIndex()];
        }

        int index = player.getRandom().nextInt(ATTACK_SHAPES.length);
        ROLL_CACHE.put(player, new CachedRoll(currentTick, index));
        return ATTACK_SHAPES[index];
    }

    /**
     * Returns a one-shot handler for the given element and shape.
     * Used when Spellwarp rolls PROJECTILE or BEAM for any spell.
     * Returns null if the shape is CONTINUOUS (handled by WandItem instead).
     */
    public static SpellCastHandler getHandlerForShape(SpellShape shape, SpellElement element) {
        return switch (shape) {
            case PROJECTILE -> ProjectileCaster.projectileHandler(ElementSpellDefaults.projectile(element));
            case BEAM -> BeamCaster.beamHandler(ElementSpellDefaults.beam(element));
            default -> null;
        };
    }

    /**
     * Returns the ContinuousSpellData for the given element.
     * Used when Spellwarp rolls CONTINUOUS for a normally one-shot spell.
     */
    public static ContinuousSpellData getContinuousData(SpellElement element) {
        return ElementSpellDefaults.continuous(element);
    }
}
