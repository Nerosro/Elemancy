package be.nerosro.elemancy.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Global loot modifier that adds +1 raw copper when mining copper ore
 * with the Infused Pickaxe. Conditions (tool match, loot table match)
 * are handled by the JSON configuration.
 */
public class CopperFortuneModifier extends LootModifier {

    public static final MapCodec<CopperFortuneModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
        codecStart(inst).apply(inst, CopperFortuneModifier::new)
    );

    public CopperFortuneModifier(LootItemCondition[] conditions, int priority) {
        super(conditions, priority);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(Items.RAW_COPPER));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends CopperFortuneModifier> codec() {
        return CODEC;
    }
}
