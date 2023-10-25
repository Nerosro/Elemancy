package io.github.nerosro.elemancy.datagen;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Nerosro on 8/10/2023.
 */
public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Elemancy.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Blocks.EXAMPLE_TAG);
                //.add(ModBlocks.INFUSED_WOOL.get()).addTag(Tags.Blocks.);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE);

        this.tag(BlockTags.NEEDS_STONE_TOOL);

        this.tag(BlockTags.NEEDS_IRON_TOOL);

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL);

        this.tag(BlockTags.FENCES)
                .add(ModBlocks.ASHEN_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.ASHEN_FENCE_GATE.get());
    }
}
