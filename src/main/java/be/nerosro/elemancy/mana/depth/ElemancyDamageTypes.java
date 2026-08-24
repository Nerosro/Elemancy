package be.nerosro.elemancy.mana.depth;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Custom damage types for Elemancy's depth system.
 */
public final class ElemancyDamageTypes {
    private ElemancyDamageTypes() {
    }

    public static final ResourceKey<DamageType> MANA_BACKLASH = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "mana_backlash")
    );

    public static final ResourceKey<DamageType> MANA_COLLAPSE = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "mana_collapse")
    );

    /**
     * Creates a DamageSource for mana backlash (Depth 1-3 overcasting damage).
     */
    public static DamageSource backlash(Player player) {
        Level level = player.level();
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(MANA_BACKLASH));
    }

    /**
     * Creates a DamageSource for mana collapse (Depth 4 instant death).
     */
    public static DamageSource collapse(Player player) {
        Level level = player.level();
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(MANA_COLLAPSE));
    }
}


