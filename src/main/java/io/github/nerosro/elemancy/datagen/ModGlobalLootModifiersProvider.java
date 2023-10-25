package io.github.nerosro.elemancy.datagen;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.item.ModItems;
import io.github.nerosro.elemancy.loot.AddItemModifier;
import io.github.nerosro.elemancy.loot.AddSuspiciousSandItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

/**
 * Created by Nerosro on 12/10/2023.
 */
public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, Elemancy.MOD_ID);
    }

    @Override
    protected void start() {
        add("pine_cone_from_spruce_leaves", new AddItemModifier(new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPRUCE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.35f).build()
        }, ModItems.PINE_CONE.get()));

        add("icecream_from_snowman", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(new ResourceLocation("entities/snow_golem")).build()
        }, ModItems.ICECREAM_COCOA.get()));

        add("infused_wool_from_weaponsmith", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(new ResourceLocation("chests/village/village_weaponsmith")).build()
        }, ModBlocks.INFUSED_WOOL.get().asItem()));

        add("infused_metal_from_suspicious_sand", new AddSuspiciousSandItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/desert_pyramid")).build()
        }, ModItems.INFUSED_METAL.get()));
    }
}
