package io.github.nerosro.elemancy.datagen;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Nerosro on 8/10/2023.
 */
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        /** ----------------
         * |SHAPED RECIPES|
         * ----------------
         * */

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ROBE_HELMET.get())
                .pattern("AAA")
                .pattern("A A")
                .define('A', ModBlocks.INFUSED_WOOL.get())
                .unlockedBy(getHasName(ModBlocks.INFUSED_WOOL.get()), has(ModBlocks.INFUSED_WOOL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ROBE_CHESTPLATE.get())
                .pattern("A A")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModBlocks.INFUSED_WOOL.get())
                .unlockedBy(getHasName(ModBlocks.INFUSED_WOOL.get()), has(ModBlocks.INFUSED_WOOL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ROBE_LEGGINGS.get())
                .pattern("AAA")
                .pattern("A A")
                .pattern("A A")
                .define('A', ModBlocks.INFUSED_WOOL.get())
                .unlockedBy(getHasName(ModBlocks.INFUSED_WOOL.get()), has(ModBlocks.INFUSED_WOOL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ROBE_BOOTS.get())
                .pattern("A A")
                .pattern("A A")
                .define('A', ModBlocks.INFUSED_WOOL.get())
                .unlockedBy(getHasName(ModBlocks.INFUSED_WOOL.get()), has(ModBlocks.INFUSED_WOOL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.ICECREAM_COCOA.get())
                .pattern(" A ")
                .pattern("BBB")
                .pattern(" C ")
                .define('A', Items.COCOA_BEANS)
                .define('B', Items.SNOWBALL)
                .define('C', Items.BOWL)
                .unlockedBy(getHasName(ModItems.ICECREAM_COCOA.get()), has(ModItems.ICECREAM_COCOA.get()))
                .save(pWriter);
        /**
         * -------------------
         * |SHAPELESS RECIPES|
         * -------------------
         * */

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.ENERGIZED_STICK.get(), 1)
                .requires(Items.STICK)
                .unlockedBy(getHasName(ModItems.ENERGIZED_STICK.get()), has(ModItems.ENERGIZED_STICK.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ASHEN_WOOD.get(), 4)
                .requires(ModBlocks.ASHEN_LOG.get())
                .unlockedBy(getHasName(ModBlocks.ASHEN_LOG.get()), has(ModBlocks.ASHEN_LOG.get()))
                .save(pWriter);
    }



    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, Elemancy.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }

    }
}
