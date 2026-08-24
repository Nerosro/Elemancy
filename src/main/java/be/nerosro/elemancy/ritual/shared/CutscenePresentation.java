package be.nerosro.elemancy.ritual.shared;

import java.util.List;

import be.nerosro.elemancy.entity.RitualLightningEntity;
import be.nerosro.elemancy.entity.RitualSigilEntity;
import be.nerosro.soulmark.element.Element;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * Shared visual operations for ritual cutscenes.
 */
public final class CutscenePresentation {
    private CutscenePresentation() {
    }

    public static void spawnTravelingBeam(ServerLevel level, Element element, BlockPos from, BlockPos to, int elapsedTicks, int totalTicks) {
        double headFraction = Math.min(1.0, elapsedTicks / (double) totalTicks);
        Vec3 fromCenter = Vec3.atCenterOf(from);
        Vec3 toCenter = Vec3.atCenterOf(to);
        DustParticleOptions dust = new DustParticleOptions(element.rgb(), 2.0f);
        Vec3 position = fromCenter.lerp(toCenter, headFraction);
        level.sendParticles(dust, position.x, position.y, position.z, 6, 0.1, 0.1, 0.1, 0.0);
    }

    public static void spawnChargeParticles(ServerLevel level, BlockPos position) {
        Vec3 above = Vec3.atCenterOf(position).add(0, 0.65, 0);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, above.x, above.y, above.z, 6, 0.25, 0.15, 0.25, 0.02);
    }

    public static RitualSigilEntity spawnSigil(ServerLevel level, List<BlockPos> vertices, Element element) {
        return RitualSigilEntity.spawn(level, vertices, element);
    }

    public static void changeSigilElement(RitualSigilEntity sigil, Element element) {
        if (sigil != null) {
            sigil.changeElement(element);
        }
    }

    public static void fadeSigil(RitualSigilEntity sigil) {
        if (sigil != null) {
            sigil.startFadeOut();
        }
    }

    public static void discardSigil(RitualSigilEntity sigil) {
        if (sigil != null) {
            sigil.discard();
        }
    }

    public static void strikeVisualOnly(ServerLevel level, BlockPos position, Element element) {
        strikeVisualOnly(level, Vec3.atCenterOf(position), element);
    }

    public static void strikeVisualOnly(ServerLevel level, Vec3 position, Element element) {
        RitualLightningEntity.spawn(level, position, element);
    }
}