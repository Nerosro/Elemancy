package be.nerosro.elemancy.jobpoint;

import be.nerosro.elemancy.skilltree.Attachments;
import net.minecraft.world.entity.player.Player;

/**
 * Public API for Elemancy Job Point grants and spending.
 */
public final class JobPointUtil {

    private JobPointUtil() {
    }

    public static JobPointData getData(Player player) {
        return player.getData(Attachments.JOB_POINTS.get());
    }

    public static void award(Player player, int amount) {
        JobPointData data = getData(player);
        data.award(amount);
        player.setData(Attachments.JOB_POINTS.get(), data);
    }

    public static boolean awardOnce(Player player, String milestoneId, int amount) {
        JobPointData data = getData(player);
        boolean awarded = data.awardOnce(milestoneId, amount);
        if (awarded) {
            player.setData(Attachments.JOB_POINTS.get(), data);
        }
        return awarded;
    }

    public static boolean trySpend(Player player, int cost) {
        JobPointData data = getData(player);
        boolean spent = data.trySpend(cost);
        if (spent) {
            player.setData(Attachments.JOB_POINTS.get(), data);
        }
        return spent;
    }

    public static int getAvailableBalance(Player player) {
        return getData(player).getAvailableBalance();
    }

    public static int getTotalEarned(Player player) {
        return getData(player).getTotalEarned();
    }
}