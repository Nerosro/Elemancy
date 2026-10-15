package be.nerosro.elemancy.mana.depth;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class ScarKnowledgeData implements ValueIOSerializable {

    private boolean scarsRevealed;
    private ManaCollapseKnowledge manaCollapseKnowledge = ManaCollapseKnowledge.NEVER_EXPERIENCED;

    public boolean isScarsRevealed() {
        return scarsRevealed;
    }

    public void revealScars() {
        scarsRevealed = true;
    }

    public ManaCollapseKnowledge getManaCollapseKnowledge() {
        return manaCollapseKnowledge;
    }

    public boolean diagnoseManaCollapse() {
        if (manaCollapseKnowledge != ManaCollapseKnowledge.EXPERIENCED_UNDOCUMENTED) return false;
        manaCollapseKnowledge = ManaCollapseKnowledge.MIRROR_DIAGNOSED;
        return true;
    }

    public void markManaCollapseExperienced() {
        if (manaCollapseKnowledge == ManaCollapseKnowledge.NEVER_EXPERIENCED) {
            manaCollapseKnowledge = ManaCollapseKnowledge.EXPERIENCED_UNDOCUMENTED;
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putBoolean("scarsRevealed", scarsRevealed);
        output.putInt("manaCollapseKnowledge", manaCollapseKnowledge.id());
    }

    @Override
    public void deserialize(ValueInput input) {
        scarsRevealed = input.getBooleanOr("scarsRevealed", false);
        manaCollapseKnowledge = ManaCollapseKnowledge.byId(input.getIntOr("manaCollapseKnowledge", 0));
    }
}