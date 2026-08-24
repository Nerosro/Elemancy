package be.nerosro.elemancy.passives;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.block.SoftGlowBlock;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * Soft Glow passive (Light element) — places temporary light blocks at the player's feet in dark areas.
 * Blocks self-remove via scheduled tick (survives server crashes).
 * Particle animation is handled by the block's own animateTick method.
 */
public final class SoftGlowPassive {
    private SoftGlowPassive() {
    }

    private static final int BASE_ACTIVATE_THRESHOLD = 2;
    private static final int ENHANCED_ACTIVATE_THRESHOLD = 4;
    private static final int PLACEMENT_INTERVAL = 20;

    /**
     * Called every tick from the orchestrator. Places a glow block if conditions are met.
     */
    public static void tick(Player player) {
        if (player.level().isClientSide()) return;
        if (player.tickCount % PLACEMENT_INTERVAL != 10) return;
        if (!SkillTreeUtil.hasNode(player, SkillTreeEntries.SOFT_GLOW_ID)) return;

        BlockPos pos = player.blockPosition();
        ServerLevel level = (ServerLevel) player.level();
        int ambientLight = level.getMaxLocalRawBrightness(pos);
        boolean isLightAttuned = AttunementUtil.getAttunement(player) == SoulmarkElements.LIGHT.get();
        int activateThreshold = isLightAttuned ? ENHANCED_ACTIVATE_THRESHOLD : BASE_ACTIVATE_THRESHOLD;

        if (ambientLight <= activateThreshold && level.getBlockState(pos).isAir()) {
            level.setBlock(pos, ElemancyBlocks.SOFT_GLOW.get().defaultBlockState()
                .setValue(SoftGlowBlock.ENHANCED, isLightAttuned), 3);
        }
    }
}
