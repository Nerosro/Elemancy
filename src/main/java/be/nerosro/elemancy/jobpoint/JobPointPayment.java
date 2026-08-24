package be.nerosro.elemancy.jobpoint;

import be.nerosro.elemancy.network.ClientJobPointData;
import be.nerosro.elemancy.network.ElemancyNetwork;
import be.nerosro.soulmark.skilltree.SkillTreePayment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Default skill-tree payment implementation for Elemancy nodes.
 */
public enum JobPointPayment implements SkillTreePayment {
    INSTANCE;

    @Override
    public boolean trySpend(Player player, int cost) {
        boolean spent = JobPointUtil.trySpend(player, cost);
        if (spent && player instanceof ServerPlayer serverPlayer) {
            ElemancyNetwork.syncJobPoints(serverPlayer);
        }
        return spent;
    }

    @Override
    public int getClientAvailableBalance() {
        return ClientJobPointData.getAvailableBalance();
    }

    @Override
    public String displayName() {
        return "Elemancy Job Points";
    }
}