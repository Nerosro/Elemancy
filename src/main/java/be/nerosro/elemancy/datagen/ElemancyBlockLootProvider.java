package be.nerosro.elemancy.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ElemancyBlockLootProvider extends LootTableProvider {

    public ElemancyBlockLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
            new SubProviderEntry(ElemancyBlockLoot::new, net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class ElemancyBlockLoot extends BlockLootSubProvider {

        private static final float[] ASHEN_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

        protected ElemancyBlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            // All wood blocks drop themselves
            dropSelf(ElemancyBlocks.ASHEN_LOG.get());
            dropSelf(ElemancyBlocks.ASHEN_WOOD.get());
            dropSelf(ElemancyBlocks.STRIPPED_ASHEN_LOG.get());
            dropSelf(ElemancyBlocks.STRIPPED_ASHEN_WOOD.get());
            dropSelf(ElemancyBlocks.ASHEN_PLANKS.get());

            // Wood derivatives
            dropSelf(ElemancyBlocks.ASHEN_STAIRS.get());
            add(ElemancyBlocks.ASHEN_SLAB.get(), createSlabItemTable(ElemancyBlocks.ASHEN_SLAB.get()));
            dropSelf(ElemancyBlocks.ASHEN_FENCE.get());
            dropSelf(ElemancyBlocks.ASHEN_FENCE_GATE.get());
            add(ElemancyBlocks.ASHEN_DOOR.get(), createDoorTable(ElemancyBlocks.ASHEN_DOOR.get()));
            dropSelf(ElemancyBlocks.ASHEN_TRAPDOOR.get());
            dropSelf(ElemancyBlocks.ASHEN_PRESSURE_PLATE.get());
            dropSelf(ElemancyBlocks.ASHEN_BUTTON.get());

            // Paradox Flower never drops as an item
            add(ElemancyBlocks.PARADOX_FLOWER.get(), noDrop());

            // Infused metal
            dropSelf(ElemancyBlocks.INFUSED_METAL_BLOCK.get());

            // Elemetal
            ElemancyBlocks.getElemetalBlocksByKey().values().forEach(block -> dropSelf(block.get()));

            // Infused wool
            dropSelf(ElemancyBlocks.INFUSED_WOOL.get());

            // Mirror (drops from lower half only, like a door)
            add(ElemancyBlocks.MIRROR.get(), createDoorTable(ElemancyBlocks.MIRROR.get()));

            // Sapling drops itself
            dropSelf(ElemancyBlocks.ASHEN_SAPLING.get());

            // Leaves: drop sapling with chance, ashen sticks instead of regular sticks
            HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            add(ElemancyBlocks.ASHEN_LEAVES.get(),
                createSilkTouchOrShearsDispatchTable(
                    ElemancyBlocks.ASHEN_LEAVES.get(),
                    ((LootPoolSingletonContainer.Builder<?>) applyExplosionCondition(
                        ElemancyBlocks.ASHEN_LEAVES.get(),
                        LootItem.lootTableItem(ElemancyBlocks.ASHEN_SAPLING.get())
                    )).when(BonusLevelTableCondition.bonusLevelFlatChance(
                        enchantments.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_SAPLING_CHANCES))
                ).withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(hasShears().or(hasSilkTouch()).invert())
                        .add(((LootPoolSingletonContainer.Builder<?>) applyExplosionDecay(
                            ElemancyBlocks.ASHEN_LEAVES.get(),
                            LootItem.lootTableItem(ElemancyItems.ASHEN_STICK.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        )).when(BonusLevelTableCondition.bonusLevelFlatChance(
                            enchantments.getOrThrow(Enchantments.FORTUNE), ASHEN_LEAVES_STICK_CHANCES)))
                ));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            List<Block> blocks = new ArrayList<>(List.of(
                ElemancyBlocks.ASHEN_LOG.get(),
                ElemancyBlocks.ASHEN_WOOD.get(),
                ElemancyBlocks.STRIPPED_ASHEN_LOG.get(),
                ElemancyBlocks.STRIPPED_ASHEN_WOOD.get(),
                ElemancyBlocks.ASHEN_PLANKS.get(),
                ElemancyBlocks.ASHEN_STAIRS.get(),
                ElemancyBlocks.ASHEN_SLAB.get(),
                ElemancyBlocks.ASHEN_FENCE.get(),
                ElemancyBlocks.ASHEN_FENCE_GATE.get(),
                ElemancyBlocks.ASHEN_DOOR.get(),
                ElemancyBlocks.ASHEN_TRAPDOOR.get(),
                ElemancyBlocks.ASHEN_PRESSURE_PLATE.get(),
                ElemancyBlocks.ASHEN_BUTTON.get(),
                ElemancyBlocks.PARADOX_FLOWER.get(),
                ElemancyBlocks.ASHEN_SAPLING.get(),
                ElemancyBlocks.ASHEN_LEAVES.get(),
                ElemancyBlocks.INFUSED_METAL_BLOCK.get(),
                ElemancyBlocks.INFUSED_WOOL.get(),
                ElemancyBlocks.MIRROR.get()
            ));
            ElemancyBlocks.getElemetalBlocksByKey().values().forEach(block -> blocks.add(block.get()));
            return blocks;
        }
    }
}
