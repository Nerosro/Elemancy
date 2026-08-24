package be.nerosro.elemancy.jobpoint;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * Stores Elemancy-owned Job Points and one-time milestone claims.
 */
public class JobPointData implements ValueIOSerializable {

    private int availableBalance;
    private int totalEarned;
    private final Set<String> claimedMilestones = new HashSet<>();

    public void award(int amount) {
        if (amount <= 0) return;
        availableBalance += amount;
        totalEarned += amount;
    }

    public boolean awardOnce(String milestoneId, int amount) {
        if (!claimedMilestones.add(milestoneId)) return false;
        award(amount);
        return true;
    }

    public boolean trySpend(int cost) {
        if (cost <= 0 || availableBalance < cost) return false;
        availableBalance -= cost;
        return true;
    }

    public int getAvailableBalance() {
        return availableBalance;
    }

    public int getTotalEarned() {
        return totalEarned;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("availableBalance", availableBalance);
        output.putInt("totalEarned", totalEarned);
        output.putInt("claimedMilestoneCount", claimedMilestones.size());

        int index = 0;
        for (String milestoneId : claimedMilestones) {
            output.putString("claimedMilestone_" + index, milestoneId);
            index++;
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        availableBalance = input.getIntOr("availableBalance", 0);
        totalEarned = input.getIntOr("totalEarned", 0);
        claimedMilestones.clear();

        int milestoneCount = input.getIntOr("claimedMilestoneCount", 0);
        for (int index = 0; index < milestoneCount; index++) {
            String milestoneId = input.getStringOr("claimedMilestone_" + index, "");
            if (!milestoneId.isEmpty()) {
                claimedMilestones.add(milestoneId);
            }
        }
    }
}