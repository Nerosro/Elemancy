package io.github.nerosro.elemancy.datagen.loot;

import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.block.custom.StrawberryCropBlock;
import io.github.nerosro.elemancy.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

/**
 * Created by Nerosro on 8/10/2023.
 */
public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.ASHEN_LOG.get());
        this.dropSelf(ModBlocks.ASHEN_WOOD.get());
        //this.dropSelf(ModBlocks.STRIPPED_ASHEN_LOG.get());
        //this.dropSelf(ModBlocks.STRIPPED_ASHEN_WOOD.get());

        this.dropSelf(ModBlocks.ASHEN_STAIRS.get());
        this.dropSelf(ModBlocks.ASHEN_BUTTON.get());
        this.dropSelf(ModBlocks.ASHEN_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.ASHEN_TRAPDOOR.get());
        this.dropSelf(ModBlocks.ASHEN_FENCE.get());
        this.dropSelf(ModBlocks.ASHEN_FENCE_GATE.get());

        this.add(ModBlocks.ASHEN_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.ASHEN_SLAB.get()));
        this.add(ModBlocks.ASHEN_DOOR.get(),
                block -> createDoorTable(ModBlocks.ASHEN_DOOR.get()));

        this.dropSelf(ModBlocks.INFUSED_WOOL.get());
        this.dropSelf(ModBlocks.INFUSED_WOOL.get());

        LootItemCondition.Builder lootitemcondition$builder = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.STRAWBERRY_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StrawberryCropBlock.AGE,5));
        this.add(ModBlocks.STRAWBERRY_CROP.get(), createCropDrops(ModBlocks.STRAWBERRY_CROP.get(), ModItems.STRAWBERRY.get(),
                ModItems.STRAWBERRY_SEEDS.get(), lootitemcondition$builder));


        this.dropSelf(ModBlocks.MANA_FLOWER.get());
        this.add(ModBlocks.POTTED_MANA_FLOWER.get(), createPotFlowerItemTable(ModBlocks.MANA_FLOWER.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
