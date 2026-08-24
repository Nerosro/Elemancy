package be.nerosro.elemancy.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Generic global loot modifier that adds a configurable item with a configurable chance.
 * When {@code replace} is true, the existing loot is cleared before adding the item (for archaeology).
 */
public class AddItemLootModifier extends LootModifier {

    private final Item item;
    private final float chance;
    private final boolean replace;

    public static final MapCodec<AddItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
        codecStart(inst)
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
            .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
            .and(Codec.BOOL.optionalFieldOf("replace", false).forGetter(m -> m.replace))
            .apply(inst, AddItemLootModifier::new)
    );

    public AddItemLootModifier(LootItemCondition[] conditions, int priority, Item item, float chance, boolean replace) {
        super(conditions, priority);
        this.item = item;
        this.chance = chance;
        this.replace = replace;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < chance) {
            if (replace) {
                generatedLoot.clear();
            }
            generatedLoot.add(new ItemStack(item));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends AddItemLootModifier> codec() {
        return CODEC;
    }
}
