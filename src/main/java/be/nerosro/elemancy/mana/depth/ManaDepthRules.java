package be.nerosro.elemancy.mana.depth;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * Depth tier classification and per-tier consequences.
 */
final class ManaDepthRules {
    private ManaDepthRules() {
    }

    static DepthTier fromMana(float currentMana) {
        if (currentMana >= 0f) return DepthTier.NONE;
        if (currentMana >= ManaDepthValues.depth1MinMana()) return DepthTier.DEPTH_1;
        if (currentMana >= ManaDepthValues.depth2MinMana()) return DepthTier.DEPTH_2;
        if (currentMana >= ManaDepthValues.depth3MinMana()) return DepthTier.DEPTH_3;
        return DepthTier.DEPTH_4;
    }

    static void applyDepthConsequences(Player player, DepthTier depth) {
        if (player.level().isClientSide()) return;

        switch (depth) {
            case DEPTH_1 -> {
                damagePlayer(player, 1.0f);
                ScarFacade.maybeApplyPhysicalScar(player, 0.35f);
            }
            case DEPTH_2 -> {
                damagePlayer(player, player.getMaxHealth() * 0.20f);
                ScarFacade.maybeApplyPhysicalScar(player, 0.50f);
                ScarFacade.maybeApplyPhysiologicalScar(player, 0.35f);
            }
            case DEPTH_3 -> {
                damagePlayer(player, player.getMaxHealth() * 0.30f);
                ScarFacade.maybeApplyEnergeticScar(player, 0.55f);
                if (player.getRandom().nextFloat() < 0.015f) {
                    ScarFacade.applyManaCollapse(player);
                }
            }
            // Reaching here means an Overwrought survival check succeeded.
            // Depth 4 never applies direct damage: outcome is collapse death or survival check.
            case DEPTH_4 -> ScarFacade.maybeApplyEnergeticScar(player, 0.75f);
            case NONE -> {
                // no-op
            }
        }
    }

    private static void damagePlayer(Player player, float amount) {
        if (amount <= 0f || !player.isAlive()) return;
        if (player.level() instanceof ServerLevel serverLevel) {
            player.hurtServer(serverLevel, ElemancyDamageTypes.backlash(player), amount);
        }
    }

}


