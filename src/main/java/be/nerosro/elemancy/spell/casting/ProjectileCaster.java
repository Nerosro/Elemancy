package be.nerosro.elemancy.spell.casting;

import java.util.Optional;

import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.entity.ManaBlastProjectile;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.spell.SpellCastHandler;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellDamagePipeline;
import be.nerosro.elemancy.spell.data.ProjectileSpellData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * Handles casting all projectile-type attack spells.
 * Each spell is parameterized via ProjectileSpellData at registration time.
 */
public final class ProjectileCaster {
    private ProjectileCaster() {
    }

    /**
     * Returns a SpellCastHandler configured for the given projectile spell data.
     * Used at registration time to bind per-element behavior.
     */
    public static SpellCastHandler projectileHandler(ProjectileSpellData data) {
        return player -> tryCast(player, data);
    }

    private static boolean tryCast(Player player, ProjectileSpellData data) {
        SpellContext context = data.toContext();
        Optional<CastResolution> result = SpellCast.cast(player, data.manaCost(), context);
        if (result.isEmpty()) return false;
        if (!result.get().spellResolved()) return true;

        if (player.level() instanceof ServerLevel serverLevel) {
            float damage = data.baseDamage() * SpellDamagePipeline.resolve(player, context);

            ManaBlastProjectile projectile = new ManaBlastProjectile(serverLevel, player);
            projectile.setDamage(damage);
            projectile.setSpellData(data);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(),
                0.0f, data.speed(), data.inaccuracy());
            serverLevel.addFreshEntity(projectile);

            CastEffects.casterBurst(serverLevel, player, data.particleColor());
        }

        return true;
    }
}

