package be.nerosro.elemancy.skilltree;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * Tracks the player's currently equipped spell (selected via the radial menu).
 * Stored as a NeoForge data attachment on the player.
 * Only one spell can be equipped at a time.
 */
public class EquippedSpellData implements ValueIOSerializable {

    private static final String TAG_SPELL_ID = "equipped_spell";

    @Nullable
    private Identifier equippedSpellId;

    public EquippedSpellData() {
        this.equippedSpellId = null;
    }

    @Nullable
    public Identifier getEquippedSpellId() {
        return equippedSpellId;
    }

    public void setEquippedSpellId(@Nullable Identifier spellId) {
        this.equippedSpellId = spellId;
    }

    public boolean hasEquippedSpell() {
        return equippedSpellId != null;
    }

    @Override
    public void serialize(ValueOutput output) {
        if (equippedSpellId != null) {
            output.putString(TAG_SPELL_ID, equippedSpellId.toString());
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        input.getString(TAG_SPELL_ID).ifPresent(id -> this.equippedSpellId = Identifier.parse(id));
    }
}
