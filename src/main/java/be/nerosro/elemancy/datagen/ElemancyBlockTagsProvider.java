package be.nerosro.elemancy.datagen;

import java.util.concurrent.CompletableFuture;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ElemancyBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class ElemancyBlockTagsProvider extends BlockTagsProvider {

    public ElemancyBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Elemancy.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Logs
        tag(BlockTags.LOGS_THAT_BURN)
            .add(ElemancyBlocks.ASHEN_LOG.get())
            .add(ElemancyBlocks.ASHEN_WOOD.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_LOG.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_WOOD.get());

        tag(BlockTags.LOGS)
            .add(ElemancyBlocks.ASHEN_LOG.get())
            .add(ElemancyBlocks.ASHEN_WOOD.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_LOG.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_WOOD.get());

        // Planks
        tag(BlockTags.PLANKS).add(ElemancyBlocks.ASHEN_PLANKS.get());

        // Wooden derivatives
        tag(BlockTags.WOODEN_STAIRS).add(ElemancyBlocks.ASHEN_STAIRS.get());
        tag(BlockTags.WOODEN_SLABS).add(ElemancyBlocks.ASHEN_SLAB.get());
        tag(BlockTags.WOODEN_FENCES).add(ElemancyBlocks.ASHEN_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(ElemancyBlocks.ASHEN_FENCE_GATE.get());
        tag(BlockTags.WOODEN_DOORS).add(ElemancyBlocks.ASHEN_DOOR.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(ElemancyBlocks.ASHEN_TRAPDOOR.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ElemancyBlocks.ASHEN_PRESSURE_PLATE.get());
        tag(BlockTags.WOODEN_BUTTONS).add(ElemancyBlocks.ASHEN_BUTTON.get());

        // Leaves
        tag(BlockTags.LEAVES).add(ElemancyBlocks.ASHEN_LEAVES.get());

        // Flowers
        tag(BlockTags.SMALL_FLOWERS).add(ElemancyBlocks.PARADOX_FLOWER.get());
        tag(BlockTags.BEE_ATTRACTIVE).add(ElemancyBlocks.PARADOX_FLOWER.get());

        // Logs prevent leaf decay
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
            .add(ElemancyBlocks.ASHEN_LOG.get())
            .add(ElemancyBlocks.ASHEN_WOOD.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_LOG.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_WOOD.get());

        // Mineable
        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(ElemancyBlocks.ASHEN_LOG.get())
            .add(ElemancyBlocks.ASHEN_WOOD.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_LOG.get())
            .add(ElemancyBlocks.STRIPPED_ASHEN_WOOD.get())
            .add(ElemancyBlocks.ASHEN_PLANKS.get())
            .add(ElemancyBlocks.ASHEN_STAIRS.get())
            .add(ElemancyBlocks.ASHEN_SLAB.get())
            .add(ElemancyBlocks.ASHEN_FENCE.get())
            .add(ElemancyBlocks.ASHEN_FENCE_GATE.get())
            .add(ElemancyBlocks.ASHEN_DOOR.get())
            .add(ElemancyBlocks.ASHEN_TRAPDOOR.get())
            .add(ElemancyBlocks.ASHEN_PRESSURE_PLATE.get())
            .add(ElemancyBlocks.ASHEN_BUTTON.get());

        tag(BlockTags.MINEABLE_WITH_HOE).add(ElemancyBlocks.ASHEN_LEAVES.get());

        // Infused metal
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ElemancyBlocks.INFUSED_METAL_BLOCK.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ElemancyBlocks.INFUSED_METAL_BLOCK.get());

        // Elemetal
        ElemancyBlocks.getElemetalBlocksByKey().values().forEach(block -> {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(block.get());
        });
    }
}
