package be.nerosro.elemancy.datagen;

import java.util.concurrent.CompletableFuture;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

public class ElemancyItemTagsProvider extends ItemTagsProvider {

    public ElemancyItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Elemancy.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ElemancyTags.WANDS)
            .add(ElemancyItems.ENERGIZED_STICK.get())
            .add(ElemancyItems.ASHEN_WAND.get());

        tag(ItemTags.PICKAXES)
            .add(ElemancyItems.INFUSED_PICKAXE.get());

        // Flowers - needed for bee interactions (block placement etc.)
        tag(ItemTags.SMALL_FLOWERS).add(ElemancyBlocks.PARADOX_FLOWER_ITEM.get());
        tag(ItemTags.FLOWERS).add(ElemancyBlocks.PARADOX_FLOWER_ITEM.get());
        // Bee breeding/temptation checks ItemTags.BEE_FOOD, not FLOWERS/SMALL_FLOWERS
        tag(ItemTags.BEE_FOOD).add(ElemancyBlocks.PARADOX_FLOWER_ITEM.get());

        var elemetalBlocksTag = tag(ElemancyTags.ELEMETAL_BLOCKS);
        ElemancyBlocks.getElemetalBlocksByKey().values().forEach(block -> elemetalBlocksTag.add(block.get().asItem()));

        var elemetalIngotsTag = tag(ElemancyTags.ELEMETAL_INGOTS);
        ElemancyItems.getElemetalIngotsByKey().values().forEach(ingot -> elemetalIngotsTag.add(ingot.get()));

        var elemetalIngots = ElemancyItems.getElemetalIngotsByKey();
        tag(ElemancyTags.FIRE_ELEMETAL_INGOTS).add(elemetalIngots.get("fire").get());
        tag(ElemancyTags.WATER_ELEMETAL_INGOTS).add(elemetalIngots.get("water").get());
        tag(ElemancyTags.EARTH_ELEMETAL_INGOTS).add(elemetalIngots.get("earth").get());
        tag(ElemancyTags.AIR_ELEMETAL_INGOTS).add(elemetalIngots.get("air").get());
        tag(ElemancyTags.LIGHT_ELEMETAL_INGOTS).add(elemetalIngots.get("light").get());
        tag(ElemancyTags.DARK_ELEMETAL_INGOTS).add(elemetalIngots.get("dark").get());
    }
}
