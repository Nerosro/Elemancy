package be.nerosro.elemancy.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Shared cast visual and audio effects reusable across spells.
 */
public final class CastEffects {
    private CastEffects() {
    }

    private static final SoundEvent SOUND_INFUSE = SoundEvent.createVariableRangeEvent(
        Identifier.fromNamespaceAndPath("elemancy", "infuse"));
    private static final SoundEvent SOUND_FIZZLE = SoundEvent.createVariableRangeEvent(
        Identifier.fromNamespaceAndPath("elemancy", "spell_fizzle"));

    /**
     * Plays a particle burst at a target position.
     */
    public static void targetBurst(ServerLevel level, Vec3 pos, int color, int count) {
        DustParticleOptions dust = new DustParticleOptions(color, 1.0f);
        level.sendParticles(dust, pos.x, pos.y + 0.2, pos.z, count, 0.2, 0.2, 0.2, 0.0);
    }

    /**
     * Plays a small particle burst at the caster's offhand position visible to other players.
     */
    public static void casterBurst(ServerLevel level, Player player, int color) {
        DustParticleOptions dust = new DustParticleOptions(color, 1.0f);
        Vec3 side = player.getLookAngle().cross(new Vec3(0.0, 1.0, 0.0)).normalize().scale(-0.25);
        Vec3 handPos = player.getEyePosition().add(side).add(0.0, -0.25, 0.0);
        level.sendParticles(dust, handPos.x, handPos.y, handPos.z, 8, 0.08, 0.08, 0.08, 0.0);
    }

    /**
     * Plays the infusion cast sound at a block position.
     */
    public static void infuseSound(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SOUND_INFUSE, SoundSource.PLAYERS, 0.8f, 1.0f);
    }

    /**
     * Plays a fizzle/failure sound at the player's position when a spell fails to resolve.
     */
    public static void fizzle(ServerLevel level, Player player) {
        level.playSound(null, player.blockPosition(), SOUND_FIZZLE, SoundSource.PLAYERS, 0.6f, 0.8f);
    }
}
