package be.nerosro.elemancy.items.tome;

import java.util.List;

import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitData;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Writes the player's current trait record into a Tome item stack.
 */
public final class TomeTraitSnapshot {

    private TomeTraitSnapshot() {
    }

    /**
     * Writes traits when their serialized snapshot differs from the Tome.
     * Returns whether the Tome item data changed.
     */
    public static boolean write(ItemStack tome, Player player) {
        TraitData data = TraitUtil.getTraitData(player);
        if (!data.isInitialized()) return false;

        CompoundTag traits = serialize(data);
        CompoundTag root = tome.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (traits.equals(root.getCompound("traits").orElse(null))) return false;

        root.put("traits", traits);
        tome.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return true;
    }

    /**
     * Writes an initial snapshot for a Tome created after traits were discovered.
     */
    public static void writeIfRevealed(ItemStack tome, Player player) {
        if (TraitUtil.isTraitsRevealed(player)) {
            write(tome, player);
        }
    }

    private static CompoundTag serialize(TraitData data) {
        CompoundTag traits = new CompoundTag();
        List<Trait> allTraits = data.getAllTraits();
        for (int index = 0; index < allTraits.size(); index++) {
            Trait trait = allTraits.get(index);
            CompoundTag entry = new CompoundTag();
            entry.putString("name", trait.name());
            entry.putString("description", trait.description());
            entry.putString("type", trait.type().name());
            entry.putString("weight", trait.weight().name());
            entry.putFloat("value", trait.value());
            traits.put("trait_" + index, entry);
        }
        traits.putInt("count", allTraits.size());
        return traits;
    }
}